package au.com.reece.de.bamboospecs.models.enums;

import javax.validation.constraints.NotNull;

public enum FileType {
    DEPLOYMENT("deployment"),
    DEPLOY_INCLUDE("deployInclude"),
    BUILD("build"),
    BUILD_INCLUDE("buildInclude");

    private final String fileTypeValue;

    FileType(String fileTypeValue) {
        this.fileTypeValue = fileTypeValue;
    }

    public static FileType fromString(@NotNull String type) {
        for (FileType fileType : FileType.values()) {
            if (fileType.fileTypeValue.equalsIgnoreCase(type)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("The type of spec file, " + type + ", is unknown");
    }
}
