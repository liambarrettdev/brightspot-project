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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Recordable.DisplayName("YouTube")
@ViewBinding(value = YouTubeVideoPlayerViewModel.class, types = YouTubeVideoSource.VIEW_TYPE)
public class YouTubeVideoSource extends ExternalVideoSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeVideoSource.class);

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
                // Identifier is not a valid URI, treating it as a video ID
                LOGGER.debug("Identifier '{}' is not a valid URI, treating as YouTube video ID: {}",
                    identifier, e.getMessage());
            }

            if (uri != null && uri.getScheme() != null) {
                videoUrl = identifier; // Default YouTube URL
                if (isShortUrl(uri)) {
                    try {
                        String path = uri.getPath();
                        if (path != null && !path.isEmpty()) {
                            String[] components = path.split("/");
                            String uniqueId = components[components.length - 1]; // Last piece of the URL
                            videoUrl = String.format(DEFAULT_URL_FORMAT, uniqueId); // Shortened YouTube URL
                            LOGGER.debug("Converted YouTube short URL to standard format: {}", videoUrl);
                        } else {
                            LOGGER.warn("YouTube short URL '{}' has no path component", identifier);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to parse YouTube short URL '{}': {}", identifier, e.getMessage(), e);
                        videoUrl = identifier; // Fall back to original URL
                    }
                }
            } else {
                videoUrl = String.format(DEFAULT_URL_FORMAT, identifier); // Just the YouTube video's ID
                LOGGER.debug("Generated YouTube URL from video ID '{}': {}", identifier, videoUrl);
            }
        }

        return videoUrl;
    }

    // -- Utility Methods -- //

    private Boolean isShortUrl(URI uri) {
        if (uri == null || uri.getHost() == null) {
            return false;
        }
        return YOU_TUBE_SHORT_URL_HOSTS.stream().anyMatch(shortUrl -> uri.getHost().contains(shortUrl));
    }
}
