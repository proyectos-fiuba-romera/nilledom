package test.mdauml.persistence.classes;

import com.nilledom.model.StepType;

/**
 * Created by ferromera on 08/12/15.
 */
public class EnumContainer {
    private StepType type;

    public EnumContainer(StepType regular) {
        type = regular;
    }

    public StepType getType() {
        return type;
    }
}
