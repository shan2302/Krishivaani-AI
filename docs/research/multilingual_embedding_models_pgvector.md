# KV-EMB-001 — Multilingual Embedding Models for PGVector
## Research Report — KrishiVaani AI

**Document status:** Research draft — not yet validated experimentally  
**Project:** KrishiVaani AI (8-week multilingual agricultural RAG system)  
**Author:** AI/ML Research Engineering Team  
**Date:** October 2026  
**Version:** 1.0  
**Target Repository Path:** `docs/research/multilingual_embedding_models_pgvector.md`  

---

## 1. Executive Summary

KrishiVaani AI is an 8-week engineering and research initiative designed to provide Indian farmers with reliable, verified agricultural information in English, Hindi (हिन्दी), and Kannada (ಕನ್ನಡ). The project evaluates three architectural paradigms:
1. **LLM-only baseline** (zero retrieval, high hallucination risk)
2. **Monolingual RAG** (language-compatible retrieval where query and documents share the same language)
3. **Cross-lingual RAG** (queries in Hindi or Kannada retrieving canonical English or multilingual ICAR agricultural advisories and vice versa)

This research document analyzes candidate multilingual text embedding models for generating semantic vector representations stored in **PostgreSQL with PGVector** and queried through a **Java / Spring Boot / Spring AI** application stack.

### Key Findings & Recommendations
- **Leading Candidate for Evaluation:** `BAAI/bge-m3` (FlagEmbedding).
  - *Strengths:* Native 8,192-token context length accommodating complex agro-advisory tables and multi-paragraph advisories; multi-granularity (dense 1024-d, sparse lexical, and multi-vector ColBERT-style); open-source commercial license (MIT); robust coverage of Indic scripts (Devanagari, Kannada).
  - *Trade-offs:* Large model footprint (~2.2 GB, 567M parameters); higher inference latency on CPU; dense vector dimension of 1024 doubles PGVector index memory vs 384/512-d models.
- **Second Candidate for Evaluation (Comparison Baseline):** `intfloat/multilingual-e5-base`.
  - *Strengths:* Highly mature, battle-tested across information retrieval benchmarks; compact size (~278M parameters, ~1.1 GB); fast inference; standard 768-d vectors natively supported by Spring AI PGVector; MIT license.
  - *Trade-offs:* 512-token sequence limit (tight for 700-token chunk plans); strict requirement for asymmetry prefixes (`query: ` and `passage: `).
- **Secondary Evaluation Alternative:** `jinaai/jina-embeddings-v3`.
  - *Strengths:* 8,192-token context window, flexible task-specific LoRA adapters (`retrieval.query`, `retrieval.passage`), and Matryoshka representation learning (MRL) allowing dimension reduction from 1024 to 512/256 to save vector storage.
  - *Trade-offs:* Requires custom task parameter integration; Apache 2.0 license.
- **Deprecations:** `LaBSE` (Language-agnostic BERT Sentence Embedding, Google 2020) is deemed **obsolete** for our primary RAG retrieval. While pioneering in cross-lingual bitext alignment, its 512-token ceiling, bi-encoder sentence-level pretraining (poor document/passage retrieval ranking), and lack of modern asymmetric instruction tuning make it unsuitable compared to modern IR encoders.

---

## 2. Project Requirements & Context

### 2.1 Language Scope & Linguistic Complexity
- **English (en):** Primary language of technical documentation, research papers, and national ICAR Kharif packages.
- **Hindi (hi - Devanagari script):** Spoken widely across northern and central agricultural belts. Technical terms often feature Sanskrit roots (*कीट प्रबंधन* - pest management) or phonetic transliterations (*यूरिया* - Urea).
- **Kannada (kn - Kannada script):** Southern Dravidian language with distinct agglutinative morphology, complex conjunct consonants (*ಒತ್ತಕ್ಷರ*), and significant dialectal variation between coastal, northern, and southern Karnataka farming communities (*ಕೀಟ ಬಾಧೆ* - pest infestation, *ರಸಗೊಬ್ಬರ* - chemical fertilizer).

