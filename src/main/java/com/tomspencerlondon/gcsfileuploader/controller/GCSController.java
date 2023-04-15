package com.tomspencerlondon.gcsfileuploader.controller;

import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GCSController {

    @Value("${gcs.bucket}")
    private String gcsBucket;

    @PostMapping("/uploaddata/{folder_name}")
    public ResponseEntity<String> uploadData(@RequestBody(required = true) byte[] requestEntity,
                                             @PathVariable("folder_name") String folderName) {
        log.info("Received folder name for creation: {}", folderName);

        Storage storage = StorageOptions.getDefaultInstance().getService();
        String folder = String.format("%s/file_%s.csv",
                folderName,
                System.currentTimeMillis());
        BlobId blobId = BlobId.of(this.gcsBucket, folder);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        Blob blob = storage.create(blobInfo, requestEntity);
        System.out.println(blob);
        return ResponseEntity
                .ok()
                .body("Successful upload to " + folderName + "/");
    }

}
