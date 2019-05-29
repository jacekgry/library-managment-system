package com.jacek.librarysystem.service;

import com.jacek.librarysystem.exception.InvitationNotFoundException;
import com.jacek.librarysystem.exception.TokenNotFoundException;
import com.jacek.librarysystem.model.InvitationToLibrary;
import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.model.VerificationToken;
import com.jacek.librarysystem.repository.InvitationToLibraryRepository;
import com.jacek.librarysystem.repository.UserRepository;
import com.jacek.librarysystem.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final InvitationToLibraryRepository invitationToLibraryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;


    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(new Date());
        userRepository.save(user);
        generateTokenAndSendConfirmationEmail(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void grantAccess(User owner, String guestEmail) {
        InvitationToLibrary invitation = InvitationToLibrary.builder()
                .owner(owner)
                .email(guestEmail)
                .token(UUID.randomUUID().toString())
                .build();
        sendMailWithInvitation(invitation);
        invitationToLibraryRepository.save(invitation);
    }

    @Override
    @Transactional
    public void acceptInvitation(User user, String token) {
        InvitationToLibrary invitation = invitationToLibraryRepository.findByToken(token)
                .orElseThrow(() -> new InvitationNotFoundException("Invitation not found"));
        if(!user.getEmail().equals(invitation.getEmail())){
            throw new AccessDeniedException("You are not allowed to accept this invitation");
        }
        invitation.setConfirmed(true);
        invitation.setGuest(user);
        invitationToLibraryRepository.save(invitation);
    }

    @Override
    @Transactional
    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token expired or never existed"));

        User user = verificationToken.getUser();
        user.setConfirmed(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    @Scheduled(fixedRate = 40000)
    @Transactional
    public void deleteUnconfirmedUsers() {
        log.info("Deleting unconfirmed accounts...");
        List<VerificationToken> tokens = verificationTokenRepository.
                findAllByExpiryDateAfter(new Date());
        for (VerificationToken token : tokens) {
            verificationTokenRepository.delete(token);
            userRepository.delete(token.getUser());
        }
    }

    @Override
    public Set<User> findUsersWhoGaveAccessTo(User user) {
        return user.getInvitationsReceived()
                .stream()
                .filter(InvitationToLibrary::isConfirmed)
                .map(InvitationToLibrary::getOwner)
                .collect(Collectors.toSet());
    }


    private void generateTokenAndSendConfirmationEmail(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = createVerificationToken(user, token);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Library system account confirmation");
        email.setText("Confirm your email address by clicking: " + "http://localhost:9090/confirm?token=" + token + "\n"
                + "Token expiry date: " + verificationToken.getExpiryDate().toString());

        javaMailSender.send(email);
    }

    private VerificationToken createVerificationToken(User user, String token) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 60);
        Date expiryDate = calendar.getTime();

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .expiryDate(expiryDate)
                .build();
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    private void sendMailWithInvitation(InvitationToLibrary invitation) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(invitation.getEmail());
        email.setSubject("Invitation to library");
        email.setText(writeInvitationMailText(invitation));
        javaMailSender.send(email);
    }

    private String writeInvitationMailText(InvitationToLibrary invitation){
        StringBuilder sb = new StringBuilder();
        String url = "http://localhost:9090/accept?token=" + invitation.getToken();
        sb.append("Library owner ")
                .append(invitation.getOwner().getUsername())
                .append("\n invites you to his library! \n");
        Optional<User> userOptional = userRepository.findByEmail(invitation.getEmail());
        if (!userOptional.isPresent()){
            User user = userOptional.get();
            if(!user.isConfirmed()){
                sb.append("Confirm your account and then accept invitation by clicking the link: \n");
            }
            else{
                sb.append("Create account and then accept the invitation by clicking the link: \n");
            }
        }
        else{
            sb.append("Accept the invitation by clicking the link:\n");
        }
        sb.append(url);
        return sb.toString();
    }

}

