package au.com.reece.de.bamboospecs.models.docker;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class VolumeMapping {
    @NotNull
    @NotEmpty
    public String local;

    @NotNull
    @NotEmpty
    public String container;
}
