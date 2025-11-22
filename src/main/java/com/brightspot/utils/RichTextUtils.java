package com.brightspot.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.brightspot.model.image.Image;
import com.brightspot.model.rte.enhancement.image.ImageEnhancement;
import com.psddev.cms.db.RichTextElement;
import com.psddev.cms.rte.EditorialMarkupRichTextPreprocessor;
import com.psddev.cms.rte.RichTextPreprocessor;
import com.psddev.dari.db.ObjectType;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class RichTextUtils {

    private RichTextUtils() {
    }

    public static boolean isBlank(String richText) {
        return richText == null || StringUtils.isBlank((richText).replaceAll("<br\\s*/?>", ""));
    }

    public static String preprocessRichText(String richText, RichTextPreprocessor... preprocessors) {
        if (preprocessors == null) {
            return richText;
        }

        Document document = documentFromRichText(richText);

        Element body = document.body();
        for (RichTextPreprocessor preprocessor : preprocessors) {
            preprocessor.preprocess(body);
        }

        richText = document.body().html();

        return richText;
    }

    public static Document documentFromRichText(String richText) {
        Document document = Jsoup.parseBodyFragment(richText);
        document.outputSettings().prettyPrint(false);

        return document;
    }

    public static String richTextToPlainText(String richText) {
        if (StringUtils.isBlank(richText)) {
            return richText;
        }

        // Parse str into a Document
        Document doc = documentFromRichText(richText);

        // Remove / unwrap track changes tags
        new EditorialMarkupRichTextPreprocessor().preprocess(doc.body());

        // Get back the string of the body.
        return doc.body().text();
    }

    public static String stripRichTextElements(String richText) {
        if (StringUtils.isBlank(richText)) {
            return richText;
        }

        // Parse str into a Document
        Document doc = documentFromRichText(richText);

        // Remove / unwrap track changes tags
        new EditorialMarkupRichTextPreprocessor().preprocess(doc.body());

        Map<String, ObjectType> concreteTagTypes = RichTextElement.getConcreteTagTypes();

        // Find all defined RichTextElements as a comma-delimited list
        String cssQuery = String.join(",", concreteTagTypes.keySet());

        // Completely remove all tags
        for (Element element : doc.select(cssQuery)) {
            element.remove();
        }

        // Get back the string of the body.
        return doc.body().html();
    }

    public static String getFirstBodyParagraph(String richText) {
        if (richText != null) {
            richText = stripRichTextElements(richText);

            String[] parts = richText.split("<br\\s?/?>");
            for (String part : parts) {
                String firstParagraph = RichTextUtils.richTextToPlainText(part);
                if (StringUtils.isNotBlank(firstParagraph)) {
                    return firstParagraph;
                }
            }
        }

        return null;
    }

    public static List<Image> getImagesFromRichText(String richText) {
        if (StringUtils.isNotBlank(richText)) {
            Document bodyDocument = RichTextUtils.documentFromRichText(richText);
            return bodyDocument.select(ImageEnhancement.TAG_NAME)
                .stream()
                .map(e -> {
                    ImageEnhancement iRte = new ImageEnhancement();
                    iRte.fromAttributes(getAttributeMap(e));
                    iRte.fromBody(e.html());
                    return iRte.getImage();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public static Image getFirstImageFromRichText(String richText) {
        if (StringUtils.isNotBlank(richText)) {
            return RichTextUtils.documentFromRichText(richText).select(ImageEnhancement.TAG_NAME)
                .stream()
                .map(e -> {
                    ImageEnhancement imageRTE = new ImageEnhancement();
                    imageRTE.fromAttributes(getAttributeMap(e));
                    imageRTE.fromBody(e.html());
                    return imageRTE.getImage();
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        }

        return null;
    }

    public static Map<String, String> getAttributeMap(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("element cannot be empty");
        }

        Map<String, String> attributeMap = new HashMap<>();
        for (Attribute attribute : element.attributes().asList()) {
            attributeMap.put(attribute.getKey(), attribute.getValue());
        }

        return attributeMap;
    }
}
