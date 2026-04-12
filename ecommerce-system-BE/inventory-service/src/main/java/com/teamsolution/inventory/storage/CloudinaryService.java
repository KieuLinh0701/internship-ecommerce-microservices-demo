package com.teamsolution.inventory.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

  private final Cloudinary cloudinary;

  public Map<String, Object> uploadImage(MultipartFile file, String folder) {
    try {
      Map<?, ?> result =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  ObjectUtils.asMap(
                      "folder",
                      folder,
                      "resource_type",
                      "image",
                      "use_filename",
                      true,
                      "unique_filename",
                      true));
      log.info("Uploaded image to Cloudinary: publicId={}", result.get("public_id"));
      return Map.of(
          "publicId", result.get("public_id").toString(),
          "url", result.get("secure_url").toString());
    } catch (IOException e) {
      log.error("Failed to upload image to Cloudinary", e);
      throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);
    }
  }

  /** Xóa một ảnh theo publicId */
  public boolean deleteImage(String publicId) {
    try {
      Map<?, ?> result =
          cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
      String resultStr = result.get("result").toString();
      log.info("Deleted image from Cloudinary: publicId={}, result={}", publicId, resultStr);
      return "ok".equalsIgnoreCase(resultStr);
    } catch (IOException e) {
      log.error("Failed to delete image from Cloudinary: publicId={}", publicId, e);
      return false;
    }
  }

  /** Xóa nhiều ảnh cùng lúc (bulk delete) — tối đa 100 publicId / lần */
  public void deleteImages(List<String> publicIds) {
    if (publicIds == null || publicIds.isEmpty()) return;

    // Cloudinary hỗ trợ bulk delete tối đa 100 item / request
    int batchSize = 100;
    for (int i = 0; i < publicIds.size(); i += batchSize) {
      List<String> batch = publicIds.subList(i, Math.min(i + batchSize, publicIds.size()));
      try {
        Map<?, ?> result =
            cloudinary.api().deleteResources(batch, ObjectUtils.asMap("resource_type", "image"));
        log.info("Bulk deleted {} images from Cloudinary", batch.size());
      } catch (Exception e) {
        log.error("Failed to bulk delete images from Cloudinary, batch starting at index {}", i, e);
      }
    }
  }
}
