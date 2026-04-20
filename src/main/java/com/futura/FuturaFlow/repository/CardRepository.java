package com.futura.FuturaFlow.repository;

import com.futura.FuturaFlow.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}