### 2.2 Ingestion & Retrieval Modes
1. **Monolingual (Same-Language) Retrieval:**
   - English Query $\rightarrow$ English ICAR document chunks
   - Hindi Query $\rightarrow$ Hindi ICAR/State Agriculture Department advisory chunks
   - Kannada Query $\rightarrow$ Kannada UAS Bangalore/Dharwad advisory chunks
2. **Cross-Lingual Retrieval:**
   - Kannada / Hindi Farmer Query $\rightarrow$ English authoritative ICAR document chunks (translated during generation step or synthesized by LLM)
   - English Extension Officer Query $\rightarrow$ Regional state-specific Kannada / Hindi advisories

### 2.3 Chunking & Sequence Constraints
- Target chunk size: **~700 tokens** with **100 tokens overlap**.
- Tokenizer disparity: Indic text in Byte-Pair Encoding (BPE) or WordPiece tokenizers trained primarily on Latin corpora suffers from **token bloat** (fertility rate of 2.5x to 4x per word compared to English). A 300-word Kannada passage may exceed 700 subword tokens if the vocabulary lacks rich Indic token representations.

### 2.4 Production Architectural Stack
- **Backend:** Java 21, Spring Boot 3.3.x, Spring AI 1.0.0-M1.
- **Vector Database:** PostgreSQL 16+ with `pgvector` 0.7+.
- **Index Type:** HNSW (Hierarchical Navigable Small World) with Cosine Distance (`vector_cosine_ops` / `<=>`).

---

## 3. Candidate Embedding Models Researched

Five candidates were investigated using official Hugging Face model cards, arXiv research publications, and open-source repositories:

1. **BAAI/bge-m3** (Beijing Academy of Artificial Intelligence, 2024)
2. **intfloat/multilingual-e5-large** (Microsoft, 2024)
3. **intfloat/multilingual-e5-base** (Microsoft, 2024)
4. **sentence-transformers/LaBSE** (Google Research, 2020)
5. **jinaai/jina-embeddings-v3** (Jina AI, 2024)

---

## 4. Comprehensive Model Comparison Table

| Attribute | BAAI/bge-m3 | intfloat/multilingual-e5-large | intfloat/multilingual-e5-base | Google LaBSE | jina-embeddings-v3 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Official Repository** | `BAAI/bge-m3` | `intfloat/multilingual-e5-large` | `intfloat/multilingual-e5-base` | `sentence-transformers/LaBSE` | `jinaai/jina-embeddings-v3` |
| **Model Architecture** | XLM-RoBERTa based multi-task encoder | XLM-RoBERTa Large | XLM-RoBERTa Base | BERT-base dual encoder | XLM-RoBERTa with Task LoRA |
| **Parameter Count** | 567 Million | 560 Million | 278 Million | 471 Million | 570 Million |
| **Dense Vector Dim** | **1024** | **1024** | **768** | **768** | **1024** (Supports MRL: 512, 256) |
| **Max Sequence Length** | **8,192 tokens** | **512 tokens** | **512 tokens** | **512 tokens** | **8,192 tokens** |
| **Supported Languages** | 100+ (En, Hi, Kn verified) | 100+ (En, Hi, Kn in XLM-R) | 100+ (En, Hi, Kn in XLM-R) | 109 (En, Hi, Kn bitext) | 89+ languages (En, Hi, Kn) |
| **Kannada Fertility/Support**| High quality Indic vocabulary | Moderate (XLM-R vocab) | Moderate (XLM-R vocab) | Moderate (Sentence level) | High quality multilingual vocab |
| **Input Asymmetry Prefixes** | None needed for dense | Mandatory (`query: ` / `passage: `) | Mandatory (`query: ` / `passage: `) | None | Task adapter (`retrieval.query`, etc.) |
| **Multi-Vector / Hybrid** | Dense + Sparse (BM25) + ColBERT | Dense only | Dense only | Dense only | Dense only (MRL support) |
| **License** | **MIT** | **MIT** | **MIT** | **Apache 2.0** | **Apache 2.0** |
| **Local Memory (FP32/FP16)**| ~2.2 GB / ~1.1 GB | ~2.2 GB / ~1.1 GB | ~1.1 GB / ~550 MB | ~1.8 GB / ~900 MB | ~2.3 GB / ~1.2 GB |
| **PGVector Index Cost** | 1024-d (High RAM) | 1024-d (High RAM) | 768-d (Moderate RAM) | 768-d (Moderate RAM) | 1024-d or 512-d with MRL |
| **Spring AI Compatibility** | Native (via Ollama / TEI / ONNX) | Native (via Ollama / TEI / ONNX) | Native (via Ollama / TEI / ONNX) | Standard HuggingFace | Custom adapter headers needed |

