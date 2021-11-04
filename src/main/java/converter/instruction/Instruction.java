package converter.instruction;

import converter.Score;
import converter.ScoreComponent;
import utility.Patterns;
import utility.Range;
import utility.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Instruction extends ScoreComponent{
	protected boolean isTop;
    
    public static String LINE_PATTERN = getLinePattern();

    protected String content;
    protected int position;
    protected Range range;
    protected boolean hasBeenApplied;

    Instruction(String content, int position, boolean isTop) {
        this.content = content;
        this.position = position;
        this.isTop = isTop;
        int relStartPos = position - Score.tabText.substring(0,position).lastIndexOf("\n");
        int relEndPos = relStartPos + content.length() - 1;
        setRange(new Range(relStartPos, relEndPos));
        
    }

    public abstract <E extends ScoreComponent> void applyTo(E scoreComponent);

    

    String getContent() {
        return this.content;
    }
    int getPosition() {
        return this.position;
    }
    void setHasBeenApplied(boolean bool) {
        this.hasBeenApplied = bool;
    }
    boolean getHasBeenApplied() {
        return this.hasBeenApplied;
    }

	@Override
	public List<Range> getRanges() {
		List<Range> ranges = new ArrayList<>();
		ranges.add(new Range(position,position+content.length()));
		return ranges;
	}
	
    public List<ValidationError> validate() {
        
        if (!this.hasBeenApplied) {
            addError(
                    "This instruction could not be applied to any measure or note.",
                    3,
                    getRanges());
        }
        return errors;
    }

    private static String getLinePattern() {
        String instruction = "(("+TimeSignature.PATTERN+")|("+Repeat.PATTERN+"))";
        return "("+Patterns.WHITESPACE+"*" + instruction + Patterns.WHITESPACE+"*" + ")+";
    }

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}
}


