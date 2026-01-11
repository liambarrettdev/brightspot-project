package com.brightspot.utils;

import java.text.ParseException;
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

/**
 * Utility class for working with cron expressions.
 *
 * <p>This class provides methods to parse and work with Quartz-style cron expressions,
 * including calculating next/previous execution times and generating human-readable
 * descriptions of cron schedules.</p>
 *
 * <p>All methods use the Quartz cron definition format and return {@code null} if
 * the cron expression is invalid or cannot be parsed.</p>
 */
public final class CronUtils {

    private static final CronDefinition DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

    private CronUtils() {
    }

    /**
     * Calculates the next execution time for a cron expression from the current time.
     *
     * <p>Parses the cron expression and calculates when it will next execute. Returns
     * {@code null} if the cron expression is invalid or cannot be parsed.</p>
     *
     * @param cronExpression the Quartz-style cron expression
     * @return the next execution time as a DateTime, or {@code null} if invalid
     */
    public static DateTime getNextExecutionTime(String cronExpression) {
        Cron cron = parseCronExpression(cronExpression);
        if (cron == null) {
            return null;
        }

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime nextExecutionTime = executionTime.nextExecution(ZonedDateTime.now()).orElse(null);

        return nextExecutionTime != null ? new DateTime((nextExecutionTime.toInstant().toEpochMilli())) : null;
    }

    /**
     * Calculates the previous execution time for a cron expression from the current time.
     *
     * <p>Parses the cron expression and calculates when it last executed. Returns
     * {@code null} if the cron expression is invalid or cannot be parsed.</p>
     *
     * @param cronExpression the Quartz-style cron expression
     * @return the previous execution time as a DateTime, or {@code null} if invalid
     */
    public static DateTime getPreviousExecutionTime(String cronExpression) {
        Cron cron = parseCronExpression(cronExpression);
        if (cron == null) {
            return null;
        }

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime previousExecutionTime = executionTime.lastExecution(ZonedDateTime.now()).orElse(null);

        return previousExecutionTime != null ? new DateTime((previousExecutionTime.toInstant().toEpochMilli())) : null;
    }

    /**
     * Generates a human-readable description of a cron expression.
     *
     * <p>Creates a description that includes the run frequency and the next execution time.
     * The description format is: "Runs: [frequency] | Next run: [date]"</p>
     *
     * <p>If the cron expression is invalid, returns "Invalid cron expression". If the
     * expression is blank, returns {@code null}.</p>
     *
     * <p>TODO: Consider using a more robust date formatting library for better localization</p>
     * <p>FIXME: The method uses both CronUtils (cron-utils library) and Quartz CronExpression which may cause inconsistencies - consider standardizing on one library</p>
     *
     * @param cronExpression the Quartz-style cron expression
     * @return a human-readable description, "Invalid cron expression" if invalid, or {@code null} if blank
     */
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
            } catch (ParseException e) {
                return "Invalid cron expression";
            }
        }

        return null;
    }

    /**
     * Gets a human-readable frequency label for a cron expression.
     *
     * <p>Uses the cron-utils library to generate a natural language description of
     * when the cron expression will run (e.g., "every day at 3:00 AM").</p>
     *
     * @param cronExpression the Quartz-style cron expression
     * @return the frequency description, or empty string if invalid
     */
    private static String getFrequencyLabel(String cronExpression) {
        Cron cron = parseCronExpression(cronExpression);

        return cron != null ? CronDescriptor.instance().describe(cron) : "";
    }

    /**
     * Parses a cron expression string into a Cron object.
     *
     * <p>Returns {@code null} if the expression is blank or cannot be parsed.</p>
     *
     * @param cronExpression the Quartz-style cron expression to parse
     * @return the parsed Cron object, or {@code null} if blank or invalid
     */
    private static Cron parseCronExpression(String cronExpression) {
        if (StringUtils.isBlank(cronExpression)) {
            return null;
        }
        return new CronParser(DEFINITION).parse(cronExpression.trim());
    }
}
