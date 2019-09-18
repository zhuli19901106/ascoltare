package com.zhuli.ascoltate.server;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zhuli.ascoltate.server.util.bean.BetterPageableArgumentResolver;

import java.util.List;

@EnableWebMvc
@Configuration
public class WebMvcConfigurerAdapter implements WebMvcConfigurer {
    @Autowired
    BetterPageableArgumentResolver betterPageableArgumentResolver;
    @Value("${spring.data.web.pageable.one-indexed-parameters:false}")
    private boolean oneIndexedParameters;

    /**
     * 返回中文不乱码
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(stringHttpMessageConverterForUtf8());
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        betterPageableArgumentResolver.setOneIndexedParameters(this.oneIndexedParameters);
        argumentResolvers.add(betterPageableArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverterForUtf8() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
}
