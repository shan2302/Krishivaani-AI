# Research: Whisper STT and Open-Source TTS Options

**Project:** KrishiVaani-AI  
**Jira Task:** Research Whisper STT and open-source TTS options  
**Epic:** Research & Planning  
**Week:** 1  
**Story Points:** 3  
**Assignee:** Vedh Nayak K  
**Status:** Research / Evaluation

## 1. Objective

This task evaluates suitable open-source speech technologies for the KrishiVaani-AI multilingual agricultural assistant.

The voice pipeline requires two components:

1. **Speech-to-Text (STT):** Convert a farmer's spoken question into text.
2. **Text-to-Speech (TTS):** Convert the generated agricultural answer into spoken audio.

The project targets **English, Hindi, and Kannada**, so language coverage, speech quality, local deployment, computational requirements, licensing, and agricultural terminology are important selection criteria.

This document records the technologies investigated, their documented capabilities, limitations, and the testing required before final integration.

## 2. KrishiVaani Voice Pipeline

```text
Farmer speaks
      |
      v
Speech-to-Text (Whisper)
      |
      v
English / Hindi / Kannada text
      |
      v
Language / Query Processing
      |
      v
Embedding Model
      |
      v
PGVector Knowledge Retrieval
      |
      v
Qwen3 8B
      |
      v
Generated grounded answer
      |
      v
Text-to-Speech
      |
      v
Spoken response to farmer
```

Whisper is responsible for the input speech layer, while the selected TTS technology is responsible for the output speech layer.

Qwen3 8B remains the generation LLM selected in the separate LLM research task. It should not be confused with either the STT or TTS components.

## 3. Speech-to-Text: OpenAI Whisper

### 3.1 Overview

Whisper is an automatic speech recognition system released by OpenAI. Its official repository describes it as a general-purpose speech recognition model trained using large-scale weak supervision.

Whisper supports multilingual speech recognition, language identification, and speech translation.

For KrishiVaani-AI:

```text
Audio input -> Whisper -> Transcribed text
```

The official implementation is available under the MIT License.

### 3.2 Relevant Capabilities

Whisper provides:

- Multilingual speech recognition
- Automatic language identification
- Speech transcription
- Speech translation to English
- Multiple model sizes
- Local inference
- Python integration
- Command-line usage

### 3.3 Whisper Model Variants

| Model | Approx. Parameters | Relative Resource Requirement | Evaluation Role |
|---|---:|---|---|
| tiny | 39M | Very low | Lightweight baseline |
| base | 74M | Low | Lightweight comparison |
| small | 244M | Moderate | Primary local candidate |
| medium | 769M | High | Accuracy comparison |
| large | ~1.55B | Very high | Accuracy/reference comparison |
| turbo | Optimized large-v3 variant | High | Speed/quality comparison |

Runtime and memory depend on hardware, implementation, audio length, language, and inference settings. These figures should not be treated as guaranteed hardware requirements.

### 3.4 Recommended Evaluation Strategy

Benchmark at least:

- Whisper small
- Whisper medium
- Whisper turbo

Measure:

- transcription quality
- Hindi transcription
- Kannada transcription
- English transcription
- agricultural terminology
- transcription latency
- CPU/GPU usage
- memory consumption

If hardware is limited, small can be used for initial local development while medium/turbo are evaluated when sufficient resources are available.

### 3.5 Language Testing

#### English

> What are the common causes of yellowing leaves in rice plants?

#### Hindi

> धान की फसल में पत्तियां पीली होने के सामान्य कारण क्या हैं?

#### Kannada

> ಭತ್ತದ ಬೆಳೆಯಲ್ಲಿ ಎಲೆಗಳು ಹಳದಿ ಬಣ್ಣಕ್ಕೆ ತಿರುಗಲು ಸಾಮಾನ್ಯ ಕಾರಣಗಳು ಯಾವುವು?

Prepare an expected transcript before testing so the output can be compared against a reference.

Multilingual support does not guarantee identical accuracy across languages. Hindi and Kannada performance must therefore be measured experimentally.

### 3.6 Agricultural Speech Testing

Generic sentences are insufficient. Test terminology such as:

- paddy / rice
- wheat
- nitrogen
- fertilizer
- irrigation
- pesticide
- soil
- rainfall
- crop disease
- yellowing leaves
- pest infestation
- sowing
- harvesting

