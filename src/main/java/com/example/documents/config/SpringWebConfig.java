package com.example.documents.config;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.documents.web")
public class SpringWebConfig implements WebMvcConfigurer {

    @Bean
    public Gson springGson() {
        JsonSerializer<Instant> instantSerializer =
                (Instant src, Type typeOfSrc, JsonSerializationContext context) ->
                        new JsonPrimitive(src.toString());

        JsonDeserializer<Instant> instantDeserializer =
                (JsonElement json, Type typeOfT, JsonDeserializationContext context) ->
                        Instant.parse(json.getAsString());

        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, instantSerializer)
                .registerTypeAdapter(Instant.class, instantDeserializer)
                .serializeNulls()
                .create();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver vr = new InternalResourceViewResolver();
        vr.setPrefix("/WEB-INF/jsp/");
        vr.setSuffix(".jsp");
        return vr;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        boolean gsonConfigured = false;

        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof GsonHttpMessageConverter gsonConverter) {
                gsonConverter.setGson(springGson());
                gsonConfigured = true;
            }
        }

        if (!gsonConfigured) {
            GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
            gsonConverter.setGson(springGson());
            converters.add(gsonConverter);
        }
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}