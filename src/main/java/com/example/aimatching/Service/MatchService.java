package com.example.aimatching.Service;

import com.example.aimatching.Entity.Influencer;
import com.example.aimatching.Repository.InfluencerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final InfluencerRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}") // YAML에서 https://api.openai.com 수정 필요
    private String apiUrl;

    @Value("${openai.prompt.matching}")
    private String matchingPrompt;

    public List<Influencer> findBestMatch(String userQuery) {
        String gptResponse = callGptApi(userQuery);
        System.out.println("GPT Raw Response: " + gptResponse);

        String realName;
        String category;
        String style;

        try {
            Map<String, String> result = objectMapper.readValue(gptResponse, new TypeReference<Map<String, String>>() {});

            realName = result.getOrDefault("name", "AI_Partner");
            category = result.getOrDefault("category", "BEAUTY").toUpperCase();
            style    = result.getOrDefault("style", "MODERN").toUpperCase();

        } catch (Exception e) {
            System.err.println("JSON 파싱 실패, 기본값 사용: " + e.getMessage());
            realName = "AI_Partner";
            category = "BEAUTY";
            style = "MODERN";
        }

        List<Influencer> nameList = repository.findByName(realName);
        if (!nameList.isEmpty()) return nameList;

        List<Influencer> categoryList = repository.findByCategoryAndStyle(category, style);

        if (categoryList.isEmpty()) {
            Influencer newInf = new Influencer(realName, category, style);
            repository.save(newInf);
            return List.of(newInf);
        }

        return categoryList;
    }

    private String callGptApi(String userQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            String combinedInput = String.format(matchingPrompt, userQuery);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");
            body.put("input", combinedInput);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // API 호출
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);
            System.out.println("Full API Response: " + response);

            // [수정] 실제 로그 구조에 맞춘 데이터 추출 (output -> content -> text)
            if (response == null || !response.containsKey("output")) {
                return getDefaultJson();
            }

            List<Map<String, Object>> output = (List<Map<String, Object>>) response.get("output");
            if (output == null || output.isEmpty()) return getDefaultJson();

            List<Map<String, Object>> content = (List<Map<String, Object>>) output.get(0).get("content");
            if (content == null || content.isEmpty()) return getDefaultJson();

            // 실제 GPT가 생성한 텍스트 추출
            String rawText = (String) content.get(0).get("text");

            // [추가] 마크다운 태그(```json ... ```) 제거 로직
            // 이 처리를 안 하면 ObjectMapper가 순수 JSON으로 인식하지 못해 에러가 납니다.
            if (rawText != null) {
                rawText = rawText.replaceAll("(?s)```json\\s*|```", "").trim();
            }

            return (rawText != null && !rawText.isEmpty()) ? rawText : getDefaultJson();

        } catch (Exception e) {
            System.err.println("API 호출 또는 데이터 추출 에러: " + e.getMessage());
            return getDefaultJson();
        }
    }


    private String getDefaultJson() {
        // 파싱 실패나 에러 시를 대비한 최소한의 규격
        return "{\"name\":\"AI_Partner\", \"category\":\"BEAUTY\", \"style\":\"MODERN\"}";
    }

}