Use both carefully spoken sentences and natural farmer-style questions.

Example:

> My rice crop leaves are turning yellow. What could be the reason?

## 4. STT Evaluation Metrics

### 4.1 Word Error Rate

Word Error Rate (WER) should be used as a quantitative metric:

```text
WER = (Substitutions + Deletions + Insertions) / Number of Reference Words
```

A lower WER indicates fewer word-level transcription errors for the evaluated test set.

For Indian-language evaluation, document the tokenization/segmentation method used because word boundaries can behave differently across languages.

### 4.2 Latency

Measure:

```text
Audio duration
       vs.
Time required to produce transcription
```

Test, for example:

- 10-second audio
- 20-second audio
- 30-second audio

Keep hardware and inference settings consistent.

### 4.3 Resource Usage

Record:

- CPU usage
- RAM usage
- GPU usage, if applicable
- GPU VRAM usage, if applicable
- model loading time

Actual measurements must be collected during testing rather than estimated.

## 5. Text-to-Speech Options

The main candidates investigated are:

1. AI4Bharat Indic-TTS
2. Piper
3. Coqui XTTS-v2

The candidates should be tested against the actual language requirements rather than selected solely because they are described as multilingual.

## 6. AI4Bharat Indic-TTS

AI4Bharat's Indic-TTS project focuses specifically on Text-to-Speech for Indian languages.

Its official repository states that it trains and evaluates TTS models for 13 Indian languages:

- Assamese
- Bengali
- Bodo
- Gujarati
- Hindi
- Kannada
- Malayalam
- Manipuri
- Marathi
- Odia
- Rajasthani
- Tamil
- Telugu

This is particularly relevant because both **Hindi and Kannada** are explicitly included.

### Relevance

| Language | Documented support |
|---|---|
| English | Not the main focus |
| Hindi | Yes |
| Kannada | Yes |

Evaluate Indic-TTS primarily for Hindi and Kannada. If English TTS is required, evaluate a separate English-capable solution.

### License

The repository is marked MIT licensed. The exact pretrained model/weight terms should still be checked before deployment.

### Evaluation

Test:

- pronunciation
- intelligibility
- naturalness
- agricultural terminology
- Hindi output
- Kannada output
- generation latency
- CPU/GPU requirements

## 7. Piper

Piper is a fast, local neural Text-to-Speech system designed for local speech synthesis.

Its local/offline design can be useful where internet availability is inconsistent.

Piper uses ONNX voice models and supports command-line and programmatic use.

### Language considerations

Piper has a broad collection of voices, including documented Hindi voices. Language availability must be checked at the individual voice/model level.

For every candidate voice, inspect its model card and licensing information.

### Useful characteristics to investigate

- local inference
- relatively lightweight deployment
- ONNX-based models
- offline operation
- simple integration

### Licensing

The engine and individual voice models must be considered separately. Some voice models can have restrictive licenses.

Therefore:

```text
Piper engine terms
        +
specific voice/model terms
        =
license decision for deployment
```

Do not assume every Piper voice has identical licensing terms.

## 8. Coqui XTTS-v2

XTTS-v2 is a multilingual text-to-speech model associated with the Coqui TTS project.

Documented capabilities include:

- multilingual speech generation
- voice cloning
- cross-language voice cloning
- speaker conditioning
- 24 kHz output

### Language Support

The current official XTTS-v2 documentation lists 16 languages:

- English
- Spanish
- French
- German
- Italian
- Portuguese
- Polish
- Turkish
- Russian
- Dutch
- Czech
- Arabic
- Chinese
- Japanese
- Hungarian
- Korean

The current official list does **not** include Kannada. Therefore, XTTS-v2 should not be assumed to satisfy the project's English/Hindi/Kannada requirement.

Some third-party/forked repositories list additional languages. Such sources should be separately validated and should not be treated as equivalent to the official current language list.

### License

XTTS-v2 uses the Coqui Public Model License (CPML). Review the current license terms before deployment.

### Relevance

XTTS-v2 is useful as a comparison candidate because of multilingual generation and voice-cloning capabilities, but the documented Kannada gap is a significant integration consideration.

## 9. TTS Comparison

