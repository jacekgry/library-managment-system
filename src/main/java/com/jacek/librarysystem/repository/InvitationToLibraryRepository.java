package com.jacek.librarysystem.repository;

import com.jacek.librarysystem.model.InvitationToLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationToLibraryRepository extends JpaRepository<InvitationToLibrary, Long> {
    Optional<InvitationToLibrary> findByToken(String token);
}
