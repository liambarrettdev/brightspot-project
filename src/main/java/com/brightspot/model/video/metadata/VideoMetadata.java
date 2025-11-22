package com.brightspot.model.video.metadata;

import com.brightspot.model.timed.TimedContentMetadata;
import com.brightspot.model.video.provider.VideoSource;

public interface VideoMetadata extends TimedContentMetadata {

    VideoSource getSource();
}
