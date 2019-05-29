package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
    List<VerificationToken> findAllByExpiryDateAfter(Date date);
}
