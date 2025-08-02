package com.zakharchuk.TextTranslator.services;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextService {

    public String[] splitTheOriginalTextIntoSentences(MultipartFile originalFile) throws IOException {
        String originalText = new String(originalFile.getBytes(), StandardCharsets.UTF_8);
        return originalText.split("(?<!(^|\\s)(Mr|Mrs|Ms|Dr|Prof))\\.\\s+(?=[A-Z])");
    }
    public String[] splitTheTranslatedTextIntoSentences(MultipartFile translatedFile) throws IOException {
        String translatedText = new String(translatedFile.getBytes(), StandardCharsets.UTF_8);
        return translatedText.split("(?<=[.!?])\\s+");
    }
    public Map<String, String> createMapWithBothSentences(String[] originalSentences, String[] translatedSentences){
        Map<String, String> sentenceMap = new LinkedHashMap<>();
        sentenceMap.put(originalSentences[0], translatedSentences[0]);
        for (int i = 0; i < Math.min(originalSentences.length, translatedSentences.length); i++) {
            sentenceMap.put(originalSentences[i].trim(), translatedSentences[i].trim());
        }
        sentenceMap.put(originalSentences[originalSentences.length-1], translatedSentences[translatedSentences.length-1]);
        return sentenceMap;
    }
    public List<String> sentencePagination(int page, String[] originalSentences){
        int start = page * 10;
        int end = Math.min(start + 10, originalSentences.length);
        return Arrays.asList(Arrays.copyOfRange(originalSentences, start, end));
    }


}
