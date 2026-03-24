package com.techie.springai.rag.repository;

import com.techie.springai.rag.model.TradeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeEventRepository extends JpaRepository<TradeEvent, Long> {
}