package com.bahar;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

/**
 * 启动程序
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@EnableScheduling
@SpringBootApplication
public class baharApplication {

    public static final String REWRITE_FILTER_NAME = "rewriteFilter";
    public static final String REWRITE_FILTER_CONF_PATH = "urlRewrite.xml";

    public static void main(String[] args) {
        SpringApplication.run(baharApplication.class, args);
        System.out.println("==================================================\n" +
                "恭喜，bahar系统启动成功啦！  \n" +
                "系统官网：https://www.bahar.cn  \n" +
                "接口文档：http://localhost:8080/swagger-ui.html \n" +
                "==================================================\n  \n");
    }

    @Bean
    public FilterRegistrationBean rewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName(REWRITE_FILTER_NAME);
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", REWRITE_FILTER_CONF_PATH);
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("statusPath", "/redirect");
        reg.addInitParameter("statusEnabledOnHosts", "*");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    @Bean
    @Primary
    public ObjectMapper globalObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
