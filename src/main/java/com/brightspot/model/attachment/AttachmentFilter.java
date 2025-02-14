package com.brightspot.model.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.brightspot.utils.Utils;
import com.psddev.cms.db.PageFilter;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.AbstractFilter;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StorageItem;
import com.psddev.dari.util.UrlStorageItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class AttachmentFilter extends AbstractFilter implements AbstractFilter.Auto {

    public static final String PATH = "/_attachment";

    private static final Integer READ_BUFFER_SIZE = 4096;
    private static final Integer READS_PER_FLUSH = 8192;

    // -- Overrides -- //

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {
        String uri = request.getRequestURI();
        if (uri.startsWith(PATH)) {
            String contentId = StringUtils.removeStart(uri, PATH).replaceAll("/", "");
            Attachment attachment = Query.from(Attachment.class).where("id = ?", contentId).first();
            if (!ObjectUtils.isBlank(attachment)) {
                deliverAttachment(attachment, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void updateDependencies(Class<? extends AbstractFilter> filterClass, List<Class<? extends Filter>> dependencies) {
        if (PageFilter.class.isAssignableFrom(filterClass)) {
            dependencies.add(getClass());
        }
    }

    private static void deliverAttachment(Attachment attachment, HttpServletResponse response) throws IOException {
        StorageItem file = attachment.getFile();
        if (file == null || StringUtils.isBlank(file.getSecurePublicUrl())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(file.getContentType());

        Optional.ofNullable(file.getMetadata())
            .map(metadata -> metadata.get("http.headers"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(header -> header.get("Content-Length"))
            .map(Object::toString)
            .ifPresent(value -> response.setHeader("Content-Length", value));

        if (attachment.isAutoDownload()) {
            response.setHeader("Content-Disposition", "attachment;filename=" + getFileName(file));
        }

        copy(file.getData(), getOriginalResponse(response).getOutputStream());
    }

    // Similar to IoUtils#copy, but optimized for large files
    private static long copy(InputStream source, OutputStream destination) throws IOException {
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        long total = 0L;
        int currentReadCount = 0;

        for (int read; (read = source.read(buffer)) > -1;) {
            destination.write(buffer, 0, read);
            total += read;
            currentReadCount++;

            // periodically flush, limiting memory usage and forcing data to be written to client sooner
            if (currentReadCount > READS_PER_FLUSH) {
                destination.flush();
                currentReadCount = 0;
            }
        }

        return total;
    }

    private static String getFileName(StorageItem item) {
        String fileName = ObjectUtils.to(String.class, item.getMetadata().get("originalFilename"));
        if (StringUtils.isBlank(fileName) && item instanceof UrlStorageItem) {
            fileName = item.getPath();
        }

        String extension = FilenameUtils.getExtension(fileName);
        String baseName = FilenameUtils.getBaseName(fileName);

        if (StringUtils.isNoneBlank(baseName, extension)) {
            fileName = Utils.toNormalized(baseName) + "." + extension;
        } else {
            fileName = Utils.toNormalized(fileName);
        }

        return fileName;
    }

    // Try to find the original response. Specifically, we don't want a com.psddev.dari.util.CapturingResponse,
    // which uses a ByteArrayOutputStream, which in turn doesn't flush and the underlying array grows to
    // contain the entire file
    private static HttpServletResponse getOriginalResponse(HttpServletResponse response) {
        while (response instanceof HttpServletResponseWrapper) {
            response = (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
        }

        return response;
    }
}
