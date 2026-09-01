package dev.algorithmlearning.api.app.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import dev.algorithmlearning.api.auth.application.AuthException;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(AuthException.class)
    ProblemDetail handleAuthException(AuthException exception, HttpServletRequest request) {
        var status = exception.getMessage().equals("Too many requests") ? HttpStatus.TOO_MANY_REQUESTS : HttpStatus.UNAUTHORIZED;
        var problem = ProblemDetail.forStatusAndDetail(status, status == HttpStatus.UNAUTHORIZED ? "Authentication failed." : "Too many requests.");
        problem.setProperty("code", status == HttpStatus.UNAUTHORIZED ? "unauthenticated" : "rate_limited");
        problem.setProperty("requestId", request.getAttribute(RequestIdFilter.ATTRIBUTE));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpectedException(Exception exception, HttpServletRequest request) {
        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.");
        problem.setTitle("Internal Server Error");
        problem.setType(java.net.URI.create("https://algorithm-learning.local/problems/internal-error"));
        problem.setProperty("code", "internal_error");
        problem.setProperty("requestId", request.getAttribute(RequestIdFilter.ATTRIBUTE));
        return problem;
    }
}
