package au.com.reece.deliveryengineering.bamboospecs.models;

import au.com.reece.deliveryengineering.bamboospecs.models.enums.NotificationTrigger;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.builders.notification.PlanCompletedNotification;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class NotificationModel extends DomainModel {
    @NotNull
    @NotEmpty
    public NotificationTrigger when;

    @NotNull
    @NotEmpty
    public String slack;

    public Notification forPlan() {
        switch (this.when) {
            case PLAN_COMPLETED:
                return new Notification()
                        .type(new PlanCompletedNotification())
                        .recipients(new AnyNotificationRecipient(
                                new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-slack:recipient.slack"))
                                .recipientString("https://hooks.slack.com/services/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|#cyborg-dev"));
            default:
                // shouldn't actually be possible, given we load via enum
                throw new RuntimeException("Unexpected 'when' value from yaml " + this.when);
        }
    }
}
