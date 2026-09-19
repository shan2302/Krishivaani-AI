# KV-002 — Literature Review on Multilingual RAG and Agricultural AI

**Jira:** KV-002  
**Epic:** Research & Planning  
**Assignee:** Vedh  
**Label:** researchreport  
**Scope:** Multilingual agricultural question answering using English, Hindi, and Kannada

---

## 1. Introduction

Agricultural information systems increasingly use Natural Language Processing (NLP), Large Language Models (LLMs), and conversational interfaces to make agricultural knowledge easier to access. However, agricultural question answering has an important reliability requirement: an answer that sounds fluent but contains unsupported or incorrect information can lead to poor farming decisions.

KrishiVaani AI is designed around this problem. The project's existing research scope defines the main research question as whether a multilingual Retrieval-Augmented Generation (RAG) system can provide more accurate, relevant, and grounded agricultural answers with fewer hallucinations than an LLM-only approach for English, Hindi, and Kannada users.

RAG is relevant because it combines an LLM's generative capabilities with information retrieved from an external knowledge source. The original RAG work by Lewis et al. introduced a framework combining parametric model memory with a non-parametric dense vector index. Their experiments showed that retrieval-augmented models could produce more specific, diverse, and factual language than a parametric-only baseline on knowledge-intensive tasks.

For KrishiVaani AI, the literature therefore needs to be examined from four connected perspectives:

1. Retrieval-Augmented Generation and factual grounding.
2. Multilingual and cross-lingual retrieval.
3. NLP resources for Indian languages, especially Hindi and Kannada.
4. Agricultural question answering and domain-specific knowledge retrieval.

This review uses these areas to identify a practical research direction for the project's proposed RAG pipeline.

---

## 2. Research Objectives

The literature review has the following objectives:

- Understand the principles and benefits of Retrieval-Augmented Generation.
- Examine how RAG can reduce dependence on information stored only in an LLM.
- Study multilingual and cross-lingual RAG approaches.
- Investigate multilingual retrieval and embedding resources relevant to Hindi and Kannada.
- Review existing agricultural AI and agricultural question-answering systems.
- Identify challenges involving low-resource languages, retrieval quality, hallucination, and domain-specific knowledge.
- Examine speech-to-text considerations for a voice-enabled agricultural assistant.
- Identify suitable evaluation approaches for retrieval, speech recognition, and answer quality.
- Derive research gaps and technical implications for KrishiVaani AI.

---

## 3. Literature Search Methodology

The review prioritizes peer-reviewed conference/journal publications, research papers, and official research/model documentation. The search focused on the following themes:

- "retrieval augmented generation knowledge intensive question answering"
- "multilingual retrieval augmented generation"
- "cross-lingual dense retrieval"
- "Indian language information retrieval"
- "Hindi Kannada multilingual embeddings"
- "agricultural question answering LLM"
- "agricultural RAG"
- "speech recognition multilingual Whisper"

The literature was selected for direct relevance to at least one of the following project components:

- RAG architecture
- multilingual retrieval
- Hindi/Kannada NLP
- agricultural question answering
- voice input
- evaluation

The review is not intended to claim that any single model is automatically the best choice for KrishiVaani AI. Model selection should be validated experimentally using the project's own evaluation dataset.

---

## 4. Retrieval-Augmented Generation

### 4.1 Basic concept

Traditional LLMs primarily rely on knowledge encoded in their parameters. This creates limitations when an application requires domain-specific, updateable, or traceable information.

Lewis et al. (2020) proposed Retrieval-Augmented Generation as a method that combines:

- a parametric generator, such as a sequence-to-sequence language model; and
- a non-parametric memory represented by a dense vector index.

The retriever selects relevant passages, and the generator conditions its answer on the retrieved information.

A simplified RAG pipeline is:

```text
User Question
      |
      v
Question Embedding
      |
      v
Vector Similarity Search
      |
      v
Relevant Documents
      |
      v
LLM + Retrieved Context
      |
      v
Grounded Answer
```

The original RAG experiments reported improvements over parametric-only baselines on knowledge-intensive NLP tasks and found the generated language to be more specific, diverse, and factual.

### 4.2 Relevance to KrishiVaani AI

Agricultural knowledge can change and may need to come from trusted sources. Instead of expecting the LLM to memorize every crop practice, pest-management recommendation, fertilizer guideline, and irrigation recommendation, KrishiVaani AI can retrieve information from a curated agricultural knowledge base.

