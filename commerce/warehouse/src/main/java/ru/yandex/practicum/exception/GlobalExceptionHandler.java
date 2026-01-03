package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> constraintViolationException(ConstraintViolationException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorMessage> specifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouseException.class)
    public ResponseEntity<ErrorMessage> productInShoppingCartLowQuantityInWarehouseException(ProductInShoppingCartLowQuantityInWarehouseException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorMessage> noSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception, HttpStatus.BAD_REQUEST));
    }
}