package com.example.demo.controller;

import com.example.demo.entity.Friendship;
import com.example.demo.entity.TextData;
import com.example.demo.entity.Image;
import com.example.demo.entity.Member;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.TextDataRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {


    private final MemberRepository userRepository;

    private final ImageRepository imageRepository;


    private final TextDataRepository textDataRepository;

    private final S3Uploader s3Uploader;


    private final FriendshipRepository friendshipRepository;


    @GetMapping("/getImage")
    public ResponseEntity<?> getImage(@RequestParam("userId") Long userId) {
        Optional<Member> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User NOT FOUND");
        }

        String dirName = "images";
        List<String> userImages = s3Uploader.getUserUploadedFiles(userId.toString(), dirName);

        if (userImages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("IMAGES NOT FOUND");
        }
        return ResponseEntity.ok(userImages);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("userId") Long userId,
                                         @RequestParam("file") MultipartFile file) throws IOException {
        Optional<Member> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Member foundUser = userOptional.get();
        String imageUrl = s3Uploader.upload(file, foundUser, "images");

        Image image = new Image();
        image.setFilename(file.getOriginalFilename());
        image.setImageUrl(imageUrl);
        image.setUser(foundUser);  // 관계 설정

        imageRepository.save(image);

        // 친구 관계를 통해 친구에게도 이미지 공유
        List<Friendship> friendships = friendshipRepository.findByUser(foundUser);
        for (Friendship friendship : friendships) {
            Member friend = friendship.getFriend();
            String friendImageUrl = s3Uploader.upload(file, friend, "images"); // 친구의 이름으로 업로드

            Image friendImage = new Image();
            friendImage.setFilename(file.getOriginalFilename());
            friendImage.setImageUrl(imageUrl);
            friendImage.setUser(friend);
            imageRepository.save(friendImage);
        }

        return ResponseEntity.ok("Image uploaded successfully and shared with friends");
    }

    //text 가져오는 부분
    @GetMapping("/getText")
    public ResponseEntity<?> getText(@RequestParam("userId") Long userId) {
        Optional<Member> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User NOT FOUND");
        }

        Member user = userOptional.get();
        List<TextData> textDataList = textDataRepository.findByUser_Id(user.getId());

        if (textDataList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TEXT DATA NOT FOUND");
        }

        List<String> contents = new ArrayList<>();
        for (TextData textData : textDataList) {
            contents.add(textData.getContent());
        }

        return ResponseEntity.ok(contents);
    }


    @PostMapping("/saveText")
    public ResponseEntity<?> saveText(@RequestBody Map<String, String> request) {
        String userIdStr = request.get("userId");
        String content = request.get("content");

        if (userIdStr == null || content == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing userId or content");
        }

        Long userId = Long.parseLong(userIdStr);
        Optional<Member> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Member foundUser = user.get();
        TextData textData = new TextData();
        textData.setContent(content);
        textData.setUser(foundUser);
        textDataRepository.save(textData);

        // 친구 이름을 통해 같은 텍스트를 공유 (서로의 friendName이 일치하는 경우에만)
        Optional<Member> friend = userRepository.findByUsername(foundUser.getFriendName());
        if (friend.isPresent() && friend.get().getFriendName().equals(foundUser.getUsername())) {
            TextData friendTextData = new TextData();
            friendTextData.setContent(content);
            friendTextData.setUser(friend.get());
            textDataRepository.save(friendTextData);
        }

        return ResponseEntity.ok("Text saved successfully");
    }
}
