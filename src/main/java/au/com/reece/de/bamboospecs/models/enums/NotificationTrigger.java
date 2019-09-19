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

package au.com.reece.de.bamboospecs.models.enums;

public enum NotificationTrigger {
    PLAN_COMPLETED,
    PLAN_FAILED,
    STATUS_CHANGED,
    DEPLOYMENT_FAILED,
    DEPLOYMENT_FINISHED,
    DEPLOYMENT_STARTED_AND_FINISHED
}
