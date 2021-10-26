package GUI;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import converter.Converter;
import converter.Score;
import converter.measure.TabMeasure;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import utility.Range;
import utility.Settings;
import utility.ValidationError;

public class Highlighter {
    protected static TreeMap<Range, ValidationError> ACTIVE_ERRORS = new TreeMap<>();
    
    private int HOVER_DELAY = 30;
    private MainViewController mvc;
    private Converter converter;
    
    protected static ExecutorService executor = Executors.newSingleThreadExecutor();

    public Highlighter(MainViewController mvc, Converter conv) {
    	this.mvc = mvc;
    	this.converter = conv;
		
    	enableHighlighting();

		Popup popup = new Popup();
		Label popupMsg = new Label();
		popupMsg.setStyle(
				"-fx-background-color: black;" +
						"-fx-text-fill: white;" +
				"-fx-padding: 5;");
		popup.getContent().add(popupMsg);

		mvc.mainText.setMouseOverTextDelay(Duration.ofMillis(HOVER_DELAY));
		mvc.mainText.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
			if (Highlighter.ACTIVE_ERRORS.isEmpty()) return;
			int chIdx = e.getCharacterIndex();
			String message = Highlighter.getMessageOfCharAt(chIdx);
			if (message.isEmpty()) return;
			Point2D pos = e.getScreenPosition();
			popupMsg.setText(message);
			popup.show(mvc.mainText, pos.getX(), pos.getY() + 10);
		});
		mvc.mainText.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
			popup.hide();
		});
    }

    public Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = mvc.mainText.getText();

        Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        task.isDone();
        return task;
    }
    
    
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        mvc.mainText.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        converter.update();
        if (converter.hasNothing()){
        	mvc.saveMXLButton.setDisable(true);
        	mvc.previewButton.setDisable(true);
        	mvc.showMXLButton.setDisable(true);
        }
        else
        {
        	mvc.saveMXLButton.setDisable(false);
        	mvc.previewButton.setDisable(false);
        	mvc.showMXLButton.setDisable(false);
        }

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        ACTIVE_ERRORS = this.filterOverlappingRanges(this.createErrorRangeMap(converter.validate()));
        if (ACTIVE_ERRORS.isEmpty()) {
            spansBuilder.add(Collections.emptyList(), text.length());
            return spansBuilder.create();
        }

        int ERROR_SENSITIVITY = Settings.getInstance().errorSensitivity;
        ArrayList<Range> errorRanges = new ArrayList<>(ACTIVE_ERRORS.keySet());
        int lastErrorEnd = 0;
        for (Range range : errorRanges) {
            int errorPriority = ACTIVE_ERRORS.get(range).getPriority();
            if (ERROR_SENSITIVITY<errorPriority) continue;
            String styleClass = getErrorStyleClass(errorPriority);
            spansBuilder.add(Collections.emptyList(), range.getStart() - lastErrorEnd);
            spansBuilder.add(Collections.singleton(styleClass), range.getSize());
            lastErrorEnd = range.getEnd();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastErrorEnd);
        return spansBuilder.create();
    }

    private TreeMap<Range, ValidationError> filterOverlappingRanges(TreeMap<Range, ValidationError> errors) {
        Iterator<Range> errorRanges = new ArrayList<>(errors.keySet()).iterator();
        if (!errorRanges.hasNext()) return new TreeMap<>();
        Range currentRange = errorRanges.next();
        while (errorRanges.hasNext()) {
            Range nextRange = errorRanges.next();

            while (nextRange.overlaps(currentRange)) {
                int currentErrorPriority = errors.get(currentRange).getPriority();
                int nextErrorPriority = errors.get(nextRange).getPriority();
                if (currentErrorPriority>nextErrorPriority) {
                    errors.remove(currentRange);
                    break;
                } else {
                    errors.remove(nextRange);
                    if (!errorRanges.hasNext()) break;
                    nextRange = errorRanges.next();
                }
            }
            currentRange = nextRange;
        }
        return errors;
    }

    private TreeMap<Range, ValidationError> createErrorRangeMap(List<ValidationError> errors) {
        TreeMap<Range, ValidationError> errorMap = new TreeMap<>();
        for (ValidationError error : errors) {
            for (Integer[] range : error.getPositions())
                errorMap.put(new Range(range[0], range[1]), error);
        }
        return errorMap;
    }

    public static String getMessageOfCharAt(int index) {
        Comparator<Range> numInRangeComparator = (r1, r2) -> {
            if (r1.contains(r2.getStart()) || r2.contains(r1.getStart()))
                return 0;
            return r1.getStart()==r1.getEnd() ? r1.getStart()-r2.getStart() : r2.getStart()- r1.getStart();
        };

        List<Range> errorRanges = new ArrayList<>(ACTIVE_ERRORS.keySet());
        Collections.sort(errorRanges, numInRangeComparator);
        //over the top, just for efficiency. search the array of ranges "errorRanges" to find the range which contains(i.e includes) the number "index"
        int rangeIdx = Collections.binarySearch(errorRanges, new Range(index, index), numInRangeComparator);
        if (rangeIdx<0)
            return "";
        Range range = errorRanges.get(rangeIdx);
        return ACTIVE_ERRORS.get(range).getMessage();
    }

    private static String getErrorStyleClass(int priority) {
        switch (priority) {
            case 1: return "highPriorityError";
            case 2: return "mediumPriorityError";
            case 3: return "lowPriorityError";
            case 4: return "unimportantError";
            default:
                new Exception("TXT2XML: invalid validation error priority").printStackTrace();
                return "";
        }
    }

    public void enableHighlighting() {
        //Subscription cleanupWhenDone = 
    	mvc.mainText.multiPlainChanges()
                .successionEnds(Duration.ofMillis(350))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(mvc.mainText.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
    }

    public boolean goToMeasure(int measureCount) {
        TabMeasure measure = new Score(mvc.mainText.getText()).getMeasure(measureCount);
        if (measure==null) return false;
        List<Integer[]> linePositions = measure.getLinePositions();
        mvc.mainText.moveTo(linePositions.get(0)[0]);
        mvc.mainText.requestFollowCaret();
        mvc.mainText.requestFocus();
        return true;
    }
}

