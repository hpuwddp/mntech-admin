package com.mntech.shopadmin.service;

import com.mntech.shopadmin.dto.response.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {

    /**
     * 单张图片上传
     */
    public UploadResponse uploadImage(MultipartFile file);

    /**
     * 批量图片上传
     */
    public List<UploadResponse> uploadImages(List<MultipartFile> files);

    /**
     * 删除图片
     */
    public void deleteImage(String fileUrl);

}
