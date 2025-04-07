package com.aptconnect.component;

import com.aptconnect.menu.master.errorLog.entity.ErrorLog;
import com.aptconnect.menu.master.errorLog.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private ErrorLogRepository errorLogRepository;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex, WebRequest request) {
        String path      = ((ServletWebRequest) request).getRequest().getRequestURI();
        String userAgent = ((ServletWebRequest) request).getRequest().getHeader("User-Agent");
        String ipAddress = ((ServletWebRequest) request).getRequest().getRemoteAddr();

        // 로그 저장
        ErrorLog errorLog = ErrorLog.builder()
                .level("ERROR")
                .message(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .stackTrace(getStackTraceAsString(ex))
                .path(path)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .timestamp(LocalDateTime.now())
                .build();

        errorLogRepository.save(errorLog);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류 발생");
    }

    private String getStackTraceAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String fullStackTrace = sw.toString();

        // 만약 너무 길다면 앞부분 2000자까지만 저장
        return fullStackTrace.length() > 2000 ? fullStackTrace.substring(0, 2000) + "..." : fullStackTrace;
    }

}