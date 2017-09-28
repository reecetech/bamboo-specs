package au.com.reece.deliveryengineering.bamboospecs.models;

import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public class PermissionModel extends DomainModel {
    @NotNull
    @NotEmpty
    public Set<String> projects;

    public Set<String> groups;
    public Set<String> users;

    public Set<String> getUsers() {
        return users == null ? new HashSet<>() : users;
    }

    public Set<String> getGroups() {
        return groups == null ? new HashSet<>() : groups;
    }

    @NotNull
    @NotEmpty
    public Set<PermissionType> grant;
}
