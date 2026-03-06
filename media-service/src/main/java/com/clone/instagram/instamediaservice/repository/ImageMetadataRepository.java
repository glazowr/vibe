package com.vibe.instamediaservice.repository;

import com.vibe.instamediaservice.model.ImageMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageMetadataRepository extends MongoRepository<ImageMetadata, String> {

}
