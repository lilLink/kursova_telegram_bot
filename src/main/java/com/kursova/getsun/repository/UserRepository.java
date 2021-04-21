package com.kursova.getsun.repository;


import com.kursova.getsun.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
