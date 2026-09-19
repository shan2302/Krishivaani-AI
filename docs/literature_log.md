# KrishiVaani-AI — Literature Log

## Purpose

This document records the academic literature reviewed for the KrishiVaani-AI project.

The literature log supports the project's research on:

- Retrieval-Augmented Generation (RAG)
- Multilingual and cross-lingual retrieval
- Indian language NLP
- Agricultural AI
- Agricultural question answering
- Speech-to-text
- Grounded LLM responses

---

## Literature Review Table

| ID | Paper / Source | Year | Research Area | Method / Approach | Languages | Dataset / Data Source | Key Finding | Limitation | Relevance to KrishiVaani |
|---|---|---:|---|---|---|---|---|---|---|
| L01 | Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks | 2020 | RAG | Retrieval + generation | English | Knowledge-intensive NLP datasets | Introduces RAG architecture combining retrieval with generation | Original evaluation is not specifically focused on Indian agricultural QA | Provides foundational RAG architecture |
| L02 | Retrieval-Augmented Generation in Multilingual Settings | 2024 | Multilingual RAG | Multilingual retrieval and generation | Multiple | Multilingual QA / retrieval settings | Studies RAG behavior in multilingual settings | Multilingual performance depends on retrieval and language characteristics | Directly relevant to multilingual RAG |
| L03 | IndicBART | 2022 | Indian NLP | Multilingual sequence-to-sequence modeling | Indic languages | Indic NLP datasets | Demonstrates multilingual modeling for Indic languages | Model/task limitations depend on dataset and language | Relevant to Indian language processing |
| L04 | IndicBERT / IndicXTREME | 2023 | Indian NLP | Multilingual language representation | Indic languages | IndicXTREME benchmarks | Evaluates multilingual language understanding | Benchmark performance varies across languages | Relevant to multilingual representation |
| L05 | IndicTrans2 | 2023 | Machine Translation | Transformer-based multilingual translation | Indic languages | Translation datasets | Provides multilingual translation capabilities for Indic languages | Translation task rather than agricultural QA | Relevant to multilingual language processing |
| L06 | IndicIRSuite | 2024 | Information Retrieval | Indian-language information retrieval evaluation | Indic languages | IR benchmarks | Provides evaluation resources for Indic information retrieval | Retrieval benchmark rather than agricultural-specific QA | Relevant to evaluating multilingual retrieval |
| L07 | Cross-Lingual Training of Dense Retrievers | 2021 | Dense Retrieval | Cross-lingual dense retrieval | Multiple | Cross-lingual retrieval datasets | Investigates multilingual retrieval representations | Performance depends on languages and training setup | Relevant to multilingual query retrieval |
| L08 | Whisper | 2022 | Speech Recognition | Large-scale speech recognition | Multilingual | Large-scale speech dataset | Provides multilingual speech-to-text capability | Recognition quality can vary by language and conditions | Relevant to voice input |
| L09 | Krishi Sathi / Intent-Aware Context Retrieval for Multi-Turn Agricultural QA | 2025 | Agricultural AI | Context retrieval + agricultural QA | Agricultural / multilingual context | Agricultural QA | Investigates context-aware agricultural question answering | Specific system and evaluation scope | Directly relevant to agricultural QA |
| L10 | Agricultural LLM Question Answering | 2026 | Agricultural AI | LLM-based agricultural QA | [To verify] | Agricultural QA data | Studies LLM-based agricultural question answering | Scope depends on evaluation setting | Relevant to agricultural AI |
| L11 | ChatCEA | 2026 | Agricultural RAG | Agricultural RAG | [To verify] | Agricultural knowledge | Investigates RAG for agricultural question answering | Requires comparison with KrishiVaani's target languages and evaluation | Relevant to agricultural RAG |

---

## Detailed Literature Notes

### L01 — Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks

**Authors:** Lewis et al.

**Year:** 2020

**Research Area:** Retrieval-Augmented Generation

**Purpose:**

Establishes the RAG approach of combining information retrieval with language generation.

**Relevance:**

Provides the foundational concept for the KrishiVaani RAG architecture.

**Use in Report:**

Sections:
- Literature Review
- Proposed System
- Experimental Methodology

---

### L02 — Retrieval-Augmented Generation in Multilingual Settings

