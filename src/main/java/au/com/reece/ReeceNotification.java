package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.builders.notification.PlanCompletedNotification;

public class ReeceNotification {
    private String when;
    private String slack;

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getSlack() {
        return slack;
    }

    public void setSlack(String slack) {
        this.slack = slack;
    }

    Notification forPlan() {
        return new Notification()
            .type(new PlanCompletedNotification())
            .recipients(new AnyNotificationRecipient(
                new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-slack:recipient.slack"))
                .recipientString("https://hooks.slack.com/services/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|#cyborg-dev"));

    }
}
