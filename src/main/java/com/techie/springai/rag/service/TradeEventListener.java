package com.techie.springai.rag.service;

import com.techie.springai.rag.model.TradeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeEventListener {

    private final TradeRagService ragService;

    @EventListener
    public void handle(TradeEvent event) {
        ragService.ingest(event);
    }
}