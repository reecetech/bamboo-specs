package au.com.reece.de.bamboospecs.models;

import au.com.reece.de.bamboospecs.models.enums.TriggerType;
import com.atlassian.bamboo.specs.api.builders.trigger.Trigger;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;
import com.atlassian.bamboo.specs.builders.trigger.ScheduledTrigger;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TriggerModel {
    @NotNull
    public TriggerType type;

    @NotNull
    @NotEmpty
    public String description;

    public String everyNumHours;
    public String dailyAt;
    public String weeklyAt;
    public String monthlyAt;
    public String cron;
    public String branch;


    static private LocalTime parseTimeNicely(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
    }

    public Trigger asTrigger() {
        switch (this.type) {
            case AFTER_SUCCESSFUL_BUILD_PLAN:
                AfterSuccessfulBuildPlanTrigger buildTrigger = new AfterSuccessfulBuildPlanTrigger();
                buildTrigger.description(this.description);
                if (this.branch != null) {
                    buildTrigger.triggerByBranch(this.branch);
                }
                return buildTrigger;
            case AFTER_STASH_COMMIT:
                return new BitbucketServerTrigger().description(this.description);
            case SCHEDULED:
                ScheduledTrigger scheduledTrigger = new ScheduledTrigger().description(this.description);
                if (this.everyNumHours != null) {
                    scheduledTrigger.scheduleEvery(Integer.getInteger(this.everyNumHours), TimeUnit.HOURS);
                }
                if (this.dailyAt != null) {
                    scheduledTrigger.scheduleOnceDaily(parseTimeNicely(dailyAt));
                }
                if (this.weeklyAt != null) {
                    String[] parts = this.weeklyAt.split(" ");
                    String upperDay = parts[0].toUpperCase();
                    scheduledTrigger.scheduleWeekly(parseTimeNicely(parts[1]), DayOfWeek.valueOf(upperDay));
                }
                if (this.monthlyAt != null) {
                    String[] parts = this.monthlyAt.split(" ");
                    scheduledTrigger.scheduleMonthly(parseTimeNicely(parts[1]), Integer.getInteger(parts[0]));
                }
                if (this.cron != null) {
                    scheduledTrigger.cronExpression(this.cron);
                }
                return scheduledTrigger;
            default:
                throw new RuntimeException("Unexpected 'type' value from yaml " + this.type);
        }
    }
}
