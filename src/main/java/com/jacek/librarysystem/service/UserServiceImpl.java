package com.jacek.librarysystem.service;

import com.jacek.librarysystem.dto.UserDto;
import com.jacek.librarysystem.exception.EmailTakenException;
import com.jacek.librarysystem.exception.InvitationNotFoundException;
import com.jacek.librarysystem.exception.TokenNotFoundException;
import com.jacek.librarysystem.exception.UsernameTakenException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Transactional
    public void save(UserDto userDto) {

        if(userRepository.findByUsername(userDto.getUsername()).isPresent()){
            throw new UsernameTakenException();
        }
        if(userRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new EmailTakenException();
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setRegistrationDate(new Date());
        user.setConfirmed(false);

        userRepository.save(user);
        generateTokenAndSendConfirmationEmail(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void grantAccess(User owner, String guestEmail) {
        InvitationToLibrary invitation = InvitationToLibrary.builder()
                .owner(owner)
                .email(guestEmail)
                .token(UUID.randomUUID().toString())
                .sent(false)
                .build();

        invitationToLibraryRepository.save(invitation);

        new Thread(() -> {
            try {
                sendMailWithInvitation(invitation);
                invitation.setSent(true);
                invitationToLibraryRepository.save(invitation);
            } catch (Exception e) {
                invitationToLibraryRepository.delete(invitation);
                log.warn(e.getStackTrace().toString());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    @Transactional
    public void acceptInvitation(User user, String token) {
        InvitationToLibrary invitation = invitationToLibraryRepository.findByToken(token)
                .orElseThrow(() -> new InvitationNotFoundException("Invitation not found"));
        if (!user.getEmail().equals(invitation.getEmail())) {
            throw new AccessDeniedException("You are not allowed to accept this invitation");
        }
        invitation.setConfirmed(true);
        invitation.setGuest(user);
        invitationToLibraryRepository.save(invitation);
    }

    @Override
    public List<User> findAllByUsernameIsNotAndConfirmed(String username, boolean confirmed) {
        return userRepository.findAllByUsernameIsNotAndConfirmed(username, confirmed);
    }

    @Override
    @Transactional
    public void cancelInvitation(User loggedInUser, Long invId) {
        InvitationToLibrary invitation = invitationToLibraryRepository.findById(invId)
                .orElseThrow(() -> new InvitationNotFoundException("Invitation not found"));
        if (invitation.getOwner().equals(loggedInUser)) {
            invitationToLibraryRepository.delete(invitation);
        } else {
            throw new AccessDeniedException("You are no owner of this invitation!");
        }
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

    @Scheduled(fixedRate = 120_000)
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
    public Set<User> findUsersWhoGaveAccessToTheirLibrariesAndConfirmed(User user) {
        return user.getInvitationsReceived()
                .stream()
                .filter(InvitationToLibrary::isConfirmed)
                .map(InvitationToLibrary::getOwner)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<InvitationToLibrary> findSentInvitations(User user) {
        return user.getInvitationsSent();
    }

    private void generateTokenAndSendConfirmationEmail(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = createVerificationToken(user, token);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Library system account confirmation");
        email.setText("Confirm your email address by clicking: " + "http://localhost:9090/confirm?token=" + token + "\n"
                + "Token expiry date: " + verificationToken.getExpiryDate().toString());

        new Thread(() -> {
            try {
                javaMailSender.send(email);
            } catch (Exception e) {
                verificationTokenRepository.delete(verificationToken);
            }
        }).start();

    }

    private VerificationToken createVerificationToken(User user, String token) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
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

    private String writeInvitationMailText(InvitationToLibrary invitation) {
        StringBuilder sb = new StringBuilder();
        String url = "http://localhost:9090/accept?token=" + invitation.getToken();
        sb.append("Library owner ")
                .append(invitation.getOwner().getUsername())
                .append("\n invites you to his library! \n");
        Optional<User> userOptional = userRepository.findByEmail(invitation.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isConfirmed()) {
                sb.append("Confirm your account and then accept invitation by clicking the link: \n");
            } else {
                sb.append("Accept the invitation by clicking the link:\n");
            }
        } else {
            sb.append("Create account and then accept the invitation by clicking the link: \n");
        }
        sb.append(url);
        return sb.toString();
    }

}

