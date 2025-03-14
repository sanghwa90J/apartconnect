package com.aptconnect.component;

import com.aptconnect.entity.ErrorLog;
import com.aptconnect.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 특정 경로(/api, /admin 등)만 기록할 수도 있음
        if (request.getRequestURI().startsWith("/api")) {
            ErrorLog log = new ErrorLog();
            log.setTimestamp(LocalDateTime.now());
            log.setLevel("INFO");
            log.setMessage("요청됨: " + request.getMethod() + " " + request.getRequestURI());
            log.setPath(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());

            errorLogRepository.save(log);
        }
        return true;
    }
}
