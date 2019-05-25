package com.jacek.librarysystem.security;

import com.jacek.librarysystem.model.User;

public interface SecurityService {
    String findLoggedInUsername();
    void autologin(String username, String password);
    User findLoggedInUser();
}
