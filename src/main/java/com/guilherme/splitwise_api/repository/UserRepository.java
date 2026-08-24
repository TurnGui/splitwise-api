package com.guilherme.splitwise_api.repository;

import com.guilherme.splitwise_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}