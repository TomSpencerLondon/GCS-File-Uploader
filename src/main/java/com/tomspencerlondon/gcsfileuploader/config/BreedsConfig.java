package com.tomspencerlondon.gcsfileuploader.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@ToString
public class BreedsConfig {

    @JsonProperty("animals")
    Map<String, String> animalsMapping;
}