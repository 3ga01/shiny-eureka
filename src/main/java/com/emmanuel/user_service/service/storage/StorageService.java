package com.emmanuel.user_service.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile file, String folder) throws IOException;
    void delete(String key);
    String buildPublicUrl(String key);
    String getUrl(String key);
}
