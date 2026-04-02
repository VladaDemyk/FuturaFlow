package com.futura.FuturaFlow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Кажемо серверу: "Якщо хтось просить адресу /uploads/**,
        // шукай ці файли у фізичній папці uploads/ у нашому проєкті"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}