package com.rubiks.backendoasis.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${MONGOHOST:localhost}")
    private String host;

    @Bean
    public MongoClient mongoClient() {
        String connection = "mongodb://" + host + ":27017";
        return MongoClients.create(connection);
    }
}
