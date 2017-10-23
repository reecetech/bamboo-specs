package au.com.reece.deliveryengineering.bamboospecs.models;

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