This directly supports the project's goal of evaluating whether retrieval improves answer accuracy, relevance, and groundedness while reducing hallucinations.

However, RAG does not guarantee correctness. If the knowledge base contains poor information or the retriever selects an irrelevant passage, the LLM can still produce a poor answer. Therefore, both retrieval quality and generation quality must be evaluated.

---

## 5. Multilingual RAG

### 5.1 Why multilingual RAG is different

A multilingual RAG system has additional challenges because the query, retrieved documents, and generated answer may involve different languages.

A useful distinction is:

**Monolingual retrieval**

```text
Hindi Query -> Hindi Documents -> Hindi Answer
```

**Cross-lingual retrieval**

```text
Kannada Query -> English/Hindi/Multilingual Documents -> Kannada Answer
```

Cross-lingual retrieval can be valuable when the highest-quality agricultural resources are not available in every target language.

### 5.2 Multilingual RAG research

Chirkova et al. (2024) studied RAG in multilingual settings using queries and datastores across 13 languages. Their findings indicate that multilingual RAG requires attention beyond simply selecting a multilingual retriever and generator. In particular, task-specific prompt engineering may be required to reliably generate answers in the user's language.

The study also identifies several multilingual problems relevant to KrishiVaani AI:

- irrelevant retrieval;
- incorrect reading or use of retrieved documents;
- fluency problems;
- code-switching;
- evaluation difficulties caused by multilingual spelling and named-entity variation.

This is important because a system may retrieve a relevant English document for a Kannada query but still fail to produce a correct Kannada response.

### 5.3 Implication

KrishiVaani AI should therefore evaluate the complete pipeline rather than only testing whether an embedding model produces similar vectors.

The evaluation should separately investigate:

1. Query representation.
2. Retrieval relevance.
3. Context quality.
4. Language correctness.
5. Grounded answer quality.

---

## 6. Multilingual Embeddings and Indian Languages

### 6.1 Role of embeddings

Embeddings convert text into numerical vectors so that semantically related queries and documents can be compared using vector similarity.

For example:

```text
"How often should I irrigate tomato crops?"
            |
            v
       Embedding Vector
            |
            v
Vector Search in Agricultural Knowledge Base
```

For multilingual RAG, the important property is that semantically equivalent sentences in different languages should be represented sufficiently close together for cross-lingual retrieval.

### 6.2 Indian-language resources

Indian language NLP has developed substantially in recent years, but resource availability is not uniform across languages and tasks.

IndicBART demonstrated the usefulness of models designed specifically for Indic languages. It focused on 11 Indic languages and English and used similarities between Indic scripts to support multilingual transfer.

IndicBERT and IndicBERT v2 are also important resources for Indian-language NLP. The IndicBERT v2 work introduced IndicXTREME, a benchmark covering many Indian-language tasks, and reported improvements over a strong baseline when averaged across languages and tasks.

For retrieval specifically, IndicIRSuite (2024) introduced neural information-retrieval resources for 11 Indian languages, including Hindi and Kannada. It includes INDIC-MARCO and Indic-ColBERT models. The reported experiments show that language-specific retrieval models can provide substantial improvements over several baseline retrieval systems.

This is particularly relevant to KrishiVaani AI because retrieval quality is one of the central components of the project's proposed evaluation.

### 6.3 IndicTrans2

IndicTrans2 is another important resource for the Indian-language ecosystem. It supports all 22 scheduled Indian languages and introduced the Bharat Parallel Corpus Collection (BPCC), together with multilingual translation models and evaluation resources.

IndicTrans2 is primarily a machine-translation system rather than a RAG system. Therefore, it should not automatically be selected as the embedding or retrieval model for KrishiVaani AI. Its relevance is instead that it demonstrates the availability of large-scale multilingual resources for Indian languages and could potentially be useful in future translation-oriented components.

### 6.4 Relevance of Kannada

Kannada is particularly important to the project because it is explicitly within the initial supported language set.

The literature indicates that Kannada is included in modern Indian-language NLP and information-retrieval resources, including IndicIRSuite and multilingual Indian-language models. Nevertheless, performance should not be assumed to be identical to English or Hindi.

KrishiVaani AI should therefore report results separately for:

- English
- Hindi
- Kannada

rather than reporting only one aggregate multilingual score.

