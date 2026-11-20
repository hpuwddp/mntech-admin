package com.mntech.shopadmin.controller;

import com.mntech.shopadmin.dto.response.ApiResponse;
import com.mntech.shopadmin.dto.response.UploadResponse;
import com.mntech.shopadmin.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    @Autowired
    private IFileService fileService;

    /**
     * 单张图片上传
     */
    @PostMapping("/image")
    public ApiResponse<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        UploadResponse response = fileService.uploadImage(file);
        return ApiResponse.success("上传成功", response);
    }

    /**
     * 批量图片上传
     */
    @PostMapping("/images")
    public ApiResponse<List<UploadResponse>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<UploadResponse> responses = fileService.uploadImages(files);
        return ApiResponse.success("上传成功", responses);
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/image")
    public ApiResponse<Void> deleteImage(@RequestParam @NotBlank String fileUrl) {
        fileService.deleteImage(fileUrl);
        return ApiResponse.success("删除成功", null);
    }
}
