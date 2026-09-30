package com.example.krishivaanibackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SpeechService {
    public String transcribe(MultipartFile audioFile, String language){
//        TODO: wire whisper/ Bhashini API here
        return "[ASR not integrated yet]";
    }

}
