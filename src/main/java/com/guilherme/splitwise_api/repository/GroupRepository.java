package com.guilherme.splitwise_api.repository;

import com.guilherme.splitwise_api.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
