package com.mntech.shopadmin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String url;          // 完整访问URL
    private String filePath;     // 服务器存储路径
    private String fileName;     // 文件名
    private Long fileSize;       // 文件大小（字节）
    private String fileType;     // 文件类型
}