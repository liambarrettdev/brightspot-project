package com.brightspot.model.iframe;

import java.util.Optional;

import com.brightspot.model.AbstractViewModel;
import com.brightspot.view.model.iframe.IframeModuleView;
import org.apache.commons.lang3.StringUtils;

public class IframeModuleViewModel extends AbstractViewModel<IframeModule> implements IframeModuleView {

    private static final String HTTP_SECURE = "https:";
    private static final String HTTP = "http:";

    @Override
    public Object getUrl() {
        // Return a URL of the form [//www.google.com] instead of [http://www.google.com],
        // so the iframe matches the protocol of the rest of the page
        return Optional.ofNullable(model.getUrl())
            .map(url -> {
                if (url.startsWith(HTTP_SECURE)) {
                    return StringUtils.removeStart(url, HTTP_SECURE);
                } else if (url.startsWith(HTTP)) {
                    return StringUtils.removeStart(url, HTTP);
                } else {
                    return url;
                }
            })
            .orElse(null);
    }

    @Override
    public Number getHeight() {
        return model.getHeight();
    }

    @Override
    public Number getWidth() {
        return model.getWidth();
    }

    @Override
    public Object getName() {
        return model.getName();
    }

    @Override
    public Object getSandbox() {
        return model.getSandbox();
    }
}
