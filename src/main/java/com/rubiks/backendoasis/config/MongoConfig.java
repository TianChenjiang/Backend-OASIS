package com.rubiks.backendoasis.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

   @Value("${MONGO:mongodb://mongo:2020liujia@101.132.102.201:27017}")
   private String mongo;

    @Bean
    public MongoClient mongoClient() {
//        MongoMappingContext ctx = new MongoMappingContext();
//        ctx.setAutoIndexCreation(true);
        // String connection = System.getenv("MONGO");
        // if (connection == null) {
        //     connection = "mongodb://localhost:27017";
        // }
        return MongoClients.create(mongo);
    }
}
