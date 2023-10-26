package com.brightspot.tool.recalculation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.brightspot.task.recalculation.MethodRecalculationTask;
import com.google.common.base.Preconditions;
import com.psddev.dari.db.Database;
import com.psddev.dari.db.Query;
import com.psddev.dari.db.Record;

/**
 * Class for describing an indexed method recalculation event. Create one to recalculate the {{@code methodName}} for
 * all results of the {{@code query}}. Ingested then deleted by {{@link MethodRecalculationTask}}.
 */
public class MethodRecalculation extends Record {

    private Query<Object> query;

    private List<String> methodNames;

    @Indexed
    private String identifier;

    @Indexed
    private Date calculationSaveDate;

    public MethodRecalculation() {
    }

    public MethodRecalculation(Query<Object> query, List<String> methodNames, String identifier) {
        super();
        this.query = query;
        this.methodNames = methodNames;
        this.identifier = identifier;
        this.calculationSaveDate = new Date(Database.Static.getDefault().now());
    }

    public Query<Object> getQuery() {
        return query;
    }

    public void setQuery(Query<Object> query) {
        this.query = query;
    }

    public List<String> getMethodNames() {
        if (methodNames == null) {
            methodNames = new ArrayList<>();
        }
        return methodNames;
    }

    public void setMethodNames(List<String> methodNames) {
        this.methodNames = methodNames;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Date getCalculationSaveDate() {
        return calculationSaveDate;
    }

    public void setCalculationSaveDate(Date calculationSaveDate) {
        this.calculationSaveDate = calculationSaveDate;
    }

    // -- Static Methods --//

    public static void recalculateMethodByQuery(Query<Object> query, String methodName, String identifier) {
        recalculateMethodByQuery(query, Collections.singletonList(methodName), identifier);
    }

    public static void recalculateMethodByQuery(Query<Object> query, List<String> methodNames, String identifier) {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(methodNames);

        new MethodRecalculation(query, methodNames, identifier).saveImmediately();
    }
}
