package com.jacek.librarysystem.service;

import com.jacek.librarysystem.dto.UserDto;
import com.jacek.librarysystem.model.InvitationToLibrary;
import com.jacek.librarysystem.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    void save(UserDto user);

    User findByUsername(String username);

    void confirmEmail(String token);

    void deleteUnconfirmedUsers();

    Set<User> findUsersWhoGaveAccessToTheirLibrariesAndConfirmed(User loggedInUser);

    Set<InvitationToLibrary> findSentInvitations(User user);

    void grantAccess(User owner, String guestEmail);

    void acceptInvitation(User user, String token);

    List<User> findAllByUsernameIsNotAndConfirmed(String username, boolean confirmed);

    void cancelInvitation(User loggedInUser, Long invId);
}

