package com.emmanuel.user_service.controller;


import com.emmanuel.user_service.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) {
        try {
            String url = storageService.upload(file, folder);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable String key) {
        storageService.delete(key);
        return ResponseEntity.ok("Deleted");
    }
}
