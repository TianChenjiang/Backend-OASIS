package com.rubiks.backendoasis.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ElasticSearchConfig {
//    @Value("104.199.248.49")
    @Value("${ESHOST:101.132.102.201}")
    private String host;

//    @Value("${elasticsearch.port}")
    @Value("9200")
    private int port;

    @Value("${ES_USER:elastic}")
    private String userName;

    @Value("${ES_PASS:2020liujia}")
    private String password;

    @Primary
    @Bean(destroyMethod = "close")
    public RestHighLevelClient restClient() {
       final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
       credentialsProvider.setCredentials(AuthScope.ANY,
               new UsernamePasswordCredentials(userName, password));
//
       RestClientBuilder builder = RestClient.builder(new HttpHost(host, port))
               .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
       RestHighLevelClient client = new RestHighLevelClient(builder);
        // RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("47.101.33.219", 9200)));
                // RestClient.builder(new HttpHost(host, port)));
        return client;
    }
}
