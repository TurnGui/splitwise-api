package com.guilherme.splitwise_api.repository;

import com.guilherme.splitwise_api.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
}