| Criterion | AI4Bharat Indic-TTS | Piper | Coqui XTTS-v2 |
|---|---|---|---|
| Primary focus | Indian languages | Local neural TTS | Multilingual TTS |
| Hindi | Yes | Voice available | Not in current official list |
| Kannada | Yes | Verify exact voice | Not in current official list |
| English | Not primary focus | Yes | Yes |
| Offline/local use | Yes | Yes | Yes |
| Voice cloning | Not primary focus | Not primary focus | Yes |
| Model/voice licensing | Check exact model | Check exact voice | CPML |
| Main evaluation need | Indian-language quality | Voice availability, quality, license | Kannada coverage, licensing |

This is a research comparison, not a performance ranking. Actual testing should determine practical suitability.

## 10. Selection Criteria

### STT

1. English accuracy
2. Hindi accuracy
3. Kannada accuracy
4. Agricultural terminology recognition
5. WER
6. Latency
7. RAM usage
8. VRAM usage
9. CPU compatibility
10. Offline capability
11. License

### TTS

1. English support
2. Hindi support
3. Kannada support
4. Pronunciation
5. Naturalness
6. Intelligibility
7. Agricultural terminology
8. Generation latency
9. CPU/GPU requirements
10. Offline capability
11. Model/voice license
12. Ease of integration

## 11. Proposed Evaluation Dataset

Create a small controlled voice test set.

### English

1. What are the common causes of yellowing leaves in rice plants?
2. How often should rice crops be irrigated?
3. What can cause pest infestation in a crop?

### Hindi

1. धान की फसल में पत्तियां पीली होने के सामान्य कारण क्या हैं?
2. धान की फसल में सिंचाई कितनी बार करनी चाहिए?
3. फसल में कीटों का प्रकोप क्यों होता है?

### Kannada

1. ಭತ್ತದ ಬೆಳೆಯಲ್ಲಿ ಎಲೆಗಳು ಹಳದಿ ಬಣ್ಣಕ್ಕೆ ತಿರುಗಲು ಸಾಮಾನ್ಯ ಕಾರಣಗಳು ಯಾವುವು?
2. ಭತ್ತದ ಬೆಳೆಗೆ ಎಷ್ಟು ಬಾರಿ ನೀರಾವರಿ ಮಾಡಬೇಕು?
3. ಬೆಳೆಯಲ್ಲಿ ಕೀಟಗಳ ಬಾಧೆ ಉಂಟಾಗಲು ಸಾಮಾನ್ಯ ಕಾರಣಗಳು ಯಾವುವು?

Use the same semantic questions in all three languages where practical.

## 12. Testing Procedure

### STT

1. Record clean speech.
2. Record natural conversational speech.
3. Run audio through the selected Whisper model.
4. Save the transcription.
5. Compare against the reference transcript.
6. Calculate WER where appropriate.
7. Record processing time.
8. Record hardware/resource usage.
9. Record terminology errors.
10. Repeat across English, Hindi and Kannada.

### TTS

1. Prepare the same text.
2. Generate audio using each candidate.
3. Test English, Hindi and Kannada where supported.
4. Check pronunciation.
5. Check intelligibility.
6. Check naturalness.
7. Measure generation time.
8. Check CPU/GPU requirements.
9. Verify model/voice license.
10. Record unsupported-language or pronunciation issues.

## 13. Integration Architecture

```text
                    USER
                      |
                      v
             +----------------+
             | Microphone     |
             +-------+--------+
                     |
                     v
             +----------------+
             | Whisper STT    |
             +-------+--------+
                     |
                     v
             +----------------+
             | Text / Language|
             | Processing     |
             +-------+--------+
                     |
                     v
             +----------------+
             | Embedding Model|
             +-------+--------+
                     |
                     v
             +----------------+
             | PGVector       |
             | Retrieval      |
             +-------+--------+
                     |
                     v
             +----------------+
             | Qwen3 8B       |
             | Generation     |
             +-------+--------+
                     |
                     v
             +----------------+
             | TTS Engine     |
             +-------+--------+
                     |
                     v
                  AUDIO
                     |
                     v
                   USER
```

## 14. Component Responsibilities

| Component | Purpose |
|---|---|
| Whisper | Speech → text |
| Embedding model | Text → vector |
| PGVector | Store/search vectors |
| Qwen3 8B | Generate answer |
| TTS | Text → speech |

The TTS model is not an LLM, and Whisper is not an embedding model.

## 15. Security and Privacy Considerations

Prefer local processing where practical.

Potential advantages:

- reduced dependency on external speech APIs
- reduced transmission of farmer audio
- greater control over stored recordings
- better operation under low-connectivity conditions

