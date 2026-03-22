package com.futura.FuturaFlow.repository;

import com.futura.FuturaFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Цей інтерфейс порожній, але він успадковує купу готових команд!
}