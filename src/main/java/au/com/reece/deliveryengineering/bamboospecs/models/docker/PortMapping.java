package au.com.reece.deliveryengineering.bamboospecs.models.docker;

import javax.validation.constraints.NotNull;

public class PortMapping {
    @NotNull
    public Integer local;

    @NotNull
    public Integer container;
}
