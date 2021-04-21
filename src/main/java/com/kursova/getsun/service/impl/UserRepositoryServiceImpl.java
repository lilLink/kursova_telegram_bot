package com.kursova.getsun.service.impl;


import com.kursova.getsun.model.User;
import com.kursova.getsun.repository.UserRepository;
import com.kursova.getsun.service.UserRepositoryService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> save(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return this.findAll().stream().filter(x -> x.getChatId() == chatId)
                                      .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.findAll().stream().filter(x -> x.getUsername().equals(username))
                                      .findFirst();
    }

    @Override
    public void deleteByChatId(Long chatId) {
        Long foundId = this.findByChatId(chatId).orElseThrow(RuntimeException::new)
                           .getId();
        userRepository.deleteById(foundId);
    }
}
