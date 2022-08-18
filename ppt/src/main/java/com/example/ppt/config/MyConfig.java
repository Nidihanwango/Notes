package com.example.ppt.config;

import com.example.ppt.interceptor.LoginInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor interceptor = new LoginInterceptor();
        List<String> paths = new ArrayList<>();
        paths.add("/manager/**");
        registry.addInterceptor(interceptor).addPathPatterns(paths);
    }
}
