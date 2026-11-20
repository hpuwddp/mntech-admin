package com.mntech.shopadmin.service.impl;

import com.mntech.shopadmin.dto.response.UploadResponse;
import com.mntech.shopadmin.exception.BusinessException;
import com.mntech.shopadmin.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {
    @Value("${file.upload.path:/app/shop-admin/uploads/}")
    private String uploadPath;

    @Value("${file.upload.domain:http://pic.bafangjiuye.site}")
    private String domain;

    @Value("${file.upload.max-size:2097152}")  // 默认2MB
    private long maxFileSize;

    // 允许的图片格式
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "bmp"
    );

    /**
     * 单张图片上传
     */
    @Override
    public UploadResponse uploadImage(MultipartFile file) {
        validateFile(file);

        try {
            // 1. 生成存储路径
            String filePath = generateFilePath(file);

            // 2. 保存文件
            File destFile = new File(filePath);
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.transferTo(destFile);

            // 3. 构建响应
            String fileName = destFile.getName();
            String accessUrl = domain + "/uploads/" + getRelativePath(filePath);

            UploadResponse response = new UploadResponse();
            response.setUrl(accessUrl);
            response.setFilePath(filePath);
            response.setFileName(fileName);
            response.setFileSize(file.getSize());
            response.setFileType(file.getContentType());

            return response;

        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 批量图片上传
     */
    @Override
    public List<UploadResponse> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("请至少上传一张图片");
        }
        if (files.size() > 20) {
            throw new BusinessException("单次最多上传20张图片");
        }

        List<UploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                responses.add(uploadImage(file));
            }
        }
        return responses;
    }

    /**
     * 删除图片
     */
    @Override
    public void deleteImage(String fileUrl) {
        // 从URL中提取文件路径
        String relativePath = fileUrl.replace(domain + "/uploads/", "");
        String fullPath = uploadPath + relativePath;

        File file = new File(fullPath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new BusinessException("文件删除失败");
            }
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名无效");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("不支持的图片格式，支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /**
     * 生成文件存储路径
     */
    private String generateFilePath(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        return uploadPath + datePath + "/" + uniqueFileName;
    }

    /**
     * 获取相对路径（用于URL）
     */
    private String getRelativePath(String fullPath) {
        return fullPath.replace(uploadPath, "");
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
