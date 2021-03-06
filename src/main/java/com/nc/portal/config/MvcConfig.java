package com.nc.portal.config;

import com.nc.portal.controller.interceptors.RoleInterceptor;
import com.nc.portal.model.Role;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("com.nc.portal")
public class MvcConfig implements WebMvcConfigurer {

    public MvcConfig() {
        super();
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new RoleInterceptor(Role.CUSTOMER)).addPathPatterns("/customer/**");//думаю сделать ещё таких на каждую роль
        registry.addInterceptor(new RoleInterceptor(Role.OPERATOR)).addPathPatterns("/operator/**");
        registry.addInterceptor(new RoleInterceptor(Role.ADMIN)).addPathPatterns("/admin/**");
        registry.addInterceptor(new RoleInterceptor(Role.DRIVER)).addPathPatterns("/driver/**");
    }
}