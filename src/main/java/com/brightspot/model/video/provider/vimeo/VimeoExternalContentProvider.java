package com.brightspot.model.video.provider.vimeo;

import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brightspot.utils.Utils;
import com.psddev.cms.db.ExternalContent;
import com.psddev.cms.db.ExternalContentProvider;
import com.psddev.dari.util.IoUtils;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.TypeReference;

public class VimeoExternalContentProvider implements ExternalContentProvider {

    private static final String OEMBED_ENDPOINT = "https://vimeo.com/api/oembed.json";
    private static final Pattern URL_PATTERN = Pattern.compile("(?i)https?://vimeo.com/([^?]+).*");

    private ExternalContent content;

    public ExternalContent getContent() {
        return content;
    }

    @Override
    public Map<String, Object> createResponse(ExternalContent content) {
        this.content = content;

        Matcher urlMatcher = URL_PATTERN.matcher(content.getUrl());
        if (!urlMatcher.matches()) {
            return null;
        }

        Map<String, Object> response;
        try {
            response = ObjectUtils.to(
                new TypeReference<Map<String, Object>>() { },
                ObjectUtils.fromJson(IoUtils.toString(
                    new URL(Utils.addQueryParameters(
                            OEMBED_ENDPOINT,
                            "url", content.getUrl(),
                            "width", 640)
                    ),
                    5000)
                )
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return response;
    }
}
