package com.fitness.app.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class CosService {

    private final COSClient cosClient;

    @Value("${cos.bucket-name}")
    private String bucketName;

    @Value("${cos.access-domain}")
    private String accessDomain;

    public CosService(COSClient cosClient) {
        this.cosClient = cosClient;
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String key = folder + "/" + UUID.randomUUID() + fileExtension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

        return accessDomain + "/" + key;
    }

    public void downloadFile(String key, HttpServletResponse response) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        try (InputStream inputStream = cosClient.getObject(getObjectRequest).getObjectContent();
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        }
    }
}
