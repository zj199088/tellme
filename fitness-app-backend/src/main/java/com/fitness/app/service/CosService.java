package com.fitness.app.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class CosService {

    private final COSClient cosClient;

    @Value("${cos.bucket-name}")
    private String bucketName;

    @Value("${cos.access-domain}")
    private String accessDomain;

    // 图片文件扩展名
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg", ".ico"
    );

    // 视频文件扩展名
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
        ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv", ".webm"
    );

    // 音乐文件扩展名
    private static final List<String> MUSIC_EXTENSIONS = Arrays.asList(
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a"
    );

    // 文档文件扩展名
    private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".rtf"
    );

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
        // 设置文件访问权限为公共读
        putObjectRequest.setCannedAcl(com.qcloud.cos.model.CannedAccessControlList.PublicRead);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

        return accessDomain + "/" + key;
    }

    public String uploadFileByType(MultipartFile file) throws IOException {
        String folder = determineFolder(file);
        return uploadFile(file, folder);
    }

    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, "image");
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        return uploadFile(file, "video");
    }

    public String uploadMusic(MultipartFile file) throws IOException {
        return uploadFile(file, "music");
    }

    public String uploadDocument(MultipartFile file) throws IOException {
        return uploadFile(file, "files");
    }

    public String uploadOther(MultipartFile file) throws IOException {
        return uploadFile(file, "other");
    }

    private String determineFolder(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "other";
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        if (IMAGE_EXTENSIONS.contains(fileExtension)) {
            return "image";
        } else if (VIDEO_EXTENSIONS.contains(fileExtension)) {
            return "video";
        } else if (MUSIC_EXTENSIONS.contains(fileExtension)) {
            return "music";
        } else if (DOCUMENT_EXTENSIONS.contains(fileExtension)) {
            return "files";
        } else {
            return "other";
        }
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