**Authors:** Chirkova et al.

**Year:** 2024

**Research Area:** Multilingual RAG

**Purpose:**

Investigates retrieval-augmented generation in multilingual settings.

**Relevance:**

Directly related to the project's focus on multilingual agricultural question answering.

**Use in Report:**

Sections:
- Multilingual RAG
- Literature Gap
- Experimental Methodology

---

### L03 — IndicBART

**Authors:** Dabre et al.

**Year:** 2022

**Research Area:** Indian-language NLP

**Relevance:**

Provides background for multilingual processing involving Indic languages.

**Use in Report:**

Section:
- Indian Language NLP

---

### L04 — IndicBERT / IndicXTREME

**Authors:** Doddapaneni et al.

**Year:** 2023

**Research Area:** Multilingual representation learning

**Relevance:**

Provides background on language understanding across Indic languages.

---

### L05 — IndicTrans2

**Authors:** Gala et al.

**Year:** 2023

**Research Area:** Indic language translation

**Relevance:**

Provides background on multilingual processing across Indian languages.

---

### L06 — IndicIRSuite

**Authors:** Haq et al.

**Year:** 2024

**Research Area:** Information retrieval

**Relevance:**

Especially relevant to evaluating retrieval across Indian languages.

---

### L07 — Cross-Lingual Training of Dense Retrievers

**Authors:** Shi et al.

**Year:** 2021

**Research Area:** Cross-lingual information retrieval

**Relevance:**

Supports the investigation of multilingual and cross-lingual retrieval approaches.

---

### L08 — Whisper

**Authors:** Radford et al.

**Year:** 2022

**Research Area:** Speech recognition

**Relevance:**

Relevant to the project's voice-input pipeline.

---

### L09 — Krishi Sathi / Intent-Aware Context Retrieval for Multi-Turn Agricultural QA

**Year:** 2025

**Research Area:** Agricultural question answering

**Relevance:**

Directly related to agricultural QA and retrieval-based context handling.

---

## Research Themes Identified

### Theme 1 — Retrieval-Augmented Generation

Literature establishes retrieval as a mechanism for supplying external knowledge to generation models.

### Theme 2 — Multilingual Retrieval

Multilingual systems introduce challenges involving:

- Cross-language retrieval
- Language representation
- Low-resource languages
- Retrieval quality

### Theme 3 — Indian Language NLP

The project specifically targets:

- English
- Hindi
- Kannada

### Theme 4 — Agricultural AI

Agricultural QA requires reliable domain-specific information and appropriate knowledge sources.

### Theme 5 — Voice-Based Interaction

Speech-to-text provides a mechanism for users to submit agricultural questions through voice.

---

## Literature Gap

The literature reviewed should be used to investigate the combination of:

1. Multilingual RAG
2. Agricultural question answering
3. Indian languages
4. Voice input
5. Grounded responses
6. Hallucination evaluation

The project's research scope specifically proposes experimental comparison between an LLM-only baseline and RAG-based responses and evaluation across English, Hindi and Kannada.

---

## Literature Review Status

| Area | Status |
|---|---|
| RAG fundamentals | Reviewed |
| Multilingual RAG | Reviewed |
| Indian language NLP | Reviewed |
| Multilingual retrieval | Reviewed |
| Agricultural AI | Reviewed |
| Agricultural QA | Reviewed |
| Speech-to-text | Reviewed |
| Experimental evaluation literature | To be expanded |
| Final reference verification | To be completed |

---

## References

1. Lewis et al. (2020). *Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks.*
2. Chirkova et al. (2024). *Retrieval-augmented generation in multilingual settings.*
3. Dabre et al. (2022). *IndicBART.*
4. Doddapaneni et al. (2023). *IndicBERT / IndicXTREME.*
5. Gala et al. (2023). *IndicTrans2.*
6. Haq et al. (2024). *IndicIRSuite.*
7. Shi et al. (2021). *Cross-Lingual Training of Dense Retrievers.*
8. Radford et al. (2022). *Whisper.*
9. Vijayvargia et al. (2025). *Krishi Sathi / Intent-Aware Context Retrieval for Multi-Turn Agricultural QA.*
10. Eldem & Eldem (2026). Agricultural LLM question answering.
11. Xia et al. (2026). *ChatCEA.*