package com.jacek.librarysystem.service;

import com.jacek.librarysystem.model.User;

public interface UserService {
    void save(User user);
    User findByUsername(String username);
}
