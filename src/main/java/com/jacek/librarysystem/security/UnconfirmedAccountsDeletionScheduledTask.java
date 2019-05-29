package com.jacek.librarysystem.security;

import com.jacek.librarysystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnconfirmedAccountsDeletionScheduledTask {

    private final UserService userService;


}
