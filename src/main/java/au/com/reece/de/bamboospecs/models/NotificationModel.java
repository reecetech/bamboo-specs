/*
 * Copyright 2019 Reece Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public Boolean responsibleUser;

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
            case STATUS_CHANGED:
                type = new PlanStatusChangedNotification();
                break;
            case DEPLOYMENT_FAILED:
                type = new DeploymentFailedNotification();
                break;
            case DEPLOYMENT_FINISHED:
                type = new DeploymentFinishedNotification();
                break;
            case DEPLOYMENT_STARTED_AND_FINISHED:
                type = new DeploymentStartedAndFinishedNotification();
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

        if (this.responsibleUser != null && this.responsibleUser) {
            recipients.add(new ResponsibleRecipient());
        }

        if (recipients.size() == 0) {
            throw new RuntimeException("No recipients defined for " + this.when);
        }

        return notification.recipients(recipients.toArray(new NotificationRecipient[]{}));
    }
}
