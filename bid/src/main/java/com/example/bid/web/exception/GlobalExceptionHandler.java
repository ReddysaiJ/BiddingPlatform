package com.example.bid.web.exception;

import com.example.bid.domain.exception.AuctionNotFoundException;
import com.example.bid.domain.exception.BidNotAllowedException;
import com.example.bid.domain.exception.InvalidBidException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final URI BAD_REQUEST = URI.create("https://api.example.com/errors/bad-request");
    private static final URI FORBIDDEN = URI.create("https://api.example.com/errors/forbidden");
    private static final URI NOT_FOUND = URI.create("https://api.example.com/errors/not-found");
    private static final URI SERVICE_UNAVAILABLE = URI.create("https://api.example.com/errors/service-unavailable");
    private static final URI INTERNAL_ERROR = URI.create("https://api.example.com/errors/internal");

    private static final String SERVICE_NAME = "bid-service";

    @ExceptionHandler(InvalidBidException.class)
    ProblemDetail handleInvalidBid(InvalidBidException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Bid Request", ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(BidNotAllowedException.class)
    ProblemDetail handleBidNotAllowed(BidNotAllowedException ex) {
        return build(HttpStatus.FORBIDDEN, "Bid Not Allowed", ex.getMessage(), FORBIDDEN);
    }

    @ExceptionHandler(AuctionNotFoundException.class)
    ProblemDetail handleAuctionNotFound(AuctionNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Auction Not Found", ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    ProblemDetail handleCircuitBreaker(CallNotPermittedException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, "Service Temporarily Unavailable", "Auction service is currently unavailable.", SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(TimeoutException.class)
    ProblemDetail handleTimeout(TimeoutException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, "Upstream Timeout", "Auction service did not respond in time.", SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FeignException.class)
    ProblemDetail handleFeign(FeignException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, "Upstream Service Error", "Error while communicating with Auction service.", SERVICE_UNAVAILABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail pd = build(HttpStatus.BAD_REQUEST, "Validation Failed", "Invalid request payload", BAD_REQUEST);

        pd.setProperty(
                "errors",
                ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList()
        );
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnhandled(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error occurred", INTERNAL_ERROR);
    }

    private ProblemDetail build(HttpStatus status, String title, String detail, URI type) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(type);
        pd.setProperty("service", SERVICE_NAME);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