---

## 7. Agricultural AI and Question Answering

### 7.1 Domain-specific agricultural knowledge

Agriculture differs from general-purpose question answering because many questions are highly domain-specific.

Examples include:

- crop cultivation;
- pest management;
- crop diseases;
- fertilizer use;
- irrigation;
- soil management;
- crop selection;
- crop-care practices.

The project's existing scope identifies these areas as candidate knowledge-base topics.

A general LLM may know broad agricultural concepts, but a production-oriented agricultural assistant requires controlled and reliable information sources.

### 7.2 Recent agricultural QA research

A 2026 Scientific Reports study evaluated LLM-based agricultural question answering across general agriculture, horticulture, and crop production at multiple difficulty levels. This supports the broader research direction of evaluating LLMs specifically on agricultural knowledge rather than assuming general-domain benchmark performance transfers directly to agriculture.

A 2025 study on "Krishi Sathi" presented an agricultural chatbot for Indian farmers using curated agricultural datasets, instruction tuning, retrieval-augmented generation, and text/speech interaction. The system focused on English and Hindi and used an intent-aware multi-turn interaction strategy before retrieval and generation.

This is particularly relevant to KrishiVaani AI because it demonstrates that agricultural conversational systems for Indian users can combine:

- agricultural knowledge;
- RAG;
- speech;
- multilingual interaction;
- conversational context.

However, KrishiVaani AI has a different experimental emphasis: it explicitly proposes comparing an LLM-only baseline with RAG and evaluating English, Hindi, and Kannada.

### 7.3 Agricultural RAG beyond India

Recent agricultural RAG research also demonstrates the value of domain-specific knowledge bases. For example, a 2026 Smart Agricultural Technology study proposed a multimodal agricultural Q&A system combining a domain-specific knowledge base with RAG for fruit-tree cultivation.

These systems show that RAG can be adapted to agricultural domains, but their datasets, languages, tasks, and evaluation settings are not identical to KrishiVaani AI. Their results should therefore be treated as supporting evidence for the research direction rather than direct evidence of expected KrishiVaani performance.

---

## 8. Voice Input and Speech-to-Text

KrishiVaani AI includes voice input, with Whisper or another free speech-to-text system proposed for converting spoken questions into text.

Whisper is a multilingual automatic speech-recognition system trained on a large and diverse dataset. OpenAI reports that its training approach improves robustness to accents, background noise, and technical language, while supporting multilingual transcription.

For KrishiVaani AI, the important point is that speech recognition becomes an additional stage before retrieval:

```text
Spoken Question
      |
      v
Speech-to-Text
      |
      v
Recognized Text
      |
      v
Multilingual Retrieval
      |
      v
RAG
      |
      v
Answer
```

Speech recognition errors can propagate into retrieval. For example, if an agricultural term is incorrectly transcribed, the retrieval system may fail to find the correct documents.

Therefore, Word Error Rate (WER), already specified in the project's evaluation plan, is important. However, WER alone does not measure the downstream impact of transcription errors on RAG. A future experiment could examine whether particular transcription errors change retrieval results or answer correctness.

---

## 9. Comparison of Relevant Literature

| Study / Resource | Year | Main Area | Languages / Scope | Key Contribution | Relevance to KrishiVaani |
|---|---:|---|---|---|---|
| Lewis et al. — RAG | 2020 | RAG | General NLP | Combined parametric generation with dense non-parametric retrieval | Foundation for the project's RAG architecture |
| IndicBART | 2022 | Indic NLP | 11 Indic languages + English | Multilingual generation model for Indic languages | Demonstrates value of Indic-focused multilingual modeling |
| IndicTrans2 | 2023 | Indian-language MT | 22 scheduled Indic languages | Large multilingual model and BPCC resources | Demonstrates available Indian-language resources |
| IndicBERT v2 / IndicXTREME | 2023 | Indic NLP | Broad Indic coverage | Multilingual benchmark and improved Indic model | Useful background for evaluating Indian-language NLP |
| IndicIRSuite | 2024 | Information Retrieval | 11 Indian languages | Indic-MARCO and Indic-ColBERT retrieval resources | Directly relevant to multilingual retrieval |
| Chirkova et al. — mRAG | 2024 | Multilingual RAG | 13 languages | Studied multilingual RAG pipeline requirements | Directly relevant to cross-lingual RAG design |
| Krishi Sathi | 2025 | Agricultural QA | English + Hindi | Agricultural chatbot using RAG and speech | Closely related Indian agricultural application |
| Agricultural LLM QA study | 2026 | Agricultural QA | Agriculture domain | Evaluated LLM performance across agricultural topics and difficulty levels | Supports domain-specific evaluation |
| Agricultural multimodal RAG | 2026 | Agricultural RAG | Fruit-tree agriculture | Domain knowledge base + RAG + multimodal QA | Evidence for domain-specific agricultural RAG |

