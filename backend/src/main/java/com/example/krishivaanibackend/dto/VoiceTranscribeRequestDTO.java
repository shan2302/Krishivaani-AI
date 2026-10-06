package com.example.krishivaanibackend.dto;

//Response DTO for POST /api/v1/voice/transcribe
public class VoiceTranscribeRequestDTO {

    private String transcription;
    private String language;
    private String status;

    public VoiceTranscribeRequestDTO(String transcription, String language) {
        this.transcription = transcription;
        this.language = language;
        this.status = "Success";
    }
//    Getters
    public String getTranscription() {
        return transcription;
    }

    public String getLanguage() {
        return language;
    }

    public String getStatus() {
        return status;
    }
}
