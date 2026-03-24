package com.techie.springai.rag.service;

import com.techie.springai.rag.model.TradeEvent;
import com.techie.springai.rag.repository.TradeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeEventRepository repo;
    private final ApplicationEventPublisher publisher;

    public void createEvent(String tradeId, String status, String type, String details) {

        TradeEvent event = TradeEvent.builder()
                .tradeId(tradeId)
                .status(status)
                .type(type)
                .details(details)
                .build();

        repo.save(event);
        publisher.publishEvent(event);
    }
}