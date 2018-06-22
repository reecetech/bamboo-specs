package au.com.reece.de.bamboospecs.models.deployment;

import au.com.reece.de.bamboospecs.models.PermissionModel;

import javax.validation.constraints.NotNull;

public class DeploymentPermissionsModel {
    @NotNull
    public PermissionModel project;

    @NotNull
    public PermissionModel environment;
}