If recordings are stored for evaluation:

- obtain appropriate consent where required
- avoid unnecessary personally identifiable information
- restrict access to recordings
- delete recordings when no longer required
- document how evaluation audio is handled

## 16. Limitations

### Whisper

- Accuracy can vary by language and accent.
- Agricultural terminology may be transcribed incorrectly.
- Background noise can affect recognition.
- Larger models require more computational resources.
- Real-world farmer speech can differ from benchmark/test speech.

### AI4Bharat Indic-TTS

- Evaluation is required for naturalness and pronunciation in the project's domain.
- English output may require a separate TTS solution.
- Exact pretrained model/weight licensing should be verified before deployment.

### Piper

- Available languages depend on individual voice models.
- Voice quality can vary between models.
- Individual voice licenses must be checked.

### XTTS-v2

- The current official documentation does not list Kannada.
- Its model license requires review before deployment.
- Multilingual capability alone does not mean it satisfies KrishiVaani's required languages.

## 17. Proposed Decision Process

```text
Research
   ↓
Language compatibility check
   ↓
License check
   ↓
Local installation
   ↓
English testing
   ↓
Hindi testing
   ↓
Kannada testing
   ↓
Agricultural terminology testing
   ↓
WER / quality measurement
   ↓
Latency measurement
   ↓
Resource measurement
   ↓
Team review
   ↓
Final component selection
```

The final selection should be documented after testing.

## 18. Initial Candidates for Practical Testing

### STT

- Whisper small
- Whisper medium
- Whisper turbo

### TTS

- AI4Bharat Indic-TTS for Hindi and Kannada
- Piper for English/Hindi and verification of exact Kannada voice availability
- XTTS-v2 as an additional multilingual candidate, with its documented language limitations considered

These are candidates for evaluation, not final performance rankings.

## 19. Task Completion Checklist

- [x] Research Whisper STT
- [x] Identify Whisper model variants
- [x] Identify multilingual requirements
- [x] Identify WER as an STT metric
- [x] Research AI4Bharat Indic-TTS
- [x] Research Piper
- [x] Research XTTS-v2
- [x] Compare language support
- [x] Identify licensing considerations
- [x] Define agricultural speech tests
- [x] Define TTS evaluation criteria
- [x] Define STT evaluation criteria
- [x] Define integration architecture
- [ ] Run local Whisper tests
- [ ] Run Hindi STT tests
- [ ] Run Kannada STT tests
- [ ] Run TTS tests
- [ ] Record measured latency
- [ ] Record resource usage
- [ ] Finalize speech stack after testing

## 20. Conclusion

Whisper is a suitable candidate for the speech-recognition stage because its official implementation supports multilingual speech recognition and local inference. Multiple Whisper model sizes should be benchmarked using English, Hindi, Kannada and agricultural-domain speech rather than assuming equal performance across languages.

For TTS, AI4Bharat Indic-TTS is particularly relevant to the Indian-language requirement because its documented language set includes both Hindi and Kannada. Piper is a useful local TTS candidate, especially where lightweight/offline operation is important, but individual voice availability and licensing must be verified. XTTS-v2 provides multilingual and voice-cloning capabilities, but its current official language list does not include Kannada, limiting its direct fit for the three-language requirement.

The final STT and TTS components should be selected only after controlled testing of language coverage, agricultural terminology, quality, latency, resource requirements, and licensing.

## 21. References

1. OpenAI Whisper: https://github.com/openai/whisper
2. OpenAI Whisper License: https://github.com/openai/whisper/blob/main/LICENSE
3. AI4Bharat Indic-TTS: https://github.com/AI4Bharat/Indic-TTS
4. Piper TTS: https://github.com/OHF-Voice/piper1-gpl
5. Coqui XTTS-v2 documentation: https://github.com/coqui-ai/TTS/blob/dev/docs/source/models/xtts.md

## 22. Research Record

**Researcher:** Vedh Nayak K  
**Jira Task:** Research Whisper STT and open-source TTS options  
**Project:** KrishiVaani-AI  
**Target Languages:** English, Hindi, Kannada  
**Related LLM:** Qwen3 8B  
**Status:** Research completed; implementation benchmarking pending

Actual experimental results should be added after local testing. No accuracy, latency, memory, or quality values should be recorded as measured results until they have actually been observed and documented.
