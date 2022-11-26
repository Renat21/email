package com.mail.demo.service;

import com.mail.demo.entity.User;
import com.mail.demo.enumer.Role;
import com.mail.demo.repository.FriendRepository;
import com.mail.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class userService - класс для основных операций над пользователем
 **/
@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageService imageService;

    private static final int USER_PAGE_SIZE=10;
    private static final int INVITE_PAGE_SIZE=5;

    @Autowired
    private FriendRepository friendService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    public void saveUser(User user){
        userRepository.save(user);
    }

    public String registerUser(User user, String confirmPassword, RedirectAttributes redirectAttributes){
        if (!confirmPassword.equals(user.getPassword())){
            redirectAttributes.addFlashAttribute("confirmPasswordError", true);
            return "redirect:/registration";
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        saveUser(user);
        return "redirect:/login";
    }

    public String changeProfile(RedirectAttributes redirectAttributes, User user,
                                String username, String name, String surname) {

        User newUser = userRepository.findByUsername(username);
        if (userRepository.findByUsername(username) != null) {
            redirectAttributes.addFlashAttribute("errorCurrentUsername", true);
            return "redirect:/profile";
        }
        else
            user.setUsername(username);

        user.setName(name);
        user.setSurname(surname);
        redirectAttributes.addFlashAttribute("profileChanged", true);
        saveUser(user);
        return "redirect:/profile";
    }

    public String changePassword(User user, String currentPassword, String newPassword,
                                 String passwordConfirm, RedirectAttributes redirectAttributes) {
        if (!Objects.equals(currentPassword, "") && !Objects.equals(newPassword, "")
                && !Objects.equals(passwordConfirm, "")) {
            if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("errorCurrentPassword", true);
                return "redirect:/profile";
            }

            if (!Objects.equals(newPassword, passwordConfirm)) {
                redirectAttributes.addFlashAttribute("errorPassword", true);
                return "redirect:/profile";
            }

            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            redirectAttributes.addFlashAttribute("passwordChanged", true);
            saveUser(user);

        }
        return "redirect:/profile";
    }

    public String changePhoto(RedirectAttributes redirectAttributes,
                              MultipartFile file, @AuthenticationPrincipal User user) throws IOException {
        imageService.saveImage(file, user);
        redirectAttributes.addFlashAttribute("photoChanged", true);
        return "redirect:/profile";
    }


    @Transactional
    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    @Transactional
    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User getUserAuth() {

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional
    public Page<User> findFriendUsers(User user, int page) {
        return userRepository.findFriendUsers(user, PageRequest.of(page, USER_PAGE_SIZE));
    }

    public User findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    @Transactional
    public Page<User> findFriendUsersWithSearch(User user, String searchLine, int page) {
        return userRepository.findFriendUsersWithSearch(user, searchLine, PageRequest.of(page, USER_PAGE_SIZE));
    }

    @Transactional
    public List<User> findStrangers(User user, int page) {
        return userRepository.findStrangers(user, PageRequest.of(page, USER_PAGE_SIZE));
    }

    @Transactional
    public List<User> findStrangersWithSearch(User user, String searchLine, int page) {
        return userRepository.findStrangersWithSearch(user, searchLine, PageRequest.of(page, USER_PAGE_SIZE));
    }

//    public List<Object> findPossibleAndMutualFriends(User user){
//        List<Object> allInformation = new ArrayList<>();
//        List<User> possibleFriends = findPossibleFriendsByMutualFriends(user).stream().filter(elem->
//                userRepository.findMutualFriends(user.getId(), elem.getId()) > 0).collect(Collectors.toList());
//
//        allInformation.add(possibleFriends);
//        allInformation.add(findMutualFriends(user.getId(), possibleFriends));
//        return allInformation;
//    }
//
//    @Transactional
//    public List<User> findPossibleFriendsByMutualFriends(User user){
//        return userRepository.findPossibleFriendsByMutualFriends(user,
//                friendService.getAcceptedFriends(user.getUsername()).stream().map(User::getId).collect(Collectors.toList()));
//    }
//
//    @Transactional
//    public List<Long> findMutualFriends(Long userId, List<User> possibleFriends){
//        return possibleFriends.stream().map(possibleFriend -> userRepository.findMutualFriends(
//                userId, possibleFriend.getId()
//        )).toList();
//    }

    @Transactional
    public List<User> fiendReceivedInvites(User user, int page) {
        return userRepository.fiendReceivedInvites(user, PageRequest.of(page, INVITE_PAGE_SIZE));
    }

    @Transactional
    public List<User> fiendReceivedInvitesWithSearch(User user, String searchLine, int page) {
        return userRepository.fiendReceivedInvitesWithSearch(user, searchLine, PageRequest.of(page, INVITE_PAGE_SIZE));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }
}
