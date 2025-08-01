package com.zakharchuk.TextTranslator.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Controller
public class TextController {

    @GetMapping("/")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("originalFile") MultipartFile originalFile,
                               @RequestParam("translatedFile") MultipartFile translatedFile,
                               HttpSession session) throws IOException {

        // Читання файлів
        String originalText = new String(originalFile.getBytes(), StandardCharsets.UTF_8);
        String translatedText = new String(translatedFile.getBytes(), StandardCharsets.UTF_8);

        // Розбиття на речення
        String[] originalSentences = originalText.split("(?<!(^|\\s)(Mr|Mrs|Ms|Dr|Prof))\\.\\s+(?=[A-Z])");
        String[] translatedSentences = translatedText.split("(?<=[.!?])\\s+");


        // Створення мапи
        Map<String, String> sentenceMap = new LinkedHashMap<>();
        sentenceMap.put(originalSentences[0], translatedSentences[0]);
        for (int i = 0; i < Math.min(originalSentences.length, translatedSentences.length); i++) {
            sentenceMap.put(originalSentences[i].trim(), translatedSentences[i].trim());
        }
        sentenceMap.put(originalSentences[originalSentences.length-1], translatedSentences[translatedSentences.length-1]);

        // Зберігаємо дані в сесії
        session.setAttribute("sentenceMap", sentenceMap);
        session.setAttribute("originalSentences", originalSentences);
        session.setAttribute("totalPages", (int) Math.ceil((double) originalSentences.length / 10));

        return "redirect:/view?page=0";
    }

    @GetMapping("/view")
    public String viewPage(@RequestParam(defaultValue = "0") int page,
                           HttpSession session,
                           Model model) {

        // Отримуємо дані з сесії
        Map<String, String> sentenceMap = (Map<String, String>) session.getAttribute("sentenceMap");
        String[] originalSentences = (String[]) session.getAttribute("originalSentences");
        Integer totalPages = (Integer) session.getAttribute("totalPages");

        if (sentenceMap == null || originalSentences == null || totalPages == null) {
            return "redirect:/";
        }

        // Пагінація
        page = Math.max(0, Math.min(page, totalPages - 1));
        int start = page * 10;
        int end = Math.min(start + 10, originalSentences.length);
        List<String> currentPageSentences = Arrays.asList(Arrays.copyOfRange(originalSentences, start, end));

        model.addAttribute("sentences", currentPageSentences);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sentenceMap", sentenceMap);

        return "text-view";
    }
}