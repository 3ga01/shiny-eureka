package com.emmanuel.user_service.service.storage;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
  String upload(MultipartFile file, String folder) throws IOException;

  String uploadAvatar(byte[] avatarBytes, String prefix) throws IOException;

  void delete(String key);

  String buildPublicUrl(String key);

  String getUrl(String key);

  byte[] generateAvatar(String firstName, String lastName, int size) throws IOException;

  String generateAndUploadAvatar(String firstName, String lastName) throws IOException;
}