---

## 10. Key Findings from the Literature

### Finding 1 — Retrieval can complement LLM parametric knowledge

The RAG literature supports using an external knowledge source for knowledge-intensive tasks. This is consistent with KrishiVaani's decision to maintain a curated agricultural knowledge base.

### Finding 2 — Multilingual RAG introduces additional failure modes

Multilingual RAG is not simply standard RAG with a multilingual model. Retrieval mismatch, language generation problems, code-switching, and evaluation differences can affect the final answer.

### Finding 3 — Indian-language retrieval deserves explicit evaluation

Resources such as IndicIRSuite show that neural information retrieval for Indian languages is an active research area. Hindi and Kannada should therefore be evaluated separately instead of assuming English retrieval performance transfers directly.

### Finding 4 — Agricultural QA should use domain-specific evaluation

Agricultural questions involve specialized terminology and potentially consequential recommendations. General LLM benchmarks are not sufficient to establish agricultural reliability.

### Finding 5 — Speech errors can affect the whole pipeline

Speech recognition is upstream of retrieval. An incorrect transcription can cause incorrect retrieval even if the RAG system itself is functioning correctly.

### Finding 6 — RAG does not eliminate hallucination automatically

RAG improves access to external information, but it does not guarantee that the retrieved information is relevant or that the generated answer faithfully follows it. The project's hallucination-rate and groundedness evaluations are therefore necessary.

---

## 11. Research Gaps

Based on the reviewed literature, the following gaps are relevant to KrishiVaani AI.

### 11.1 Combined multilingual + agricultural + RAG evaluation

There is research on RAG, multilingual RAG, Indian-language retrieval, and agricultural AI separately. However, fewer studies directly evaluate the combined problem of:

```text
Agricultural Domain
        +
English / Hindi / Kannada
        +
RAG
        +
Voice Input
        +
LLM-only Baseline
        +
Hallucination Evaluation
```

KrishiVaani can contribute by experimentally evaluating this combination.

### 11.2 Kannada-specific agricultural retrieval

Kannada is supported by several Indian-language NLP and retrieval resources, but the project's agricultural domain introduces a more specific requirement: retrieval must work for agricultural terminology and farmer-style queries.

The project should therefore test Kannada independently rather than assuming that a general Kannada embedding benchmark predicts agricultural retrieval performance.

### 11.3 Cross-language knowledge retrieval

An important design question is whether agricultural documents should be stored only in the same language as the user's query or whether a multilingual knowledge base can retrieve documents across languages.

For example:

```text
Kannada Question
      |
      v
Multilingual Query Embedding
      |
      v
English + Hindi + Kannada Documents
      |
      v
Relevant Context
      |
      v
Kannada Answer
```

The second design could increase knowledge coverage, but it introduces additional translation and grounding challenges. This should be treated as an experimental design question rather than an assumption.

### 11.4 Retrieval quality versus generation quality

A low-quality answer may result from either:

1. poor retrieval, or
2. failure of the LLM to use good retrieved context.

Therefore, the project should not evaluate only the final generated answer. Retrieval metrics should also be recorded.

### 11.5 Evaluation in three languages

Aggregated results can hide language-specific weaknesses. The project should report metrics separately for English, Hindi, and Kannada and then provide an overall summary.

---

## 12. Proposed Technical Direction for KrishiVaani AI

The literature supports the following conceptual architecture:

```text
                  +----------------------+
                  |      User Input      |
                  |   Text / Voice      |
                  +----------+-----------+
                             |
                   Voice input only
                             |
                             v
                  +----------------------+
                  |   Speech-to-Text     |
                  | Whisper / Free STT   |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  | Query Processing     |
                  | Language Handling    |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  | Multilingual         |
                  | Embedding Model      |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  | PostgreSQL +         |
                  | PGVector Retrieval   |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  | Relevant Agricultural|
                  | Knowledge            |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  | LLM + Retrieved      |
                  | Context              |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  | Grounded Response    |
                  | English/Hindi/Kannada|
                  +----------------------+
```

