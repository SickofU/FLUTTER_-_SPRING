package com.example.demo.service;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.example.demo.entity.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;
import java.util.List;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, Member user, String dirName) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String userId = user.getId().toString(); // User 객체로부터 userId 가져오기

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        // 오리지널 파일 이름 올려야 tmp로 안바뀌고 정상적인 파일 올릴 수 있음
        String fileName = dirName + "/" + userId + "/" + originalFilename;
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // 업로드한 파일 가져오기
    public List<String> getUserUploadedFiles(String userId, String dirName) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(dirName + "/" + userId + "/");

        ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
        List<String> fileUrls = new ArrayList<>();
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            // objectSummary.getKey()를 사용하여 파일 키 가져오기
            String fileUrl = amazonS3.getUrl(bucket, objectSummary.getKey()).toString();
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            System.out.println("파일이 삭제되었습니다.");
        } else {
            System.out.println("파일이 삭제되지 않았습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = File.createTempFile("upload-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        }
        return Optional.of(convertFile);
    }

}
