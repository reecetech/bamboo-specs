package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.enums.FileType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BambooYamlFileModel extends DomainModel {
    @NotNull
    public String type;

    @NotNull
    @NotEmpty
    public String bambooServer;

    public FileType getFileType() {
        return FileType.fromString(this.type);
    }
}
