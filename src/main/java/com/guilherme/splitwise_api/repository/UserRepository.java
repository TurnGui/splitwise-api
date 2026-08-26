package com.guilherme.splitwise_api.repository;

import com.guilherme.splitwise_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> id(Long id);
}