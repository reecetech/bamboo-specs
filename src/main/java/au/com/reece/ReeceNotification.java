package au.com.reece;

import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.builders.notification.PlanCompletedNotification;
import com.atlassian.bamboo.specs.util.Logger;

import javax.annotation.Nullable;

public class ReeceNotification extends CheckRequired {
    private static final Logger log = Logger.getLogger(ReeceNotification.class);
    @Required public NotificationTrigger when;
    @Required public String slack;

    @Nullable
    Notification forPlan() {
        if (!this.checkRequired()) return null;
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
