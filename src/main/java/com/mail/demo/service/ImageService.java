package com.mail.demo.service;

import com.mail.demo.entity.Image;
import com.mail.demo.entity.User;
import com.mail.demo.repository.ImageRepository;
import com.mail.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class ImageService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Image toImageEntity(MultipartFile file) throws IOException {
        return new Image(file.getName(), file.getOriginalFilename(), file.getSize(), file.getContentType(),
                file.getBytes());
    }

    public void saveImage(MultipartFile file, User user) throws IOException {
        if (file.getSize() != 0) {
            if (user.getImage() != null) {
                Image oldImage = user.getImage();
                Image img = toImageEntity(file);
                user.setImage(img);
                userRepository.save(user);
                imageRepository.deleteById(oldImage.getId());
            } else {
                user.setImage(toImageEntity(file));
                userRepository.save(user);
            }
        }
    }

    public void save(Image image) {
        imageRepository.save(image);
    }


    public Image findImageById(Long id){
        return imageRepository.findImageById(id);
    }

}