The final model and embedding selection should be determined experimentally rather than fixed solely from this literature review.

---

## 13. Recommended Experimental Design

The project's existing scope already defines three major experiments.

### Experiment A — LLM-only baseline

```text
Question -> LLM -> Answer
```

This establishes baseline answer quality without external retrieval.

### Experiment B — RAG-grounded evaluation

```text
Question
   ↓
Retrieve Documents
   ↓
LLM + Retrieved Context
   ↓
Answer
```

This tests whether retrieval improves the quality and grounding of answers.

### Experiment C — Cross-lingual / multilingual evaluation

Run equivalent or carefully designed agricultural questions in:

- English
- Hindi
- Kannada

Measure retrieval and answer performance separately.

### Additional recommended analysis

For each language, record:

- Retrieval Precision
- Retrieval Recall
- Answer Relevance
- Groundedness
- Hallucination Rate

For voice queries additionally record:

- Word Error Rate (WER)

A useful experiment matrix is:

| Input | System | Retrieval | Main Measurements |
|---|---|---|---|
| English text | LLM-only | No | Answer quality, hallucination |
| English text | RAG | Yes | Precision, Recall, relevance, groundedness, hallucination |
| Hindi text | LLM-only | No | Same answer metrics |
| Hindi text | RAG | Yes | Same retrieval + answer metrics |
| Kannada text | LLM-only | No | Same answer metrics |
| Kannada text | RAG | Yes | Same retrieval + answer metrics |
| English/Hindi/Kannada voice | RAG | Yes | WER + retrieval + answer metrics |

---

## 14. Knowledge Base Considerations

The quality of the agricultural knowledge base is likely to be as important as the model itself.

The project should prioritize sources that are:

- authoritative;
- relevant to Indian agriculture;
- current enough for the intended use;
- traceable to their source;
- clearly structured;
- appropriate for the target crops and practices.

Potential categories include:

- crop cultivation guidance;
- crop disease information;
- pest-management information;
- fertilizer guidance;
- irrigation guidance;
- soil management;
- crop selection;
- general crop-care practices.

The system should retain source metadata where possible. This can help with debugging retrieval and assessing whether generated claims are supported.

A useful document representation is:

```text
Document ID
Title
Source
Language
Agricultural Topic
Crop
Region (if applicable)
Publication / Update Date
Text Chunk
Embedding
```

---

## 15. Risks and Limitations

### 15.1 Hallucination is not completely solved by RAG

The LLM can still introduce unsupported statements even when correct context is retrieved.

### 15.2 Retrieval errors

The correct document may exist in the knowledge base but fail to appear in the top-k retrieved results.

### 15.3 Language imbalance

English may have substantially more training and evaluation resources than Hindi or Kannada. Performance should therefore be measured separately.

### 15.4 Agricultural regional variation

Agricultural recommendations can depend on crop variety, climate, soil, region, season, and farming practice. A generic answer may not be appropriate for every situation.

### 15.5 Speech recognition errors

Farmer speech, regional accents, background noise, and agricultural terminology can affect transcription quality.

### 15.6 Dataset limitations

A small evaluation set may produce unstable conclusions. Questions should cover multiple agricultural topics and difficulty levels.

### 15.7 Evaluation limitations

Automated metrics do not fully capture whether agricultural advice is useful or safe. Where feasible, expert or carefully designed human evaluation should supplement automated measurements.

---

## 16. Conclusion

The literature supports the use of Retrieval-Augmented Generation as a practical architecture for knowledge-intensive question answering, while also showing that multilingual RAG introduces additional retrieval, generation, and evaluation challenges.

For KrishiVaani AI, the most relevant research direction is therefore not simply "build an LLM chatbot." The project should experimentally test whether a curated agricultural knowledge base combined with multilingual retrieval and an LLM can improve answer quality and reduce unsupported information compared with an LLM-only baseline.

Indian-language NLP research provides useful resources for Hindi and Kannada, including multilingual models and dedicated information-retrieval resources. At the same time, recent multilingual RAG research indicates that retrieval and generation need to be evaluated carefully across languages.

