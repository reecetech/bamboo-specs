package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.enums.SpecFileType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class BambooYamlFileModel extends DomainModel {
    @NotNull
    @NotEmpty
    public String specType;

    @NotNull
    @NotEmpty
    public String bambooServer;

    @JsonIgnore
    public SpecFileType getFileType() {
        return SpecFileType.fromString(this.specType);
    }
}
