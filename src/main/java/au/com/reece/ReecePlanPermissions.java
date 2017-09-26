package au.com.reece;


import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;

import java.util.HashMap;

public class ReecePlanPermissions {
    public HashMap<String, PermissionType[]> groups = new HashMap<>();
    public HashMap<String, PermissionType[]> users = new HashMap<>();
}
