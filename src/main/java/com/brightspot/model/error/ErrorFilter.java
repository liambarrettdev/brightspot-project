package com.brightspot.model.error;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brightspot.model.error.handler.ErrorHandler;
import com.brightspot.tool.CustomSiteSettings;
import com.psddev.cms.db.PageFilter;
import com.psddev.dari.util.AbstractFilter;

public class ErrorFilter extends AbstractFilter implements AbstractFilter.Auto {

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        Set<ErrorHandler> handlers = Optional.ofNullable(PageFilter.Static.getSite(request))
            .map(s -> CustomSiteSettings.get(s, CustomSiteSettings::getErrorHandlers))
            .orElse(Collections.emptySet());

        chain.doFilter(request, new ErrorResponseWrapper(request, response, handlers));
    }

    @Override
    public void updateDependencies(Class<? extends AbstractFilter> filterClass, List<Class<? extends Filter>> dependencies) {
        if (PageFilter.class.isAssignableFrom(filterClass)) {
            dependencies.add(getClass());
        }
    }
}
