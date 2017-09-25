package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.builders.notification.PlanCompletedNotification;
import com.atlassian.bamboo.specs.util.Logger;

import javax.annotation.Nullable;

public class ReeceNotification {
    private static final Logger log = Logger.getLogger(ReeceNotification.class);
    private NotificationTrigger when;
    private String slack;

    public NotificationTrigger getWhen() {
        return when;
    }

    public void setWhen(NotificationTrigger when) {
        this.when = when;
    }

    public String getSlack() {
        return slack;
    }

    public void setSlack(String slack) {
        this.slack = slack;
    }

    @Nullable
    Notification forPlan() {
        if (this.when == null) {
            log.info("Missing 'when' value in yaml task: " + this.slack);
            return null;
        }
        switch (this.when) {
            case PLAN_COMPLETED:
                return new Notification()
                        .type(new PlanCompletedNotification())
                        .recipients(new AnyNotificationRecipient(
                                new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-slack:recipient.slack"))
                                .recipientString("https://hooks.slack.com/services/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|#cyborg-dev"));
            default:
                // shouldn't actually be possible, given we load via enum
                log.info("Unexpected 'when' value from yaml " + this.when);
                return null;
        }
    }
}
