package com.brightspot.model.audio.metadata;

import com.brightspot.model.audio.provider.AudioSource;
import com.brightspot.model.timed.TimedContentMetadata;

public interface AudioMetadata extends TimedContentMetadata {

    AudioSource getSource();
}
