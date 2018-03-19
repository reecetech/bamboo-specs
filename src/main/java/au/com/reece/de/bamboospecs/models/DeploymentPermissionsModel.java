package au.com.reece.de.bamboospecs.models;

import javax.validation.constraints.NotNull;

public class DeploymentPermissionsModel {
    @NotNull
    public PermissionModel project;

    @NotNull
    public PermissionModel environment;
}
