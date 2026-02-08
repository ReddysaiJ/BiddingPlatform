package com.example.auction.web.exception;

import com.example.auction.domain.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String SERVICE = "auction-service";

    private static final URI NOT_FOUND = URI.create("https://api.auction.com/errors/not-found");
    private static final URI VALIDATION = URI.create("https://api.auction.com/errors/validation");
    private static final URI ACCESS = URI.create("https://api.auction.com/errors/access-denied");
    private static final URI AUTH = URI.create("https://api.auction.com/errors/authentication");
    private static final URI DOMAIN = URI.create("https://api.auction.com/errors/domain");
    private static final URI INTERNAL = URI.create("https://api.auction.com/errors/internal");

    @ExceptionHandler(AuctionNotFoundException.class)
    ProblemDetail handleNotFound(AuctionNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), "Resource Not Found", NOT_FOUND, "Domain");
    }

    @ExceptionHandler(InvalidJwtException.class)
    ProblemDetail handleInvalidJwt(InvalidJwtException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), "Invalid or Missing JWT", AUTH, "Authentication");
    }

    @ExceptionHandler(AccessDeniedDomainException.class)
    ProblemDetail handleAccessDenied(AccessDeniedDomainException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), "Access Denied", URI.create("urn:problem:access-denied"), "SECURITY");
    }

    @ExceptionHandler(AuctionException.class)
    ProblemDetail handleDomain(AuctionException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "Auction Domain Violation", DOMAIN, "Business");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleBeanValidation(MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return build(HttpStatus.BAD_REQUEST, errors, "Validation Failed", VALIDATION, "Validation");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), "Constraint Violation", VALIDATION, "Validation");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnhandled(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error occurred", "Internal Server Error", INTERNAL, "System");
    }

    private ProblemDetail build(HttpStatus status, String detail, String title, URI type, String category) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(type);
        pd.setProperty("service", SERVICE);
        pd.setProperty("category", category);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
