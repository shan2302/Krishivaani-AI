package com.example.krishivaanibackend.Controller;


import com.example.krishivaanibackend.dto.VoiceTranscribeRequestDTO;
import com.example.krishivaanibackend.service.SpeechService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/voice")
@CrossOrigin(origins = "*")
public class VoiceController {
    private final SpeechService speechService;
    public VoiceController(SpeechService speechService){
        this.speechService = speechService;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<VoiceTranscribeRequestDTO> transcribe(
            @RequestParam("audio")MultipartFile audioFile,
            @RequestParam(value="language", defaultValue = "hi") String language
            ){
        if(audioFile.isEmpty()){
            throw new IllegalArgumentException("Empty file");
        }
        String transcription = speechService.transcribe(audioFile, language);
        return ResponseEntity.ok(new VoiceTranscribeRequestDTO(transcription, language));
    }

}