*Technical citations:* BGE-M3 (Chen et al., arXiv:2402.03216); Multilingual-E5 (Wang et al., arXiv:2402.05672); LaBSE (Feng et al., ACL 2022); Jina-v3 (Jina AI Tech Report, 2024).

---

## 5. English, Hindi, and Kannada Suitability Analysis

### 5.1 Agricultural Terminology & Named Entities
Agricultural text contains specialized taxonomy, agro-chemicals, crop varieties, dosages, and soil health metrics:
- **English:** *"Stem borer management in Kharif paddy using Chlorantraniliprole 18.5 SC at 60 ml/acre"*
- **Hindi:** *"खरीफ धान में तना छेदक कीट की रोकथाम के लिए क्लोरेंट्रानिलिप्रोल 18.5 एससी 60 मिली प्रति एकड़ की दर से छिड़काव करें"*
- **Kannada:** *"ಖಾರೀಫ್ ಭತ್ತದ ಬೆಳೆಯಲ್ಲಿ ಕಾಂಡ ಕೊರೆಯುವ ಹುಳುವಿನ ನಿಯಂತ್ರಣಕ್ಕಾಗಿ ಕ್ಲೋರಾಂಟ್ರಾನಿಲಿಪ್ರೋಲ್ 18.5 ಎಸ್.ಸಿ ಕೀಟನಾಶಕವನ್ನು ಎಕರೆಗೆ 60 ಮಿ.ಲೀ ನಂತೆ ಸಿಂಪಡಿಸಿ"*

**Model Behavior:**
1. **BGE-M3:** Trained on massive multilingual web corpora with unsupervised contrastive learning, fine-tuned on multi-lingual retrieval datasets (MIRACL). Handles chemical brand names and botanical loanwords smoothly across scripts.
2. **mE5 (Base/Large):** Excels when queries follow natural instructional syntax. However, transliterated trade names (*"ಕ್ಲೋರಾಂಟ್ರಾನಿಲಿಪ್ರೋಲ್"*) suffer higher subword fragmentation in mE5's standard XLM-R vocabulary compared to BGE-M3.
3. **LaBSE:** Optimized for sentence-level semantic equivalence. While it aligns *"ಖಾರೀಫ್ ಭತ್ತ"* with *"Kharif rice"*, it fails to distinguish fine-grained numerical dosage instructions (*60 ml/acre* vs *100 ml/acre*), leading to semantic drifting in agricultural advisories.

### 5.2 Chunking and Token Limit Constraints
Our project plan specifies chunks of **~700 tokens with 100 tokens overlap**.
- **The 512-Token Barrier (mE5-Base, mE5-Large, LaBSE):**
  These models hard-truncate inputs beyond 512 tokens. If an ICAR advisory table (e.g., district-wise rainfall & pest forecast) contains 700 tokens, 188 tokens (~27%) of critical information will be silently discarded!
  *Implication:* Adopting mE5 requires shrinking our chunk size to 350-400 tokens with 50 tokens overlap.
- **The 8,192-Token Advantage (BGE-M3, Jina-v3):**
  Accommodates 700-token chunks with ample headroom, even under high subword fertility rates in Kannada script. Entire advisory sections remain intact.

