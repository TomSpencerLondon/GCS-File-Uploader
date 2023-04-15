package com.tomspencerlondon.gcsfileuploader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GCSController {

    @GetMapping("/")
    public String get() {
        return "hello";
    }
}
