package com.kursova.getsun.service.impl;

import com.kursova.getsun.model.IsAliveResponseEntity;
import com.kursova.getsun.service.IsAliveService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class IsAliveServiceImpl implements IsAliveService {

    @Override
    public IsAliveResponseEntity processIsAlive() {
        return IsAliveResponseEntity.builder()
                .uuid(UUID.randomUUID())
                .status(true).info("OK")
                .dateCreated(LocalDateTime.now())
                .build();
    }
}
