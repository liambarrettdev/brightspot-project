package com.brightspot.utils;

import java.util.Objects;

import com.psddev.cms.db.Directory;
import com.psddev.cms.db.Site;
import com.psddev.cms.tool.CmsTool;
import com.psddev.dari.db.Application;
import com.psddev.dari.db.Recordable;
import com.psddev.dari.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for generating canonical URLs for content items.
 *
 * <p>This class provides methods to generate canonical URLs for recordable objects
 * based on their permalink configuration and site associations.</p>
 */
public final class DirectoryUtils {

    private DirectoryUtils() {
    }

    /**
     * Gets the canonical URL for a recordable object.
     *
     * <p>Looks for a URL in the following order:</p>
     * <ol>
     *     <li>Site permalink from the requested {@code Site}</li>
     *     <li>Site permalink from the recordable's owner {@code Site}</li>
     *     <li>First site permalink from any {@code Site} the recordable is accessible by</li>
     *     <li>Global permalink on the recordable (combined with site's primary URL)</li>
     * </ol>
     *
     * <p>Returns {@code null} if the recordable is null, has no state, or no valid
     * permalink can be found. Any wildcard characters (*) in the URL are removed.</p>
     *
     * <p>TODO: Consider caching URL generation results for better performance</p>
     * <p>FIXME: The logic for combining site URL with global permalink (step 4) may produce incorrect URLs if the permalink already includes a domain</p>
     *
     * @param site the site context for URL generation (may be null)
     * @param recordable the recordable object to get the URL for
     * @return the canonical URL, or {@code null} if not found or recordable is invalid
     */
    public static String getCanonicalUrl(Site site, Recordable recordable) {

        String url;

        if (recordable == null || recordable.getState() == null) {
            return null;
        }

        // #1
        url = recordable.getState().as(Directory.ObjectModification.class).getSitePermalink(site);
        if (StringUtils.isNotBlank(url)) {
            if (site == null) {
                CmsTool cmsSettings = Application.Static.getInstance(CmsTool.class);
                String siteUrl = StringUtils.removeEnd(cmsSettings.getDefaultSiteUrl(), "/");
                url = (siteUrl != null ? siteUrl : "") + url;
            }
        }

        // #2
        if (StringUtils.isBlank(url) && !ObjectUtils.equals(
            site,
            recordable.as(Site.ObjectModification.class).getOwner())) {
            Site ownerSite = recordable.getState().as(Site.ObjectModification.class).getOwner();
            if (ownerSite != null) {
                url = recordable.getState().as(Directory.ObjectModification.class).getSitePermalink(ownerSite);
            }
        }

        // #3
        if (StringUtils.isBlank(url)) {
            url = recordable.getState().as(Site.ObjectModification.class).getConsumers().stream()
                .filter(Objects::nonNull)
                .map(consumer -> recordable.getState()
                    .as(Directory.ObjectModification.class)
                    .getSitePermalink(consumer))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        }

        // #4
        if (StringUtils.isBlank(url) && site != null) {
            String siteUrl = StringUtils.removeEnd(site.getPrimaryUrl(), "/");
            String permalink = recordable.getState().as(Directory.ObjectModification.class).getSitePermalink(null);
            if (permalink == null || siteUrl == null) {
                url = null;
            } else {
                url = siteUrl + permalink;
            }
        }

        return url == null ? null : url.replace("*", StringUtils.EMPTY);
    }
}
