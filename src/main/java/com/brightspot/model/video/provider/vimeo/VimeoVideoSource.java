package com.brightspot.model.video.provider.vimeo;

import java.util.Optional;

import com.brightspot.model.video.provider.ExternalVideoSource;
import com.brightspot.utils.OEmbedUtils;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;

@Recordable.DisplayName("Vimeo")
@ViewBinding(value = VimeoVideoPlayerViewModel.class, types = VimeoVideoSource.VIEW_TYPE)
public class VimeoVideoSource extends ExternalVideoSource {

    protected static final String VIEW_TYPE = "vimeo-video";

    private static final String DEFAULT_URL_FORMAT = "https://vimeo.com/%s";

    // -- Overrides -- //

    @Override
    public Long getVideoDuration() {
        return Optional.ofNullable(getExternalMetadata())
            .map(OEmbedUtils::getDuration)
            .map(Integer::longValue)
            .orElse(null);
    }

    @Override
    protected String getVideoUrlFormat() {
        return DEFAULT_URL_FORMAT;
    }

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }
}
