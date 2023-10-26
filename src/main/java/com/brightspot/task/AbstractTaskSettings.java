package com.brightspot.task;

import com.brightspot.utils.CronUtils;
import com.psddev.cms.db.ToolUi;
import com.psddev.dari.db.Record;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;

@Recordable.Embedded
public abstract class AbstractTaskSettings extends Record {

    public static final String TAB_ADVANCED = "Advanced";

    public abstract String getCronExpressionFallback();

    public abstract void triggerTask();

    private Boolean enabled;

    @ToolUi.NoteHtml("<span data-dynamic-html='${content.getCronExpressionNote()}'</span>")
    @ToolUi.Placeholder(dynamicText = "${content.getCronExpressionFallback()}")
    private String cronExpression;

    @ToolUi.Tab(TAB_ADVANCED)
    private Boolean runImmediately;

    public Boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCronExpression() {
        return ObjectUtils.firstNonBlank(cronExpression, getCronExpressionFallback());
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Boolean isRunImmediately() {
        return Boolean.TRUE.equals(runImmediately);
    }

    public void setRunImmediately(Boolean runImmediately) {
        this.runImmediately = runImmediately;
    }

    // -- Overrides -- //

    @Override
    protected void beforeCommit() {
        if (isRunImmediately()) {
            setRunImmediately(false);
            triggerTask();
        }
        super.beforeCommit();
    }

    // -- Helper Methods -- //

    public String getCronExpressionNote() {
        return CronUtils.getCronDescription(getCronExpression());
    }
}
