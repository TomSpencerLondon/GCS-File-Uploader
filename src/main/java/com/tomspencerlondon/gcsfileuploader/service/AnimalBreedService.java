package com.tomspencerlondon.gcsfileuploader.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomspencerlondon.gcsfileuploader.config.BreedsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnimalBreedService {

    @Autowired
    BreedsConfig breedsConfig;

    @Autowired
    public void setBreedsConfig(@Value("${animals.config.json}") String animalConfig) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        breedsConfig = objectMapper.readValue(animalConfig, BreedsConfig.class);
    }

    public BreedsConfig getBreedsConfig() {
        return breedsConfig;
    }
}
