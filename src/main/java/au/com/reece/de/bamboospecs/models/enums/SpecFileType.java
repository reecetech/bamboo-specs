package au.com.reece.de.bamboospecs.models.enums;

import javax.validation.constraints.NotNull;

public enum SpecFileType {
    DEPLOYMENT("deployment"),
    DEPLOY_INCLUDE("deployInclude"),
    PLAN("plan"),
    PLAN_INCLUDE("planInclude");

    private final String fileTypeValue;

    SpecFileType(String fileTypeValue) {
        this.fileTypeValue = fileTypeValue;
    }

    public static SpecFileType fromString(@NotNull String type) {
        for (SpecFileType specFileType : SpecFileType.values()) {
            if (specFileType.fileTypeValue.equalsIgnoreCase(type)) {
                return specFileType;
            }
        }
        throw new IllegalArgumentException("The specType of spec file, " + type + ", is unknown");
    }
}
