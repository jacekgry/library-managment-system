package com.jacek.librarysystem.service;

import com.jacek.librarysystem.model.User;

public interface UserService {
    void save(User user);
    User findByUsername(String username);
    void grantAccess(User owner, User userToBeGranted);
    void confirmEmail(String token);
}
