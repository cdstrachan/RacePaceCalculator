package com.cds.paceservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class PaceCalculatorInceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(PaceCalculatorController.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String sMetric = request.getRequestURI().replace("/", "");
        log.info("PreHandler called for: " + sMetric);
        if (!sMetric.contains(".")) // anything with a DOT is a resource and excluded from the diagnistics
            DiagnosticsService.incrementCounter(sMetric);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

        String sMetric = request.getRequestURI().replace("/", "");
        log.info("PostHandler called for: " + sMetric);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception exception) throws Exception {

        String sMetric = request.getRequestURI().replace("/", "");
        log.info("AfterCompletion called for: " + sMetric);

    }
}