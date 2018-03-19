package au.com.reece.de.bamboospecs.models.enums;

import java.io.File;

public enum FileType {
    PERMISSIONS("permissions.yaml"),
    PLAN("plan.yaml"),
    DEPLOYMENT("deployment.yaml");

    private final String fileType;

    FileType(String s) {
        this.fileType = s;
    }

    public static FileType getFromFile(File file) {
        for (FileType type : FileType.values()) {
            if (type.fileType.equalsIgnoreCase(file.getName())) {
                return type;
            }
        }
        throw new IllegalArgumentException("File " + file.getName() + " is not a known specification type");
    }
}
