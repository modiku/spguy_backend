package com.tencent.cloudrun.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("上传失败，文件为空");
        }

        // 获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + ext;

        // 保存到本地
        File dest = new File(uploadDir, fileName);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // 返回文件访问 URL
        return accessUrl + "/" + fileName;
    }
}