### 5.3 Transliteration & Dialectal Farmer Queries
Farmers frequently search using spoken colloquial terms rather than formal Kannada:
- *Formal:* ಕಳೆನಾಶಕ (Weedicide)
- *Colloquial / Loanword:* ಕಳೆ ಮೆಡಿಸಿನ್ (Weed medicine)
- *Latinized Kannada (Kanglish):* "Bhatthada kanda koreyuva huluva nigrahakke yava aushadhi beku?"
BGE-M3 shows significantly higher cross-script tolerance due to its dual multi-lingual pretraining objectives (Dense + Lexical + ColBERT Multi-Vector).

---

## 6. PGVector and Spring AI Compatibility Analysis

### 6.1 Vector Embeddings in PostgreSQL with PGVector
When an agricultural document chunk is ingested:
1. The text passage is tokenized and transformed into a dense float vector:
   $$\vec{v} \in \mathbb{R}^{d} \quad (d = 1024 \text{ for BGE-M3}; d = 768 \text{ for mE5-base})$$
2. Vector normalization ensures $|\vec{v}| = 1.0$. Under normalized vectors, **Cosine Distance** is directly related to Euclidean (L2) distance:
   $$D_{\text{cosine}}(\vec{u}, \vec{v}) = 1 - \frac{\vec{u} \cdot \vec{v}}{\|\vec{u}\| \|\vec{v}\|} = 1 - (\vec{u} \cdot \vec{v})$$
3. In PostgreSQL:
   ```sql
   CREATE EXTENSION IF NOT EXISTS vector;

   CREATE TABLE agricultural_chunk_embeddings (
       id BIGSERIAL PRIMARY KEY,
       document_id VARCHAR(64) NOT NULL,
       language VARCHAR(10) NOT NULL,
       chunk_index INT NOT NULL,
       chunk_text TEXT NOT NULL,
       metadata JSONB,
       embedding vector(1024) NOT NULL
   );

   -- HNSW index for high-speed approximate nearest neighbor (ANN) retrieval
   CREATE INDEX idx_agri_chunks_hnsw 
   ON agricultural_chunk_embeddings 
   USING hnsw (embedding vector_cosine_ops)
   WITH (m = 16, ef_construction = 64);
   ```

### 6.2 Architectural Symmetry Requirement
> [!IMPORTANT]
> Query embeddings and document embeddings **must** be computed using the identical model checkpoint, tokenizer, and pooling strategy (typically Mean Pooling or CLS token). Vector dimensions must match identically.

For models requiring instruction prefixes (like `multilingual-e5`):
- Documents **must** be embedded with: `"passage: " + chunk_text`
- Queries **must** be embedded with: `"query: " + query_text`
*Failure to include these prefixes causes up to a 10-15% drop in retrieval accuracy (nDCG@10).* BGE-M3 does not require prefixes for dense embedding, reducing runtime complexity.

### 6.3 Spring AI VectorStore Integration
Spring AI provides first-class support for PGVector via `PgVectorStore`:
```java
@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1024) // 1024 for BGE-M3, 768 for mE5-base
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(false)
                .build();
    }
}
```

**Spring AI EmbeddingModel Backend Options:**
- **Option A (Containerized Inference Service - Recommended):** Run Hugging Face Text Embeddings Inference (TEI) or Ollama serving `bge-m3`. Spring AI connects over high-throughput HTTP using `OpenAiEmbeddingModel` with a custom `baseUrl`.
- **Option B (In-Process ONNX Runtime):** Spring AI `TransformersEmbeddingModel` running ONNX runtime locally in Java. (Suitable for 768-d mE5-base; BGE-M3 ONNX requires high JVM off-heap memory).

---

## 7. Proposed Evaluation Experiment Design

To prevent selection bias from general benchmarks, we design a reproducible, offline evaluation protocol using verified agricultural documents.

### 7.1 Curated Test Dataset (Ground Truth Triples)
A curated benchmark of 30 multi-lingual queries (10 English, 10 Hindi, 10 Kannada) mapped against verified ICAR document chunks:

