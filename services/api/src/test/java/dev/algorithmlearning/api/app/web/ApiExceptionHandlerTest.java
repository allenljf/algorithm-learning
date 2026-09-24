package dev.algorithmlearning.api.app.web;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

class ApiExceptionHandlerTest {

    @Test
    void logsRequestIdAndSanitizedStackTraceForUnexpectedFailures() {
        var request = new MockHttpServletRequest();
        var requestId = UUID.randomUUID().toString();
        request.setAttribute(RequestIdFilter.ATTRIBUTE, requestId);
        var logger = (Logger) LoggerFactory.getLogger(ApiExceptionHandler.class);
        var events = new ListAppender<ILoggingEvent>();
        events.start();
        logger.addAppender(events);

        try {
            new ApiExceptionHandler().handleUnexpectedException(
                    new IllegalStateException("password=must-not-appear"), request);
        } finally {
            logger.detachAppender(events);
        }

        assertThat(events.list).hasSize(1);
        var event = events.list.getFirst();
        assertThat(event.getFormattedMessage()).contains(requestId).contains(IllegalStateException.class.getName());
        assertThat(event.getFormattedMessage()).doesNotContain("password=must-not-appear");
        assertThat(event.getThrowableProxy().getStackTraceElementProxyArray()).isNotEmpty();
        assertThat(event.getThrowableProxy().getMessage()).doesNotContain("password=must-not-appear");
    }
}
