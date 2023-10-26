package com.brightspot.utils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.CronExpression;

public final class CronUtils {

    private static final CronDefinition DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

    private CronUtils() {
    }

    public static DateTime getNextExecutionTime(String cronExpression) {
        Cron cron = parseCronExpression(cronExpression);
        if (cron == null) {
            return null;
        }

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime nextExecutionTime = executionTime.nextExecution(ZonedDateTime.now()).orElse(null);

        return nextExecutionTime != null ? new DateTime((nextExecutionTime.toInstant().toEpochMilli())) : null;
    }

    public static DateTime getPreviousExecutionTime(String cronExpression) {
        Cron cron = parseCronExpression(cronExpression);
        if (cron == null) {
            return null;
        }

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime previousExecutionTime = executionTime.lastExecution(ZonedDateTime.now()).orElse(null);

        return previousExecutionTime != null ? new DateTime((previousExecutionTime.toInstant().toEpochMilli())) : null;
    }

    public static String getCronDescription(String cronExpression) {
        Instant now = Instant.now();
        Date dateNow = Date.from(now);

        if (StringUtils.isNotBlank(cronExpression)) {
            cronExpression = cronExpression.trim();

            StringBuilder builder = new StringBuilder();
            try {
                CronExpression cron = new CronExpression(cronExpression);

                // add run frequency
                try {
                    String frequencyLabel = getFrequencyLabel(cronExpression);
                    if (StringUtils.isNotBlank(frequencyLabel)) {
                        builder.append("Runs: ");
                        builder.append(frequencyLabel);
                    }
                } catch (Exception e) {
                    builder.append(cronExpression);
                }

                // add next run date
                builder.append(" | Next run: ");
                Date nextValidDate = Optional.ofNullable(getNextExecutionTime(cronExpression))
                    .map(DateTime::toDate)
                    .orElse(null);

                if (nextValidDate == null) {
                    builder.append("Never");
                } else {
                    // if the next valid run time is resolved to be in the past, use now as a baseline
                    if (nextValidDate.before(dateNow)) {
                        nextValidDate = cron.getNextValidTimeAfter(dateNow);
                    }

                    builder.append(LocalizationUtils.currentUserDate(nextValidDate));
                }

                return builder.toString();
            } catch (Exception e) {
                return "Invalid cron expression";
            }
        }

        return null;
    }

    private static String getFrequencyLabel(String cronExpression) {
        Cron cron = parseCronExpression(cronExpression);

        return cron != null ? CronDescriptor.instance().describe(cron) : "";
    }

    private static Cron parseCronExpression(String cronExpression) {
        if (StringUtils.isBlank(cronExpression)) {
            return null;
        }
        return new CronParser(DEFINITION).parse(cronExpression.trim());
    }
}
