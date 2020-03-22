package com.rubiks.backendoasis.repository;

import com.rubiks.backendoasis.entity.PaperEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends MongoRepository<PaperEntity, String> {
}
