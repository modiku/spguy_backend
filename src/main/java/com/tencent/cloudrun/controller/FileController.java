package com.tencent.cloudrun.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    // 从配置文件读取上传目录
    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    // 从配置文件读取基础访问 URL
    @Value("${file.access.url:http://localhost:8080/uploads}")
    private String accessUrl;

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("上传失败，文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + ext;

        File dest = new File(new File(uploadDir).getAbsolutePath(), fileName);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        String fileUrl = accessUrl + "/" + fileName;

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("filename", fileName);
        return result;
    }
}