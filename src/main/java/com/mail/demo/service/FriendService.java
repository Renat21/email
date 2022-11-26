package com.mail.demo.service;

import com.mail.demo.entity.Friend;
import com.mail.demo.entity.User;
import com.mail.demo.enumer.InviteStatus;
import com.mail.demo.repository.FriendRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class friendService - класс для основных операций над друзьями пользователя
 **/
@Service
@Slf4j
public class FriendService {

    @Autowired
    private UserService userService;
    @Autowired
    private FriendRepository friendRepository;


    public String redirectToFriendListOrToProfile(String username, String where) {
        if (where != null)
            return "redirect:/" + where + "/friendList/1";
        return "redirect:/user/" + username;
    }

    @Transactional
    public String sendInvite(String username, String where) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (existsByFirstUserAndSecondUser(userFromSession, friendUser) || existsByFirstUserAndSecondUser(friendUser, userFromSession)) {
            log.error("user is already in friend list");
            return redirectToFriendListOrToProfile(username, where);
        }
        Friend friend = new Friend(userFromSession, friendUser, InviteStatus.PENDING);
        save(friend);
        return redirectToFriendListOrToProfile(username, where);
    }

    @Transactional
    public String resultInvite(String username, int result, String where) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (!existsByFirstUserAndSecondUser(userFromSession, friendUser) && !existsByFirstUserAndSecondUser(friendUser, userFromSession)) {
            log.error("users are not friends");
            return redirectToFriendListOrToProfile(username, where);
        }
        Friend friend = findLinkedFriends(userFromSession, friendUser);

        switch (result) {
            case 1 -> {
                friend.setInviteStatus(InviteStatus.ACCEPTED);
                save(friend);
            }
            case 2 -> friendRepository.delete(friend);
            default -> {
            }
        }
        return redirectToFriendListOrToProfile(username, where);
    }

    @Transactional
    public String deleteFriend(String username, String where) {

        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        if (friendUser == null) {
            log.error("no such user");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (!existsByFirstUserAndSecondUser(userFromSession, friendUser) && !existsByFirstUserAndSecondUser(friendUser, userFromSession)) {
            log.error("users are not friends");
            return redirectToFriendListOrToProfile(username, where);
        }
        if (existsByFirstUserAndSecondUser(userFromSession, friendUser)) {
            deleteFriendByFirstUserAndSecondUser(userFromSession, friendUser);
        } else {
            deleteFriendByFirstUserAndSecondUser(friendUser, userFromSession);
        }
        return redirectToFriendListOrToProfile(username, where);
    }

    @Transactional
    public Friend findLinkedFriends(User firstUser, User secondUser) {
        if (existsByFirstUserAndSecondUser(firstUser, secondUser)) {
            return findFriendByFirstUserAndSecondUser(firstUser, secondUser);
        } else {
            return findFriendByFirstUserAndSecondUser(secondUser, firstUser);
        }
    }

    public List<User> getFriends(String username) {
        User userFromSession = userService.findUserByUsername(username);
        List<Friend> friendsByFirstUser = findFriendsByFirstUser(userFromSession);
        List<Friend> friendsBySecondUser = findFriendsBySecondUser(userFromSession);
        return getUsers(friendsByFirstUser, friendsBySecondUser);
    }

    private List<User> getUsers(List<Friend> friendsByFirstUser, List<Friend> friendsBySecondUser) {
        List<User> friends = new ArrayList<>();
        for (Friend friend : friendsByFirstUser) {
            friends.add(userService.findUserByUsername(friend.getSecondUser().getUsername()));
        }
        for (Friend friend : friendsBySecondUser) {
            friends.add(userService.findUserByUsername(friend.getFirstUser().getUsername()));
        }
        return friends;
    }

    public List<User> getAcceptedFriends(String username) {
        User userFromSession = userService.findUserByUsername(username);
        List<Friend> friendsByFirstUser = findFriendsByFirstUser(userFromSession).stream().filter(x -> x.getInviteStatus() == InviteStatus.ACCEPTED).toList();
        List<Friend> friendsBySecondUser = findFriendsBySecondUser(userFromSession).stream().filter(x -> x.getInviteStatus() == InviteStatus.ACCEPTED).toList();
        return getUsers(friendsByFirstUser, friendsBySecondUser);
    }

    @Transactional
    public boolean isFriends(String username) {
        List<User> friends = getFriends(userService.getUserAuth().getUsername());
        for (User friendUser : friends) {
            if (friendUser.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean checkFriendStatus(String username) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Friend friend = findLinkedFriends(userFromSession, friendUser);
        if (friend == null) {
            return false;
        }
        return isFriends(username) && friend.getInviteStatus() == InviteStatus.ACCEPTED;
    }

    @Transactional
    public boolean isInviteReceived(String username) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Friend friend = findLinkedFriends(userFromSession, friendUser);
        if (friend == null) {
            return false;
        }
        return friend.getSecondUser().equals(userFromSession) && friend.getInviteStatus() == InviteStatus.PENDING;
    }

    @Transactional
    public boolean isInviteSend(String username) {
        User userFromSession = userService.getUserAuth();
        User friendUser = userService.findUserByUsername(username);
        Friend friend = findLinkedFriends(userFromSession, friendUser);
        if (friend == null) {
            return false;
        }
        return friend.getFirstUser().getUsername().equals(userFromSession.getUsername()) && friend.getInviteStatus() == InviteStatus.PENDING;
    }

    public void addPossibleFriend(String username){
        if (isInviteReceived(username))
            resultInvite(username, 1, null);
        else
            sendInvite(username, null);
    }

    @Transactional
    public boolean existsByFirstUserAndSecondUser(User firstUser, User secondUser) {
        return friendRepository.existsByFirstUserAndSecondUser(firstUser, secondUser);
    }

    @Transactional
    public Friend findFriendByFirstUserAndSecondUser(User firstUser, User secondUser) {
        return friendRepository.findFriendByFirstUserAndSecondUser(firstUser, secondUser);
    }

    @Transactional
    public void deleteFriendByFirstUserAndSecondUser(User firstUser, User secondUser) {
        friendRepository.deleteFriendByFirstUserAndSecondUser(firstUser, secondUser);
    }

    @Transactional
    public List<Friend> findFriendsByFirstUser(User firstUser) {
        return friendRepository.findFriendsByFirstUser(firstUser);
    }

    @Transactional
    public List<Friend> findFriendsBySecondUser(User secondUser) {
        return friendRepository.findFriendsBySecondUser(secondUser);
    }


    @Transactional
    public void save(Friend friend) {
        friendRepository.save(friend);
    }

    public List<Boolean> getInviteSendFriends(List<User> friends) {
        return friends.stream().map(friend -> isInviteSend(friend.getUsername())).collect(Collectors.toList());
    }


    @Transactional
    public List<Object> findFriendsAndStrangers(String username, String searchLine, int page) {

        User currentUser = userService.findUserByUsername(username);
        List<Object> response = new ArrayList<>();
        List<Boolean> isInviteSendStrangers = new ArrayList<>();
        Page<User> friendUsers;
        List<User> profilesOfStrangers = new ArrayList<>();

        if (Objects.equals(searchLine, "")) {
            friendUsers = userService.findFriendUsers(currentUser, page);
            if (friendUsers.isLast()) {
                profilesOfStrangers = userService.findStrangers(currentUser, friendUsers.getTotalPages() == 0 ? page : page - friendUsers.getTotalPages() + 1);
                isInviteSendStrangers = getInviteSendFriends(profilesOfStrangers);
            }
        } else {
            friendUsers = userService.findFriendUsersWithSearch(currentUser, searchLine, page);

            if (friendUsers.isLast()) {
                profilesOfStrangers = userService.findStrangersWithSearch(currentUser, searchLine,
                        friendUsers.getTotalPages() == 0 ? page : page - friendUsers.getTotalPages() + 1);
                isInviteSendStrangers = getInviteSendFriends(profilesOfStrangers);
            }
        }
        response.add(friendUsers.toList());
        response.add(profilesOfStrangers);
        response.add(isInviteSendStrangers);
        return response;
    }

    public List<User> findSuggestions(String username, String searchLine, int page) {
        List<User> usersReceived;
        User currentUser = userService.findUserByUsername(username);
        if (Objects.equals(searchLine, ""))
            usersReceived = userService.fiendReceivedInvites(currentUser, page);
        else
            usersReceived = userService.fiendReceivedInvitesWithSearch(currentUser, searchLine, page);
        return usersReceived;
    }

    public String clearSearchLine(String searchLine) {
        return searchLine.replaceAll("[^A-Za-zА-Яа-я0-9 ]", "");
    }
}