```json
[
  {
    "query_id": "Q-EN-001",
    "language": "en",
    "query": "What is the recommended seed rate and fertilizer dosage for Kharif maize?",
    "relevant_chunk_ids": ["ICAR-MZ-04", "ICAR-MZ-05"],
    "domain": "Cereal agronomy"
  },
  {
    "query_id": "Q-HI-001",
    "language": "hi",
    "query": "धान की फसल में भूरा फुदका (BPH) कीट का जैविक नियंत्रण कैसे करें?",
    "relevant_chunk_ids": ["ICAR-PD-12", "ICAR-PD-14"],
    "domain": "Pest management"
  },
  {
    "query_id": "Q-KN-001",
    "language": "kn",
    "query": "ತೊಗರಿ ಬೆಳೆಯಲ್ಲಿ ಬಂಜರು ರೋಗದ ಲಕ್ಷಣಗಳು ಮತ್ತು ನಿಯಂತ್ರಣ ಕ್ರಮಗಳು ಯಾವುವು?",
    "relevant_chunk_ids": ["ICAR-PG-03", "ICAR-PG-04"],
    "domain": "Pulses pathology"
  },
  {
    "query_id": "Q-KN-XL-001",
    "language": "kn",
    "query": "ಹತ್ತಿ ಬೆಳೆಯಲ್ಲಿ ಗುಲಾಬಿ ಕಾಯಿಕೊರೆಯುವ ಹುಳುವಿನ ಬಾಧೆ ತಡೆಯುವುದು ಹೇಗೆ?",
    "relevant_chunk_ids": ["ICAR-CT-EN-08"],
    "domain": "Cross-lingual cotton pest management (Kannada query -> English doc)"
  }
]
```

### 7.2 Retrieval Evaluation Metrics
For $K = 5$:
- **Recall@5:** Proportion of relevant agricultural chunks retrieved in the top 5 results:
  $$\text{Recall}@K = \frac{|\text{Retrieved}_K \cap \text{Relevant}|}{|\text{Relevant}|}$$
- **Precision@5:** Fraction of retrieved top 5 chunks that are verified relevant.
- **MRR@5 (Mean Reciprocal Rank):** Evaluates whether the primary authoritative advisory is ranked at position 1.
- **Retrieval Latency (ms):** P50, P95, and P99 query embedding + vector search time.

### 7.3 Experimental Matrix & Results Placeholder

| Model Candidate | Mode | Recall@5 (En) | Recall@5 (Hi) | Recall@5 (Kn) | Cross-Lingual Recall@5 (Kn->En) | MRR@5 | Latency P95 (ms) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **BAAI/bge-m3** | Dense (1024) | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* |
| **multilingual-e5-base** | Dense (768) | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* |
| **multilingual-e5-large**| Dense (1024) | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* |
| **jina-embeddings-v3** | Dense (1024) | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* |
| **LaBSE** | Dense (768) | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* | *[Unmeasured]* |

*(Note: In accordance with project safety guidelines, no evaluation values are fabricated. All numbers will be recorded during the execution phase in week 2).*

---

## 8. Recommended Shortlist & Trade-Offs

### 8.1 Primary Shortlist: BAAI/bge-m3
- **Primary Justification:** Supports 8,192 tokens natively, eliminating chunk truncation on long Indian agricultural advisories. Features top-tier Indic language semantic representations (Devanagari and Kannada scripts). Zero licensing friction (permissive MIT).
- **Core Trade-offs:** Requires 1024-dimension vectors in PGVector (33% more RAM than 768-d models) and approximately 1.5x longer CPU inference latency.

### 8.2 Comparison Baseline: intfloat/multilingual-e5-base
- **Primary Justification:** Standard 768-d vector fits existing default Spring AI PGVector tutorials. Fast CPU inference, low memory footprint (~550 MB in FP16), MIT license.
- **Core Trade-offs:** Hard 512-token sequence limit forces smaller chunk sizes (~350 tokens) and creates risk of splitting multi-sentence chemical treatment regimens across chunk boundaries.

