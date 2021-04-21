package com.kursova.suncheck.service.impl;

import com.kursova.suncheck.model.IsAliveResponseEntity;
import com.kursova.suncheck.service.IsAliveService;

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
