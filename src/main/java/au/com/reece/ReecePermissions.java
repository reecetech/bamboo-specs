package au.com.reece;

import java.util.List;

public class ReecePermissions {
    private String bambooServer;
    private List<ReecePermission> permissions;

    public String getBambooServer() {
        return bambooServer;
    }

    public void setBambooServer(String bambooServer) {
        this.bambooServer = bambooServer;
    }

    public List<ReecePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<ReecePermission> permissions) {
        this.permissions = permissions;
    }
}
