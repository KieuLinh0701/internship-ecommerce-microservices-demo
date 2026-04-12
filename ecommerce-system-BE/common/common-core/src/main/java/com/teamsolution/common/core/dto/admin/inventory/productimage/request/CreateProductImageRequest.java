package com.teamsolution.common.core.dto.admin.inventory.productimage.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateProductImageRequest(List<MultipartFile> files) {}
