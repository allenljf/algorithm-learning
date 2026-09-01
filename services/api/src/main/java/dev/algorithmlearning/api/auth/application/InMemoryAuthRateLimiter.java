package dev.algorithmlearning.api.auth.application;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Small replaceable boundary; production deployments may substitute a distributed limiter. */
public final class InMemoryAuthRateLimiter implements AuthRateLimiter {
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final Clock clock;
    public InMemoryAuthRateLimiter(Clock clock) { this.clock = clock; }
    public boolean allows(String remoteAddress, String account) {
        var key = remoteAddress + ":" + account;
        var now = clock.instant();
        return windows.compute(key, (ignored, current) -> current == null || !current.startedAt().plusSeconds(60).isAfter(now)
                ? new Window(now, 1) : new Window(current.startedAt(), current.count() + 1)).count() <= 10;
    }
    private record Window(Instant startedAt, int count) { }
}
