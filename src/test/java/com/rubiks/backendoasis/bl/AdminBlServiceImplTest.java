package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.model.admin.UpdatePaperParameter;
import com.rubiks.backendoasis.response.BasicResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class AdminBlServiceImplTest {
    @Mock
    MongoTemplate mongoTemplate;
    @Mock
    RestHighLevelClient client;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    AdminBlServiceImpl adminBlServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}



