package au.com.reece;

import java.util.List;

public class ReecePlan {
    private String bambooServer;
    private String projectKey;
    private String projectName;
    private String planKey;
    private String planName;

    public String getBambooServer() {
        return bambooServer;
    }

    public void setBambooServer(String bambooServer) {
        this.bambooServer = bambooServer;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPlanKey() {
        return planKey;
    }

    public void setPlanKey(String planKey) {
        this.planKey = planKey;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }
}
