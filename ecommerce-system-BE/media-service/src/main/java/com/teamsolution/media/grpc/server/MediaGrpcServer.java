package com.teamsolution.media.grpc.server;

import com.teamsolution.media.storage.CloudinaryFolderResolver;
import com.teamsolution.media.storage.CloudinaryService;
import com.teamsolution.proto.grpc.media.DeleteImagesRequest;
import com.teamsolution.proto.grpc.media.DeleteImagesResponse;
import com.teamsolution.proto.grpc.media.ImageRequest;
import com.teamsolution.proto.grpc.media.ImageResponse;
import com.teamsolution.proto.grpc.media.MediaServiceGrpc;
import com.teamsolution.proto.grpc.media.UploadImagesRequest;
import com.teamsolution.proto.grpc.media.UploadImagesResponse;
import com.teamsolution.common.core.enums.media.CloudinaryFolderType;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class MediaGrpcServer extends MediaServiceGrpc.MediaServiceImplBase {

    private final CloudinaryService cloudinaryService;
    private final CloudinaryFolderResolver folderResolver;

    @Override
    public void uploadImages(UploadImagesRequest request,
            StreamObserver<UploadImagesResponse> observer) {
        try {
            String folder = folderResolver.getFolder(
                    CloudinaryFolderType.valueOf(request.getFolderType())
            );

            List<ImageResponse> responses = new ArrayList<>();

            for (ImageRequest imageData : request.getImagesList()) {
                MockMultipartFile file = new MockMultipartFile(
                        imageData.getOriginalFilename(),
                        imageData.getOriginalFilename(),
                        "image/*",
                        imageData.getData().toByteArray()
                );

                Map<String, Object> uploaded = cloudinaryService.uploadImage(file, folder);

                responses.add(ImageResponse.newBuilder()
                        .setPublicId(uploaded.get("publicId").toString())
                        .setUrl(uploaded.get("url").toString())
                        .build());
            }

            observer.onNext(UploadImagesResponse.newBuilder()
                    .addAllResults(responses)
                    .build());
            observer.onCompleted();

        } catch (Exception e) {
            log.error("Failed to upload images via gRPC", e);
            observer.onError(Status.INTERNAL
                    .withDescription("Upload failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteImages(DeleteImagesRequest request,
            StreamObserver<DeleteImagesResponse> observer) {
        try {
            cloudinaryService.deleteImages(request.getPublicIdsList());

            observer.onNext(DeleteImagesResponse.newBuilder()
                    .setSuccess(true)
                    .build());
            observer.onCompleted();

        } catch (Exception e) {
            log.error("Failed to delete images via gRPC", e);
            observer.onError(Status.INTERNAL
                    .withDescription("Delete failed: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}