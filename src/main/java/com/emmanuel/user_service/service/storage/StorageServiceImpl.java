package com.emmanuel.user_service.service.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.s3.endpoint}")
  private String endpoint;

  @Override
  public String upload(MultipartFile file, String folder) throws IOException {
    String key = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

    PutObjectRequest request =
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.getContentType())
            .build();

    s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

    return buildPublicUrl(key);
  }

  @Override
  public void delete(String key) {
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
  }

  @Override
  public String buildPublicUrl(String key) {
    return endpoint + "/" + bucket + "/" + key;
  }


    @Override
  public String getUrl(String key) {
    return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toString();
  }
}
