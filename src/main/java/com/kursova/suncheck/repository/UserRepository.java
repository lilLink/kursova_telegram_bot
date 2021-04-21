package com.kursova.suncheck.repository;


import com.kursova.suncheck.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
