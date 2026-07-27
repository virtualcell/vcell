package org.vcell.restq.handlers.dto;

import cbit.image.GIFImage;
import org.vcell.util.ISize;

import java.util.Base64;

/**
 * Flat, JSON-safe DTO for a {@link GIFImage} browse thumbnail.
 * <p>
 * The core {@link GIFImage} exposed its bytes in a way that OpenAPI mapped to a binary {@code File},
 * and {@link GIFImage#getSize()} decoded the image on every access — NPE'ing for an undecodable
 * thumbnail and aborting the whole streamed response mid-way. This DTO carries the raw gif bytes as
 * base64 and the (already null-safe) decoded dimensions, so it serializes deterministically and never
 * throws. {@code width}/{@code height} are null when the gif cannot be decoded.
 */
public record PreviewRep(
        String gifBase64,
        Integer width,
        Integer height
) {
    public static PreviewRep fromGIFImage(GIFImage gif) {
        if (gif == null) {
            return null;
        }
        String base64 = gif.getGifEncodedData() == null ? null
                : Base64.getEncoder().encodeToString(gif.getGifEncodedData());
        ISize size = gif.getSize(); // null-safe: returns null for an undecodable gif
        Integer width = size == null ? null : size.getX();
        Integer height = size == null ? null : size.getY();
        return new PreviewRep(base64, width, height);
    }
}
