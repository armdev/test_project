package com.progress.backend.configs;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = {"com"}, excludeFilters = {
    @ComponentScan.Filter(Configuration.class)})
@EnableTransactionManagement
@EnableMBeanExport
@EnableWebMvc
public class CommonCoreConfig {
    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setLocations(new Resource[]{        
            new ClassPathResource("log4j.properties")
        });
        ppc.setIgnoreUnresolvablePlaceholders(false);
        ppc.setIgnoreResourceNotFound(false);
        return ppc;
    }
}
