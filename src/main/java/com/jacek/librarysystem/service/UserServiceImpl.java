package com.jacek.librarysystem.service;

import com.jacek.librarysystem.model.User;
import com.jacek.librarysystem.model.VerificationToken;
import com.jacek.librarysystem.repository.UserRepository;
import com.jacek.librarysystem.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
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
    public void grantAccess(User owner, User userToBeGranted) {
        owner.getUsersGivenAccess().add(userToBeGranted);
        userRepository.save(owner);
    }

    @Override
    public void confirmEmail(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(new Date().compareTo(verificationToken.getExpiryDate()) < 0) {
            User user = verificationToken.getUser();
            user.setConfirmed(true);
            userRepository.save(user);
        }
    }


    private void generateTokenAndSendConfirmationEmail(User user){
        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Library system account confirmation");
        email.setText("Confirm your email address by clicking: " + "http://localhost:9090/confirm?token=" + token);

        javaMailSender.send(email);
    }

    private void createVerificationToken(User user, String token){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        Date expiryDate = calendar.getTime();

        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .expiryDate(expiryDate)
                .build();
        verificationTokenRepository.save(verificationToken);
    }

}
