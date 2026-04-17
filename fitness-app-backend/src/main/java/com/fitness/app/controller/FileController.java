package com.fitness.app.controller;

import com.fitness.app.common.Result;
import com.fitness.app.service.CosService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    private final CosService cosService;

    @Autowired
    public FileController(CosService cosService) {
        this.cosService = cosService;
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = cosService.uploadFileByType(file);
        return Result.success(url);
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        cosService.downloadFile(key, response);
    }
}
