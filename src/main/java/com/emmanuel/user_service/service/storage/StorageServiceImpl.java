package com.emmanuel.user_service.service.storage;

import com.emmanuel.user_service.utility.PASTEL_COLORS;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
  public String uploadAvatar(byte[] avatarBytes, String prefix) throws IOException {
    String key = "avatars/" + prefix + "-" + UUID.randomUUID() + ".png";

    PutObjectRequest request =
        PutObjectRequest.builder().bucket(bucket).key(key).contentType("image/png").build();

    s3Client.putObject(request, RequestBody.fromBytes(avatarBytes));

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

  @Override
  public byte[] generateAvatar(String firstName, String lastName, int size) throws IOException {
    String initials = getInitials(firstName, lastName);
    Color bgColor = getColorForName(firstName + lastName);

    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();

    // Set rendering hints for better quality
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Draw background
    g2d.setColor(bgColor);
    g2d.fillRect(0, 0, size, size);

    // Draw circle for rounded effect (optional)
    g2d.setColor(bgColor.darker());
    g2d.fillOval(0, 0, size, size);

    // Draw initials
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, size / 2));

    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(initials);
    int textHeight = fm.getHeight();

    int x = (size - textWidth) / 2;
    int y = (size - textHeight) / 2 + fm.getAscent();

    g2d.drawString(initials, x, y);

    g2d.dispose();

    // Convert to byte array
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "PNG", baos);

    return baos.toByteArray();
  }

  @Override
  public String generateAndUploadAvatar(String firstName, String lastName) throws IOException {
    byte[] avatarBytes = generateAvatar(firstName, lastName, 400);

    // Create a MultipartFile-like structure or upload directly
    String fileName = "avatar-" + UUID.randomUUID() + ".png";
    String key = "avatars/" + fileName;

    // Upload to S3
    return uploadAvatar(avatarBytes, fileName);
  }

  private String getInitials(String firstName, String lastName) {
    String firstInitial =
        firstName != null && !firstName.isEmpty()
            ? String.valueOf(firstName.charAt(0)).toUpperCase()
            : "U";
    String lastInitial =
        lastName != null && !lastName.isEmpty()
            ? String.valueOf(lastName.charAt(0)).toUpperCase()
            : "U";

    return firstInitial + lastInitial;
  }

  private Color getColorForName(String name) {
    return PASTEL_COLORS.getRandomColor();
  }
}
