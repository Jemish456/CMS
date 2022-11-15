package com.globalvox.clinicmanagementsystem.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${profile.photo.path}")
    private String profilePhotoPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path photoDir= Paths.get(profilePhotoPath);
        String photoPath=photoDir.toFile().getAbsolutePath();

        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/");
        registry.addResourceHandler("./profile-photos/**").addResourceLocations("file:/"+photoPath+"/");
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
//		mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setSkipNullEnabled(true)
                .setSourceNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        return mapper;

    }

}
