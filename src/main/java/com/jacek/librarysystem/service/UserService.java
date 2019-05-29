package com.jacek.librarysystem.service;

import com.jacek.librarysystem.model.User;

import java.util.Set;

public interface UserService {
    void save(User user);
    User findByUsername(String username);
    void confirmEmail(String token);
    void deleteUnconfirmedUsers();
    Set<User> findUsersWhoGaveAccessTo(User loggedInUser);
    void grantAccess(User owner, String guestEmail);
    void acceptInvitation(User user, String token);
}
