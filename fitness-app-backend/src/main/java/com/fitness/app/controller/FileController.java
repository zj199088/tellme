package com.fitness.app.controller;

import com.fitness.app.common.Result;
import com.fitness.app.service.CosService;
import com.fitness.app.service.AppConfigService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    private final CosService cosService;
    private final AppConfigService appConfigService;

    @Autowired
    public FileController(CosService cosService, AppConfigService appConfigService) {
        this.cosService = cosService;
        this.appConfigService = appConfigService;
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        // 检查文件大小
        int maxFileSizeMB = appConfigService.getNumberConfig("max_file_size_mb");
        if (file.getSize() > maxFileSizeMB * 1024 * 1024) {
            return Result.error("文件大小超过限制，最大允许" + maxFileSizeMB + "MB");
        }
        
        String url = cosService.uploadFileByType(file);
        return Result.success(url);
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        cosService.downloadFile(key, response);
    }
}
