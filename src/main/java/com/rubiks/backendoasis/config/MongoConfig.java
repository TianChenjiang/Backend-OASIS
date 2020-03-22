package com.rubiks.backendoasis.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

    @Value("${MONGOHOST:localhost}")
    private String host;

    @Bean
    public MongoClient mongoClient() {
//        MongoMappingContext ctx = new MongoMappingContext();
//        ctx.setAutoIndexCreation(true);

        String connection = "mongodb://" + host + ":27017/oasis";
        return MongoClients.create(connection);
    }
}
