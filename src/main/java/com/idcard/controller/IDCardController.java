package com.idcard.controller;

import com.idcard.service.IDCardService;
import com.idcard.service.UserService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/idcard")
public class IDCardController {

    private final IDCardService idCardService;
    private final UserService   userService;

    public IDCardController(IDCardService idCardService, UserService userService) {
        this.idCardService = idCardService;
        this.userService   = userService;
    }

    @GetMapping("/download/{cardId}")
    public ResponseEntity<byte[]> downloadCard(@PathVariable Long cardId) {
        try {
            byte[] pdfBytes = idCardService.getPDFBytes(cardId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                ContentDisposition.attachment().filename("id-card-" + cardId + ".pdf").build());
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/regenerate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String regenerate(@PathVariable Long userId, RedirectAttributes flash) {
        try {
            idCardService.generateIDCard(userService.getUserById(userId));
            flash.addFlashAttribute("success", "Card regenerated successfully.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Regeneration failed: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }
}
