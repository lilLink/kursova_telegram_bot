package com.kursova.getsun.service.impl;

import com.kursova.getsun.model.User;
import com.kursova.getsun.model.UserConfig;
import com.kursova.getsun.repository.UserConfigRepository;
import com.kursova.getsun.service.UserRepositoryService;
import com.kursova.getsun.service.UserService;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryService userRepoService;
    private final UserConfigRepository userConfigRepository;

    @Override
    public void register(Update update) {
        User newUser = new User();
        newUser.setChatId(update.getMessage().getChatId());
        newUser.setUsername(update.getMessage().getFrom().getUserName());
        try {
            userRepoService.save(newUser).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            log.error("User registration failed", e);
        }
    }

    @Override
    public void remove(Update update) {
        Long userChatId = update.getMessage().getChatId();
        UserConfig userConfig = userConfigRepository.findAll().stream()
                .filter(c -> c.getUser().getChatId() == userChatId)
                .findFirst().orElse(null);
        try {
            if (userConfig != null) {
                userConfigRepository.delete(userConfig);
            }
            if (this.checkIfUserRegistered(userChatId)) {
                userRepoService.deleteByChatId(userChatId);
            }
        } catch (Exception e) {
            userConfigRepository.save(userConfig);
            log.error("User removing failed", e);
        }
    }

    @Override
    public boolean checkIfUserRegistered(long chatId) {
        return userRepoService.findByChatId(chatId).isPresent();
    }
}
