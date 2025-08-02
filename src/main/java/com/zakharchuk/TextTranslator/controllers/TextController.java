package com.zakharchuk.TextTranslator.controllers;

import com.zakharchuk.TextTranslator.services.TextService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
public class TextController {

    private final TextService textService;

    public TextController(TextService textService) {
        this.textService = textService;
    }

    @GetMapping("/")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("originalFile") MultipartFile originalFile,
                               @RequestParam("translatedFile") MultipartFile translatedFile,
                               HttpSession session) throws IOException {

        String[] originalSentences = textService.splitTheOriginalTextIntoSentences(originalFile);
        String[] translatedSentences = textService.splitTheTranslatedTextIntoSentences(translatedFile);

        Map<String, String> sentenceMap = textService
                .createMapWithBothSentences(originalSentences, translatedSentences);

        session.setAttribute("sentenceMap", sentenceMap);
        session.setAttribute("originalSentences", originalSentences);
        session.setAttribute("totalPages", (int) Math.ceil((double) originalSentences.length / 10));

        return "redirect:/view?page=0";
    }

    @GetMapping("/view")
    public String viewPage(@RequestParam(defaultValue = "0") int page,
                           HttpSession session,
                           Model model) {

        Map<String, String> sentenceMap = (Map<String, String>) session.getAttribute("sentenceMap");
        String[] originalSentences = (String[]) session.getAttribute("originalSentences");
        Integer totalPages = (Integer) session.getAttribute("totalPages");

        if (sentenceMap == null || originalSentences == null || totalPages == null) {
            return "redirect:/";
        }

        page = Math.max(0, Math.min(page, totalPages - 1));
        List<String> currentPageSentences = textService.sentencePagination(page, originalSentences);
        model.addAttribute("sentences", currentPageSentences);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sentenceMap", sentenceMap);

        return "text-view";
    }
}