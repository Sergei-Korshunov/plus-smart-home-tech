package ru.yandex.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class ErrorMessage {
    private Throwable cause;
    private StackTraceElement[] stackTrace;
    private HttpStatus httpstatus;
    private String userMessage;
    private String message;
    private Throwable[] suppressed;
    private String localizedMessage;

    public ErrorMessage(RuntimeException exception, HttpStatus httpstatus) {
        this.cause = exception.getCause();
        this.stackTrace = exception.getStackTrace();
        this.httpstatus = httpstatus;
        this.userMessage = exception.getMessage();
        this.message = exception.getMessage();
        this.suppressed = exception.getSuppressed();
        this.localizedMessage = exception.getLocalizedMessage();
    }
}