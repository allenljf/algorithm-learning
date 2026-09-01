package dev.algorithmlearning.api.problems.domain;

import java.net.URI;

public final class ExternalUrlPolicy {
    private ExternalUrlPolicy() { }
    public static void requireAllowed(String platform, String url) {
        if (url == null) return;
        try {
            var parsed = URI.create(url);
            if (!"https".equalsIgnoreCase(parsed.getScheme()) || parsed.getHost() == null) throw new IllegalArgumentException("External URL must use HTTPS.");
            var host = parsed.getHost().toLowerCase();
            if ("leetcode".equals(platform) && !(host.equals("leetcode.com") || host.endsWith(".leetcode.com"))) throw new IllegalArgumentException("LeetCode URLs must use leetcode.com.");
            if ("hacker_rank".equals(platform) && !(host.equals("hackerrank.com") || host.endsWith(".hackerrank.com"))) throw new IllegalArgumentException("HackerRank URLs must use hackerrank.com.");
        } catch (IllegalArgumentException exception) { throw exception; }
    }
}
