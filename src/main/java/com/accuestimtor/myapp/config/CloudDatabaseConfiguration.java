package com.accuestimtor.myapp.config;

import com.accuestimtor.myapp.domain.util.JSR310DateConverters.*;

import com.github.mongobee.Mongobee;

import com.accuestimtor.myapp.config.oauth2.OAuth2AuthenticationReadConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories("com.accuestimtor.myapp.repository")
@Profile(Constants.SPRING_PROFILE_CLOUD)
public class CloudDatabaseConfiguration extends AbstractCloudConfig {

    private final Logger log = LoggerFactory.getLogger(CloudDatabaseConfiguration.class);

    @Bean
    public MongoDbFactory mongoFactory() {
        return connectionFactory().mongoDbFactory();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new OAuth2AuthenticationReadConverter());
        converterList.add(DateToZonedDateTimeConverter.INSTANCE);
        converterList.add(ZonedDateTimeToDateConverter.INSTANCE);
        return new CustomConversions(converterList);
    }

    @Bean
    public Mongobee mongobee(MongoDbFactory mongoDbFactory) throws Exception {
        log.debug("Configuring Mongobee");
        Mongobee mongobee = new Mongobee(mongoDbFactory.getDb().getMongo());
        mongobee.setDbName(mongoDbFactory.getDb().getName());
        // package to scan for migrations
        mongobee.setChangeLogsScanPackage("com.accuestimtor.myapp.config.dbmigrations");
        mongobee.setEnabled(true);
        return mongobee;
    }
}