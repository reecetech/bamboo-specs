package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReecePermission {
    private static final Logger log = Logger.getLogger(ReeceTask.class);
    public List<String> projects = new ArrayList<>();
    public List<String> users = new ArrayList<>();
    public List<String> groups = new ArrayList<>();
    public List<String> permissions = new ArrayList<>();

    public boolean addPermissions(HashMap<String, ReecePlanPermissions> rpp) {
        boolean ok = true;
        for (String idString: this.projects) {
            if (!rpp.containsKey(idString)) {
                rpp.put(idString, new ReecePlanPermissions());
            }
            ReecePlanPermissions perms = rpp.get(idString);

            List<PermissionType> values = new ArrayList<>();
            for (String perm : this.permissions) {
                values.add(PermissionType.valueOf(perm));
            }
            PermissionType[] types = values.toArray(new PermissionType[values.size()]);

            for (String user: this.users) {
                if (perms.users.containsKey(user)) {
                    log.info("Duplicate user %s found for project %s", user, idString);
                    ok = false;
                }
                perms.users.put(user, types);
            }

            for (String group: this.groups) {
                if (perms.groups.containsKey(group)) {
                    log.info("Duplicate group %s found for project %s", group, idString);
                    ok = false;
                }
                perms.groups.put(group, types);
            }
        }

        return ok;
    }
}
