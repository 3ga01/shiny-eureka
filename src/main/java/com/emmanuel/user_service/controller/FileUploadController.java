package com.emmanuel.user_service.controller;

import com.emmanuel.user_service.service.storage.StorageService;
import com.emmanuel.user_service.utility.URI;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(URI.FILE_BASE_URI)
@RequiredArgsConstructor
public class FileUploadController {

  private final StorageService storageService;

  @PostMapping("/upload")
  public ResponseEntity<?> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "folder", defaultValue = "general") String folder)
      throws IOException {
    String url = storageService.upload(file, folder);
    return ResponseEntity.ok(url);
  }

  @DeleteMapping("/{key}")
  public ResponseEntity<?> delete(@PathVariable String key) {
    storageService.delete(key);
    return ResponseEntity.ok("Deleted");
  }
}
