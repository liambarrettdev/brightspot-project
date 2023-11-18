package com.brightspot.model.video.provider.vimeo;

import com.brightspot.model.video.provider.ExternalVideoSource;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Vimeo")
@ViewBinding(value = VimeoVideoPlayerViewModel.class, types = VimeoVideoSource.VIEW_TYPE)
public class VimeoVideoSource extends ExternalVideoSource {

    protected static final String VIEW_TYPE = "vimeo-video";

    private static final String DEFAULT_URL_FORMAT = "https://vimeo.com/%s";

    @Override
    protected String getVideoUrlFormat() {
        return DEFAULT_URL_FORMAT;
    }

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
