package com.mail.demo.service;

import com.mail.demo.entity.Image;
import com.mail.demo.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class ImageService {
    @Autowired
    private UserService userService;

    @Autowired
    private ImageRepository imageRepository;

    public Image toImageEntity(MultipartFile file) throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes());
    }

    public void save(Image image) {
        imageRepository.save(image);
    }


    public Image findImageById(Long id){
        return imageRepository.findImageById(id);
    }

}