Agricultural AI research further supports the importance of domain-specific knowledge and evaluation. The combination of agricultural RAG, Indian-language retrieval, voice input, and explicit LLM-versus-RAG comparison provides a clear experimental direction for the project.

The project's proposed contribution is therefore to evaluate a complete multilingual agricultural QA pipeline across English, Hindi, and Kannada, measuring retrieval quality, speech recognition quality, answer relevance, groundedness, and hallucination rate. The results can provide evidence about where multilingual RAG succeeds, where it fails, and which components require improvement.

---

## 17. References

1. Lewis, P., Perez, E., Piktus, A., et al. (2020). **Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks.** arXiv:2005.11401.  
   https://arxiv.org/abs/2005.11401

2. Chirkova, N., Rau, D., Déjean, H., Formal, T., Clinchant, S., & Nikoulina, V. (2024). **Retrieval-augmented generation in multilingual settings.** arXiv:2407.01463.  
   https://arxiv.org/abs/2407.01463

3. Dabre, R., Shrotriya, H., Kunchukuttan, A., Puduppully, R., Khapra, M., & Kumar, P. (2022). **IndicBART: A Pre-trained Model for Indic Natural Language Generation.** Findings of ACL 2022, 1849–1863.  
   https://aclanthology.org/2022.findings-acl.145/

4. Doddapaneni, S., Aralikatte, R., Ramesh, G., Goyal, S., Khapra, M., Kunchukuttan, A., & Kumar, P. (2023). **Towards Leaving No Indic Language Behind: Building Monolingual Corpora, Benchmark and Models for Indic Languages.** ACL 2023, 12402–12426.  
   https://aclanthology.org/2023.acl-long.693/

5. Gala, J., Chitale, P. A., Raghavan, A. K., et al. (2023). **IndicTrans2: Towards High-Quality and Accessible Machine Translation Models for all 22 Scheduled Indian Languages.** Transactions on Machine Learning Research.  
   https://arxiv.org/abs/2305.16307

6. Haq, S., Sharma, A., Khattab, O., Chhaya, N., & Bhattacharyya, P. (2024). **IndicIRSuite: Multilingual Dataset and Neural Information Models for Indian Languages.** ACL 2024, 501–509.  
   https://aclanthology.org/2024.acl-short.46/

7. Shi, P., Zhang, R., Bai, H., & Lin, J. (2021). **Cross-Lingual Training of Dense Retrievers for Document Retrieval.** Proceedings of the 1st Workshop on Multilingual Representation Learning, 251–253.  
   https://aclanthology.org/2021.mrl-1.24/

8. Radford, A., Kim, J. W., Xu, T., et al. (2022). **Robust Speech Recognition via Large-Scale Weak Supervision.** OpenAI.  
   https://cdn.openai.com/papers/whisper.pdf

9. Vijayvargia, A., Nagpal, A., Pundalik, K., et al. (2025). **Intent Aware Context Retrieval for Multi-Turn Agricultural Question Answering.** arXiv:2508.03719.  
   https://arxiv.org/abs/2508.03719

10. Eldem, A., & Eldem, H. (2026). **The development and evaluation of agricultural question-answering systems based on large language models.** Scientific Reports, 16, 5357.  
    https://www.nature.com/articles/s41598-026-35003-9

11. Xia, F., Pan, J., Zhong, R., et al. (2026). **ChatCEA: a knowledge-driven intelligent service agent for controlled environment agriculture.** Computers and Electronics in Agriculture, 248, 111733.  
    https://www.sciencedirect.com/science/article/pii/S0168169926003285

---

## 18. Alignment with KrishiVaani AI

This literature review aligns with the project's existing research definition:

- **Languages:** English, Hindi, Kannada.
- **Input:** Text and voice.
- **Retrieval:** Vector-based retrieval using PostgreSQL + PGVector.
- **Generation:** LLM using retrieved agricultural context.
- **Baseline:** LLM-only.
- **Experiments:** LLM-only, RAG, and multilingual/cross-lingual evaluation.
- **Metrics:** Precision, Recall, WER, Hallucination Rate, Answer Relevance, and Groundedness.
- **Primary research goal:** Determine whether multilingual RAG improves agricultural question answering and reduces hallucinations.

The literature review should be treated as the research foundation for subsequent implementation and experimental evaluation. It does not by itself establish that RAG will outperform the project's baseline; that conclusion should be made only after the planned experiments are completed.
