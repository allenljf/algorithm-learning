package dev.algorithmlearning.api.app.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void assignsOneGeneratedRequestIdToTheRequestAndResponse() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (ignoredRequest, ignoredResponse) -> {
        });

        assertThat(request.getAttribute(RequestIdFilter.ATTRIBUTE))
                .isEqualTo(response.getHeader(RequestIdFilter.HEADER));
        assertThat(response.getHeader(RequestIdFilter.HEADER)).isNotBlank();
    }
}
