package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationError {
    int priority;
    List<Range> positions = new ArrayList<>();
    String message;

    public ValidationError(String message, int priority, List<Range> positions) {
        this.message = message;
        this.priority = priority;
        this.positions = positions;
//        for (Integer[] position : positions)
//            this.positions.add(Arrays.copyOf(position, position.length));
    }
    
    public int getPriority() {
    	return this.priority;
    }
    
    public List<Range> getPositions() {
//    	List<Integer[]> positions= new ArrayList<>();
//    	for (Integer[] position : this.positions)
//            positions.add(Arrays.copyOf(position, position.length));
    	return positions;
    }
    
    public String getMessage() {
    	return this.message;
    }
}
