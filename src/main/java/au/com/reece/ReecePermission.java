package au.com.reece;

import java.util.ArrayList;
import java.util.List;

public class ReecePermission {
    private List<String> projects = new ArrayList<>();
    private List<String> users = new ArrayList<>();
    private List<String> groups = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
