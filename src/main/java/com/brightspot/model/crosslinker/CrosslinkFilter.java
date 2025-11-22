package com.brightspot.model.crosslinker;

import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psddev.cms.db.PageFilter;
import com.psddev.crosslinker.db.CrosslinkOperation;
import com.psddev.crosslinker.db.Crosslinkable;
import com.psddev.dari.util.AbstractFilter;
import com.psddev.dari.util.Settings;

public class CrosslinkFilter extends AbstractFilter implements AbstractFilter.Auto {

    // -- Overrides -- //

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {
        boolean isCachingEnabled = Settings.get(boolean.class, "crosslinker/isCachingEnabled");

        Object mainObject = PageFilter.Static.getMainObject(request);
        if (mainObject instanceof Crosslinkable) {
            if (isCachingEnabled) {
                CrosslinkOperation.Static.evaluateWithCache(mainObject);
            } else {
                CrosslinkOperation.Static.evaluate(mainObject);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void updateDependencies(
        Class<? extends AbstractFilter> filterClass,
        List<Class<? extends Filter>> dependencies) {
        if (PageFilter.class.isAssignableFrom(filterClass)) {
            dependencies.add(getClass());
        }
    }
}
