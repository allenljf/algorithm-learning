package dev.algorithmlearning.api.tags.domain;

import java.text.Normalizer;
import java.util.Locale;

/** Produces the display and owner-local comparison forms defined by the tag contract. */
public final class TagNameNormalizer {
    private TagNameNormalizer() { }

    public static NormalizedTagName normalize(String rawName) {
        if (rawName == null) throw new IllegalArgumentException("Tag name is required.");
        var displayName = Normalizer.normalize(rawName, Normalizer.Form.NFKC).trim().replaceAll("\\s+", " ");
        if (displayName.isEmpty() || displayName.codePointCount(0, displayName.length()) > 80) {
            throw new IllegalArgumentException("Tag name must contain 1 to 80 characters.");
        }
        return new NormalizedTagName(displayName, displayName.toLowerCase(Locale.ROOT));
    }

    public record NormalizedTagName(String displayName, String comparisonKey) { }
}
