package com.brightspot.model.video.provider.youtube;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.brightspot.model.video.provider.ExternalVideoSource;
import com.psddev.cms.view.ViewBinding;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.CollectionUtils;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Recordable.DisplayName("YouTube")
@ViewBinding(value = YouTubeVideoPlayerViewModel.class, types = YouTubeVideoSource.VIEW_TYPE)
public class YouTubeVideoSource extends ExternalVideoSource {

    protected static final String VIEW_TYPE = "youtube-video";

    private static final String KEY_DURATION = "duration_seconds";
    private static final String DEFAULT_URL_FORMAT = "https://www.youtube.com/watch?v=%s";
    private static final List<String> YOU_TUBE_SHORT_URL_HOSTS = Arrays.asList("www.youtu.be", "youtu.be");

    // -- Overrides -- //

    @Override
    protected String getVideoUrlFormat() {
        return DEFAULT_URL_FORMAT;
    }

    @Override
    public String getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Long getVideoDuration() {
        return Optional.ofNullable(getExternalMetadata())
            .map(metadata -> CollectionUtils.getByPath(metadata, KEY_DURATION))
            .map(duration -> ObjectUtils.to(Long.class, duration))
            .orElse(null);
    }

    @Override
    public String getVideoUrl() {
        String videoUrl = null;
        String identifier = getVideoId();

        if (!StringUtils.isBlank(identifier)) {
            identifier = identifier.trim();

            URI uri = null;
            try {
                uri = URI.create(identifier);
            } catch (RuntimeException e) {
                // ignore
            }

            if (uri != null && uri.getScheme() != null) {
                videoUrl = identifier; // Default YouTube URL
                if (isShortUrl(uri)) {
                    String[] components = uri.getPath().split("/");
                    String uniqueId = components[components.length - 1]; // Last piece of the URL
                    videoUrl = String.format(DEFAULT_URL_FORMAT, uniqueId); // Shortened YouTube URL
                }
            } else {
                videoUrl = String.format(DEFAULT_URL_FORMAT, identifier); // Just the YouTube video's ID
            }
        }

        return videoUrl;
    }

    // -- Utility Methods -- //

    private Boolean isShortUrl(URI uri) {
        return YOU_TUBE_SHORT_URL_HOSTS.stream().anyMatch(shortUrl -> uri.getHost().contains(shortUrl));
    }
}
