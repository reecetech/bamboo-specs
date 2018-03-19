package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.enums.NotificationTrigger;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.notification.AnyNotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.Notification;
import com.atlassian.bamboo.specs.api.builders.notification.NotificationRecipient;
import com.atlassian.bamboo.specs.api.builders.notification.NotificationType;
import com.atlassian.bamboo.specs.builders.notification.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class NotificationModel extends DomainModel {
    @NotNull
    public NotificationTrigger when;

    public String slack;

    public String[] recipientGroups;

    public String[] recipientUsers;

    public Notification asNotification() {
        Notification notification = new Notification();
        ArrayList<NotificationRecipient> recipients = new ArrayList<>();
        NotificationType type;

        switch (this.when) {
            case PLAN_COMPLETED:
                type = new PlanCompletedNotification();
                break;
            case PLAN_FAILED:
                type = new PlanFailedNotification();
                break;
            case DEPLOYMENT_FAILED:
                type = new DeploymentFailedNotification();
                break;
            case DEPLOYMENT_FINISHED:
                type = new DeploymentFinishedNotification();
                break;
            default:
                // shouldn't actually be possible, given we load via enum
                throw new RuntimeException("Unexpected 'when' value from yaml " + this.when);
        }

        notification.type(type);

        if (this.slack != null) {
            recipients.add(new AnyNotificationRecipient(
                new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-slack:recipient.slack"))
                .recipientString(slack));
        }

        if (this.recipientGroups != null) {
            for (String name : this.recipientGroups)
                recipients.add(new GroupRecipient(name));
        }

        if (this.recipientUsers != null) {
            for (String name : this.recipientUsers)
                recipients.add(new UserRecipient(name));
        }

        if (recipients.size() == 0) {
            throw new RuntimeException("No recipients defined for " + this.when);
        }

        return notification.recipients(recipients.toArray(new NotificationRecipient[]{}));
    }
}
