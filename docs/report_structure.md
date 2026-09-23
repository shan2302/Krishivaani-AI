# KrishiVaani-AI — Academic Report Structure

## 1. Title Page

### Project Title
KrishiVaani-AI: Multilingual Voice-Enabled Agricultural Question Answering Using Retrieval-Augmented Generation

### Team Members
- Shantanu
- Vedh
- Dheeraj
- Nitish

### Department / Institution
[To be finalized]

### Academic Year
[To be finalized]

---

## 2. Abstract

A concise summary of:

- Agricultural information-access problem
- Language barrier faced by Indian farmers
- Use of multilingual Retrieval-Augmented Generation (RAG)
- Voice input using Speech-to-Text
- Agricultural knowledge base
- English, Hindi and Kannada support
- Experimental comparison between LLM-only and RAG-based approaches
- Main evaluation metrics
- Expected contribution

---

## 3. Introduction

### 3.1 Background

Introduction to:

- Agricultural information access
- Digital accessibility for farmers
- Language barriers
- Large Language Models (LLMs)
- Hallucination problem
- Retrieval-Augmented Generation (RAG)

### 3.2 Problem Statement

Describe the difficulty of obtaining reliable and understandable agricultural information and the need for grounded multilingual question answering.

### 3.3 Motivation

Explain why a multilingual, voice-enabled agricultural QA system is relevant.

### 3.4 Research Questions

The primary research question and supporting research questions defined in the project research scope document.

### 3.5 Objectives

Describe the project's research and technical objectives.

---

## 4. Literature Review

### 4.1 Retrieval-Augmented Generation

- RAG architecture
- Retrieval process
- Context augmentation
- Generation using retrieved knowledge
- Hallucination reduction

### 4.2 Multilingual RAG

- Multilingual retrieval
- Cross-lingual retrieval
- Multilingual embeddings
- Challenges in low-resource languages

### 4.3 Indian Language NLP

Focus on:

- English
- Hindi
- Kannada

### 4.4 Agricultural AI

Review existing applications of:

- Agricultural question answering
- Crop information systems
- Agricultural knowledge bases
- LLM-based agricultural assistants

### 4.5 Speech-to-Text

Review voice-based agricultural interfaces and relevant speech recognition approaches.

### 4.6 Literature Gap

Identify gaps related to:

- Multilingual agricultural QA
- Grounded agricultural responses
- Indian language retrieval
- Voice-enabled agricultural QA
- Hallucination reduction

---

## 5. Proposed System

### 5.1 System Overview

### 5.2 System Architecture

User
→ Language / Input Processing
→ Speech-to-Text for voice input
→ Query Processing
→ Embedding Generation
→ PGVector Retrieval
→ Agricultural Knowledge Base
→ LLM + Retrieved Context
→ Grounded Response

### 5.3 Input Processing

- Text input
- Voice input
- Whisper / selected STT system

### 5.4 Multilingual Query Processing

- English
- Hindi
- Kannada

### 5.5 Embedding and Retrieval

- Embedding generation
- Vector similarity
- PostgreSQL
- PGVector

### 5.6 Knowledge Base

Agricultural information covering:

- Crop cultivation
- Crop diseases
- Pests
- Fertilizers
- Irrigation
- Soil
- Crop selection
- Basic crop-care practices

### 5.7 Response Generation

Explain how retrieved agricultural context is supplied to the LLM to generate a grounded response.

---

## 6. System Implementation

### 6.1 Technology Stack

### 6.2 Backend

- Spring Boot
- Spring AI

### 6.3 Database

- PostgreSQL
- PGVector

### 6.4 AI / NLP Components

- LLM
- RAG
- Embeddings
- Speech-to-Text

### 6.5 Frontend

[To be documented]

### 6.6 Integration

Describe how the frontend, backend, retrieval layer and LLM interact.

---

## 7. Experimental Methodology

### 7.1 Experiment A — LLM-Only Baseline

Evaluate the LLM without external retrieval.

### 7.2 Experiment B — RAG

Evaluate the LLM with retrieved agricultural knowledge.

### 7.3 Experiment C — Multilingual / Cross-Lingual Evaluation

Evaluate English, Hindi and Kannada.

### 7.4 Dataset / Question Set

Document:

- Question categories
- Languages
- Number of questions
- Data sources
- Test methodology

[To be finalized]

---

## 8. Evaluation Metrics

### 8.1 Precision

### 8.2 Recall

### 8.3 Word Error Rate (WER)

### 8.4 Hallucination Rate

### 8.5 Answer Relevance

### 8.6 Groundedness

Explain how each metric is calculated and applied to the experiments.

---

## 9. Results

### 9.1 LLM-Only Results

### 9.2 RAG Results

### 9.3 Multilingual Results

### 9.4 Speech Recognition Results

### 9.5 Comparative Analysis

Compare the experimental results without making claims that are not supported by the collected data.

---

## 10. Discussion

### 10.1 Findings

### 10.2 Effect of RAG

### 10.3 Multilingual Performance

### 10.4 Voice Input Performance

### 10.5 Retrieval Quality

### 10.6 Limitations

---

## 11. Research Gap and Contribution

Describe how the project contributes to research on:

- Multilingual RAG
- Agricultural QA
- Indian language NLP
- Grounded LLM responses
- Voice-enabled agricultural systems

---

## 12. Limitations and Scope

Document the project boundaries.

The current project scope includes English, Hindi and Kannada.

Out-of-scope areas include:

- Every Indian language
- Replacing agricultural experts
- 100% accuracy
- Machinery
- IoT
- Satellite systems
- Image-based disease diagnosis
- Financial/legal/insurance services
- Marketplace functionality
- Yield prediction

---

## 13. Future Work

Potential future improvements identified from the research and experimental findings.

[To be finalized based on project results]

---

## 14. Conclusion

Summarize:

- Problem
- Proposed approach
- Experimental methodology
- Findings
- Contribution
- Future direction

---

## 15. References

All academic papers, datasets, documentation and other sources cited in the report.

References should use one consistent citation style.

---

## 16. Appendix

Possible contents:

- Sample questions
- Prompt templates
- Retrieval examples
- Experimental tables
- Additional results
- System screenshots
- Configuration details