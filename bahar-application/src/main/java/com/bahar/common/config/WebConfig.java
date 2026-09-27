package com.bahar.common.config;

import com.bahar.common.web.AdminUserInterceptor;
import com.bahar.common.web.ClientUserInterceptor;
import com.bahar.common.web.CommandInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.concurrent.TimeUnit;

/**
 * web配置
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 本地文件存储根目录，取自 images.root（例如 D:/oss/bahar-shop/）
     * */
    @Value("${images.root:}")
    private String imagesRoot;

    /**
     * 拼装本地磁盘资源位置，供 /static/** 直接读取盘上的上传文件
     * */
    private String localUploadLocation() {
        if (imagesRoot == null || imagesRoot.trim().isEmpty()) {
            return "classpath:/static/";
        }
        String root = imagesRoot.trim().replace("\\", "/");
        if (!root.endsWith("/")) {
            root = root + "/";
        }
        return "file:" + root + "static/";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger 资源映射必须放在 /** 之前
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/", "classpath:/other-resources/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(false)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                .addTransformer(new CssLinkResourceTransformer());
        // 静态资源：优先 classpath，未命中时从本地磁盘 images.root 读取上传文件
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/", localUploadLocation());

        registry.addResourceHandler("/**").addResourceLocations(
                "classpath:/static/");
    }

    @Bean
    public CommandInterceptor commandInterceptor() {
        return new CommandInterceptor();
    }

    @Bean
    public AdminUserInterceptor adminUserInterceptor() {
        return new AdminUserInterceptor();
    }

    @Bean
    public ClientUserInterceptor portalUserInterceptor() {
        return new ClientUserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Command
        registry.addInterceptor(commandInterceptor())
                .addPathPatterns("/cmd/**");

        // 后台拦截
        registry.addInterceptor(adminUserInterceptor())
                .addPathPatterns("/backendApi/**")
                .excludePathPatterns("/clientApi/captcha/**")
                .excludePathPatterns("/backendApi/captcha/**")
                .excludePathPatterns("/backendApi/userCoupon/exportList")
                .excludePathPatterns("/backendApi/order/export")
                .excludePathPatterns("/backendApi/member/export")
                .excludePathPatterns("/backendApi/goods/goods/downloadTemplate")
                .excludePathPatterns("/backendApi/member/downloadTemplate")
                .excludePathPatterns("/backendApi/login/**");

        // 客户端拦截
        registry.addInterceptor(portalUserInterceptor())
                .addPathPatterns("/clientApi/**")
                .excludePathPatterns("/clientApi/sign/**")
                .excludePathPatterns("/clientApi/page/home")
                .excludePathPatterns("/clientApi/captcha/**")
                .excludePathPatterns("/clientApi/goodsApi/**")
                .excludePathPatterns("/clientApi/coupon/list")
                .excludePathPatterns("/clientApi/coupon/detail")
                .excludePathPatterns("/clientApi/cart/**")
                .excludePathPatterns("/clientApi/user/**")
                .excludePathPatterns("/clientApi/service/list")
                .excludePathPatterns("/clientApi/settlement/submit")
                .excludePathPatterns("/clientApi/pay/doPay")
                .excludePathPatterns("/clientApi/pay/weixinCallback")
                .excludePathPatterns("/clientApi/pay/weixinRefundNotify")
                .excludePathPatterns("/clientApi/pay/aliPayCallback")
                .excludePathPatterns("/clientApi/order/todoCounts")
                .excludePathPatterns("/clientApi/order/detail")
                .excludePathPatterns("/clientApi/store/**")
                .excludePathPatterns("/clientApi/article/**")
                .excludePathPatterns("/clientApi/message/getOne")
                .excludePathPatterns("/clientApi/message/wxPush")
                .excludePathPatterns("/clientApi/sms/sendVerifyCode")
                .excludePathPatterns("/clientApi/book/list")
                .excludePathPatterns("/clientApi/book/detail")
                .excludePathPatterns("/clientApi/book/cateList");
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将 swagger-ui.html 重定向到 swagger-ui/
        registry.addRedirectViewController("/swagger-ui.html", "/swagger-ui/");
    }
}
