package com.bahar.framework.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.Ordered;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

/**
 * 请求级语言解析：URL 参数 lang > 请求头 Accept-Language > 默认 zh-CN。
 * 解析结果写入 LocaleContextHolder，并在 finally 清理，避免线程池复用串语言。
 */
@Configuration
public class LocaleFilter {

    @Bean
    public FilterRegistrationBean<Filter> localeFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LocaleRequestFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("localeFilter");
        return registration;
    }

    static class LocaleRequestFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            try {
                Locale locale = resolveLocale((HttpServletRequest) request);
                LocaleContextHolder.setLocaleContext(new SimpleLocaleContext(locale), true);
                chain.doFilter(request, response);
            } finally {
                LocaleContextHolder.resetLocaleContext();
            }
        }

        private Locale resolveLocale(HttpServletRequest request) {
            // ① URL 参数 lang（值如 en-US / en / zh-CN）
            String lang = request.getParameter("lang");
            Locale locale = parseLang(lang);
            if (locale != null) {
                return locale;
            }
            // ② 请求头 Accept-Language（取第一个片段）
            String accept = request.getHeader("Accept-Language");
            if (accept != null && !accept.isEmpty()) {
                String first = accept.split(",")[0].trim();
                locale = parseLang(first);
                if (locale != null) {
                    return locale;
                }
            }
            // ③ 默认 zh-CN
            return Locale.SIMPLIFIED_CHINESE;
        }

        private Locale parseLang(String s) {
            if (s == null || s.isEmpty()) {
                return null;
            }
            String lower = s.toLowerCase();
            if (lower.startsWith("zh")) {
                return Locale.SIMPLIFIED_CHINESE;
            }
            if (lower.startsWith("en")) {
                return Locale.US;
            }
            return null;
        }
    }
}
