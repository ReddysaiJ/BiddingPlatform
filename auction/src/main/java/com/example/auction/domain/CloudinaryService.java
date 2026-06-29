package com.example.auction.domain;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.auction.domain.models.SignedUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.folder}")
    private String folder;

    @Value("${cloudinary.max-images}")
    private int maxImages;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public SignedUploadResponse generateSignedUrl() {
        return generateSignedUrl(UUID.randomUUID().toString());
    }

    public List<SignedUploadResponse> generateSignedUrls(UUID auctionUid, int count) {
        if (count < 1 || count > maxImages)
            throw new IllegalArgumentException(
                    "Image count must be between 1 and " + maxImages);

        List<SignedUploadResponse> responses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            responses.add(generateSignedUrl(UUID.randomUUID().toString()));
        }
        return responses;
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image: " + publicId, e);
        }
    }

    public void deleteImages(List<String> publicIds) {
        publicIds.forEach(this::deleteImage);
    }


    private SignedUploadResponse generateSignedUrl(String imageId) {
        try {
            long timestamp = System.currentTimeMillis() / 1000L;
            String publicId = folder + "/auction/" + imageId;

            Map<String, Object> params = Map.of(
                    "timestamp", timestamp,
                    "public_id", publicId,
                    "folder",    folder
            );

            String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);
            return new SignedUploadResponse(
                    signature,
                    apiKey,
                    timestamp,
                    cloudName,
                    folder,
                    publicId,
                    "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload"
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Cloudinary signed URL", e);
        }
    }
}