package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    List<User> findAllByUsernameIsNotAndConfirmed(String username, boolean confirmed);
}
