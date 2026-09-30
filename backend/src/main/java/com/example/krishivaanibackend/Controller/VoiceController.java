package com.example.krishivaanibackend.Controller;


import com.example.krishivaanibackend.service.SpeechService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/voice")
@CrossOrigin(origins = "*")
public class VoiceController {
    private final SpeechService speechService;
    public VoiceController(SpeechService speechService){
        this.speechService = speechService;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<Map<String, String>> transcribe(
            @RequestParam("audio")MultipartFile audioFile,
            @RequestParam(value="language", defaultValue = "hi") String language
            ){
        if(audioFile.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error","Empty file"));
        }
        String transcription = speechService.transcribe(audioFile, language);
        return ResponseEntity.ok(Map.of("transcription", transcription, "language", language));
    }

}
