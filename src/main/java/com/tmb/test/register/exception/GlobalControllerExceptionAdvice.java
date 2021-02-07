package com.tmb.test.register.exception;

import com.tmb.test.register.service.model.BaseErrorResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@RestControllerAdvice
public class GlobalControllerExceptionAdvice extends ResponseEntityExceptionHandler {

    // 400
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        logger.error(errors, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(ex, baseErrorResponse, headers, baseErrorResponse.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        logger.error(errors, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(ex, baseErrorResponse, headers, baseErrorResponse.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();
        logger.error(error, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String error = ex.getRequestPartName() + " part is missing";
        logger.error(error, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String error = ex.getParameterName() + " parameter is missing";
        logger.error(error, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        final String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        logger.error(error, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        final List<String> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }
        logger.error(errors, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @ExceptionHandler(MemberTypeNotAllowException.class)
    public ResponseEntity<Object> handleMemberTypeNotAllowException(final MemberTypeNotAllowException ex, final WebRequest request) {
        logger.error(ex.getLocalizedMessage(), ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    // 401
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Object> handleTokenExpiredException(final TokenExpiredException ex, final WebRequest request) {
        logger.error(ex.getLocalizedMessage(), ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    // 404
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        logger.error(error, ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(final UserNotFoundException ex, final WebRequest request) {
        logger.error(ex.getLocalizedMessage(), ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @ExceptionHandler(UserRefCodeNotFoundException.class)
    public ResponseEntity<Object> handleUserRefCodeFoundException(final UserRefCodeNotFoundException ex, final WebRequest request) {
        logger.error(ex.getLocalizedMessage(), ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    // 405
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));
        logger.error(builder.toString(), ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(final DataIntegrityViolationException ex, final WebRequest request) {
        logger.error("Data integrity violation error", ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.CONFLICT, NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    // 415
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
                                                                     final HttpHeaders headers,
                                                                     final HttpStatus status,
                                                                     final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(" "));
        logger.error(builder.toString(), ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        logger.error("Unexpected error", ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("Unexpected error", ex);
        final BaseErrorResponse baseErrorResponse = new BaseErrorResponse(status);
        return new ResponseEntity<>(baseErrorResponse, new HttpHeaders(), baseErrorResponse.getStatus());
    }
}
