package au.com.reece.deliveryengineering.bamboospecs.models;

import javax.validation.constraints.NotNull;

public class DeploymentPermissionsModel {
    @NotNull
    public PermissionModel project;

    @NotNull
    public PermissionModel environment;
}
