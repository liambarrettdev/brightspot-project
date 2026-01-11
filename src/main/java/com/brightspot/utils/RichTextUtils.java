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

/**
 * Utility class for working with rich text content.
 *
 * <p>This class provides methods to parse, process, and extract information from
 * rich text HTML content. It uses JSoup for HTML parsing and manipulation.</p>
 *
 * <p>All methods handle null and blank input gracefully.</p>
 */
public final class RichTextUtils {

    private RichTextUtils() {
    }

    /**
     * Checks if rich text content is blank (empty or contains only whitespace/line breaks).
     *
     * <p>Removes HTML line break tags before checking if the content is blank, so
     * content with only line breaks is considered blank.</p>
     *
     * @param richText the rich text content to check
     * @return {@code true} if the content is null or blank after removing line breaks
     */
    public static boolean isBlank(String richText) {
        return richText == null || StringUtils.isBlank((richText).replaceAll("<br\\s*/?>", ""));
    }

    /**
     * Preprocesses rich text content using one or more preprocessors.
     *
     * <p>Parses the rich text into a JSoup document and applies each preprocessor
     * in sequence to the document body. Returns the original text if preprocessors
     * array is null.</p>
     *
     * @param richText the rich text content to preprocess
     * @param preprocessors the preprocessors to apply (ignored if null)
     * @return the preprocessed rich text HTML
     */
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

    /**
     * Parses rich text content into a JSoup Document.
     *
     * <p>Creates a document from the rich text fragment with pretty printing disabled
     * to preserve the original formatting.</p>
     *
     * @param richText the rich text content to parse
     * @return a JSoup Document representing the parsed HTML
     */
    public static Document documentFromRichText(String richText) {
        Document document = Jsoup.parseBodyFragment(richText);
        document.outputSettings().prettyPrint(false);

        return document;
    }

    /**
     * Converts rich text HTML to plain text.
     *
     * <p>Parses the rich text, removes editorial markup (track changes), and extracts
     * only the text content without any HTML tags.</p>
     *
     * @param richText the rich text content to convert
     * @return the plain text content, or the original string if blank
     */
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

    /**
     * Removes all RichTextElement tags from rich text content.
     *
     * <p>Parses the rich text, removes editorial markup, and then removes all tags
     * that correspond to RichTextElement types. Returns the remaining HTML content.</p>
     *
     * @param richText the rich text content to process
     * @return the HTML with RichTextElement tags removed, or the original string if blank
     */
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

    /**
     * Extracts the first non-blank paragraph from rich text content.
     *
     * <p>Strips RichTextElement tags, splits on line breaks, and returns the first
     * part that contains non-blank plain text.</p>
     *
     * @param richText the rich text content to extract from
     * @return the first non-blank paragraph as plain text, or {@code null} if not found
     */
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

    /**
     * Extracts all Image objects from rich text content.
     *
     * <p>Parses the rich text and finds all ImageEnhancement tags, converting them
     * to Image objects. Returns an empty list if no images are found or if the
     * rich text is blank.</p>
     *
     * @param richText the rich text content to extract images from
     * @return a list of Image objects found in the rich text, or empty list if none found
     */
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

    /**
     * Extracts the first Image object from rich text content.
     *
     * <p>Parses the rich text and finds the first ImageEnhancement tag, converting
     * it to an Image object. Returns {@code null} if no image is found or if the
     * rich text is blank.</p>
     *
     * @param richText the rich text content to extract the image from
     * @return the first Image object found, or {@code null} if not found
     */
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

    /**
     * Extracts all attributes from a JSoup Element into a map.
     *
     * <p>Creates a map of all attribute key-value pairs from the element. Throws
     * an exception if the element is null.</p>
     *
     * @param element the JSoup Element to extract attributes from
     * @return a map of attribute names to values
     * @throws IllegalArgumentException if element is null
     */
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
