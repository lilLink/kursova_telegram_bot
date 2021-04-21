package com.kursova.suncheck.repository;


import com.kursova.suncheck.model.UserConfig;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {

}
