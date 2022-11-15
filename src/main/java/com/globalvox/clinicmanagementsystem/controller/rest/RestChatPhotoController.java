package com.globalvox.clinicmanagementsystem.controller.rest;


import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api")
public class RestChatPhotoController {

    @Value("${message.photo.path}")
    private String messagePhotoPath;

    @CrossOrigin(value = "*")
    @RequestMapping(value = "/file", headers = "Content-Type= multipart/form-data", method = RequestMethod.POST)
    public String uploadImages(@RequestPart("photo") MultipartFile multipartFile,@RequestParam(value = "conversationId") Long conversationId) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        /* Checking Image Uploaded or not */
        if (fileName != null){
            /* file uploaded then taking name and upload to conversation directory */
            String uploadDir = messagePhotoPath + conversationId;
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                /* Check directory Exist or not  */
                Files.createDirectories(uploadPath);
            }

            /* Upload Photo to Directory if there exception also Handle exception */
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                System.out.println(filePath.toString());
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (NotFoundException ex) {
                throw new NotFoundException("Could Not Save Image ");
            }
            return fileName;
        }
        return  null;
    }
}
