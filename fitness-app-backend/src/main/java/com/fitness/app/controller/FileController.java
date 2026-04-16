package com.fitness.app.controller;

import com.fitness.app.service.CosService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/upload/music")
    public ResponseEntity<String> uploadMusic(@RequestParam("file") MultipartFile file) {
        try {
            String url = cosService.uploadFile(file, "music");
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = cosService.uploadFile(file, "image");
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam("key") String key, HttpServletResponse response) {
        try {
            cosService.downloadFile(key, response);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }
}
