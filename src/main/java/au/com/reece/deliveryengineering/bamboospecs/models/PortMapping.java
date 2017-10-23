package au.com.reece.deliveryengineering.bamboospecs.models;

import javax.validation.constraints.NotNull;

public class PortMapping {
    @NotNull
    public Integer local;

    @NotNull
    public Integer container;
}
