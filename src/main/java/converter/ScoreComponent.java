package converter;

import utility.ValidationError;
import java.util.List;

public interface ScoreComponent {
    List<ValidationError> validate();
}