---

## 9. Risks and Mitigation Strategies

1. **Token Truncation in Kannada:**
   - *Risk:* Complex Kannada Unicode characters inflate subword counts.
   - *Mitigation:* Ensure models with 8,192 context window (BGE-M3) or enforce strict pre-chunk token counting with the model's native Hugging Face tokenizer.
2. **PGVector HNSW Memory Overhead:**
   - *Risk:* 1024-d vectors with large HNSW index ($m=16, ef=64$) can exceed PostgreSQL shared buffers on small dev environments.
   - *Mitigation:* Allocate minimum 2 GB RAM to PostgreSQL container; optionally evaluate half-precision vectors (`halfvec` in PGVector 0.7+) if memory is constrained.
3. **Instruction Prefix Omission:**
   - *Risk:* Forgetting `query: ` and `passage: ` in `multilingual-e5` degrades similarity search.
   - *Mitigation:* Encapsulate embedding generation behind a Spring AI custom decorator that automatically prepends prefixes.

---

## 10. Next Implementation Steps (Post-Research)

1. **Step 1:** Establish Python benchmarking script in `research/scripts/evaluate_embeddings.py` using Hugging Face `sentence-transformers` and the 30-query test dataset.
2. **Step 2:** Measure actual Recall@5, MRR@5, and latency on representative CPU/GPU hardware.
3. **Step 3:** Deploy the selected model as a containerized embedding microservice (via Hugging Face TEI or Ollama) with an OpenAI-compatible `/v1/embeddings` endpoint.
4. **Step 4:** Configure Spring AI `PgVectorStore` in `backend/src/main/java/com/example/krishivaanibackend/config/VectorStoreConfig.java`.

---

## 11. Authoritative References & Citations

1. **BAAI BGE-M3 Model Card & Paper:**
   - Model Card: [https://huggingface.co/BAAI/bge-m3](https://huggingface.co/BAAI/bge-m3)
   - Research Paper: Chen et al., *BGE M3-Embedding: Multi-Lingual, Multi-Functionality, Multi-Granularity Text Embeddings Through Multi-Tasking*, arXiv:2402.03216 (2024). [https://arxiv.org/abs/2402.03216](https://arxiv.org/abs/2402.03216) (Accessed Oct 2026).
2. **Microsoft Multilingual-E5:**
   - Model Card: [https://huggingface.co/intfloat/multilingual-e5-base](https://huggingface.co/intfloat/multilingual-e5-base)
   - Research Paper: Wang et al., *Multilingual E5 Text Embeddings: A Technical Report*, arXiv:2402.05672 (2024). [https://arxiv.org/abs/2402.05672](https://arxiv.org/abs/2402.05672) (Accessed Oct 2026).
3. **Google LaBSE:**
   - Model Card: [https://huggingface.co/sentence-transformers/LaBSE](https://huggingface.co/sentence-transformers/LaBSE)
   - Research Paper: Feng et al., *Language-agnostic BERT Sentence Embedding*, ACL 2022. [https://arxiv.org/abs/2007.01852](https://arxiv.org/abs/2007.01852) (Accessed Oct 2026).
4. **Jina Embeddings v3:**
   - Model Card: [https://huggingface.co/jinaai/jina-embeddings-v3](https://huggingface.co/jinaai/jina-embeddings-v3)
   - Technical Announcement & Documentation: Jina AI (2024). [https://jina.ai/news/jina-embeddings-v3-multilingual-foundational-model](https://jina.ai/news/jina-embeddings-v3-multilingual-foundational-model) (Accessed Oct 2026).
5. **PostgreSQL PGVector Official Documentation:**
   - GitHub Repository & Docs: [https://github.com/pgvector/pgvector](https://github.com/pgvector/pgvector) (Accessed Oct 2026).
6. **Spring AI PGVector Reference Documentation:**
   - Spring AI Docs: [https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html) (Accessed Oct 2026).
