package com.techie.springai.rag.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techie.springai.rag.service.TradeRagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {

    private final TradeRagService ragService;
    private final ChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();


    @GetMapping("/replayQueue")
    public List<Map<String,Object>> replay(@RequestParam String tradeId) throws Exception {
        List<Document> docs = ragService.search(tradeId,
                List.of("NEWT","MODI","EROR","TERM"),
                "TO_SEND");

        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String prompt = """
            You are a system that outputs STRICT JSON ONLY.
            Replay the trade lifecycle for trade %s with action types NEWT, MODI, EROR, TERM and status TO_SEND.
            Only include the latest timestamp for each action type.
            Output MUST be an array of objects with fields:
            - actionType
            - tradeId
            - status
            - timestamp
            - details
            Do not include explanations.
            Context:
            %s
            """.formatted(tradeId, context);

        String response = chatClient.prompt().user(prompt).call().content();
        response = extractJson(response);

        try{
            return mapper.readValue(response, new TypeReference<>(){});
        }catch(Exception e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

//    @GetMapping("/queryTrade")
//    public String queryTrade(
//            @RequestParam String tradeId,
//            @RequestParam String question
//    ) throws Exception {
//
//        // 1️⃣ Get relevant events from VectorStore
//        List<Document> docs = ragService.search(tradeId, question);
//
//        String context = docs.stream()
//                .map(Document::getFormattedContent)  // latest Spring AI API
//                .sorted()
//                .collect(Collectors.joining("\n"));
//
//        // 2️⃣ Create prompt for LLM
//        String prompt = """
//        You are a trade lifecycle analyst AI.
//
//        Answer the user question strictly based on the context below.
//        Do not make up information.
//
//        TradeId: %s
//        Question: %s
//        Context:
//        %s
//        """.formatted(tradeId, question, context);
//
//
//        // 3️⃣ Send prompt to Ollama ChatClient
//        String response = chatClient.prompt()
//                .user(prompt)
//                .call()
//                .content();
//
//        return response;
//    }

    @GetMapping("/queryTrade")
    public String query(@RequestParam String tradeId, @RequestParam String question){
        List<Document> docs = ragService.search(tradeId,
                question);

        String context = docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String prompt = """
            You are a trade lifecycle analyst AI.
            Answer the user question strictly based on context below. Do NOT hallucinate.
            TradeId: %s
            Question: %s
            Context:
            %s
            """.formatted(tradeId, question, context);

        return chatClient.prompt().user(prompt).call().content();
    }

    public String extractJson(String raw) {
        int start = raw.indexOf("[");      // start of JSON array
        int end = raw.lastIndexOf("]");    // end of JSON array

        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }

        // fallback: try JSON object
        start = raw.indexOf("{");
        end = raw.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }

        // if nothing found, return empty JSON array
        return "[]";
    }
}