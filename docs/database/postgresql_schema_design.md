# PostgreSQL Schema Design and ER Architecture for KrishiVaani AI

**Document status:** Final Architectural Proposal — Design and Planning Specification  
**Project:** KrishiVaani AI (Multilingual Agricultural RAG: English, Hindi, Kannada)  
**Database Target:** Neon Serverless PostgreSQL 16+ with `pgvector` 0.7+  
**Application Backend:** Java 21, Spring Boot 4.1.x, Spring Data JPA, Spring AI 2.0.x  
**File Path:** `docs/database/postgresql_schema_design.md`  

---

## 1. Executive Summary & Requirements

KrishiVaani AI is an 8-week engineering and scientific research initiative designed to deliver verified agricultural decision support to Indian farmers across English, Hindi (हिन्दी), and Kannada (ಕನ್ನಡ). The project compares three core AI architectures:
1. **LLM-only baseline:** Direct generative generation without retrieval grounding.
2. **Monolingual RAG:** Language-matched dense retrieval and grounded synthesis (e.g., Hindi Query $\rightarrow$ Hindi advisory chunks $\rightarrow$ Hindi answer).
3. **Cross-lingual RAG:** Multi-script semantic retrieval where queries in Hindi or Kannada retrieve canonical national ICAR advisories in English or other languages, with context translated or synthesized by the LLM in the user's native language.

To support this mission, the persistent storage layer must accommodate:
- **Verified Source Documents:** Relational records tracking provenance, organizations (ICAR, state universities), publication years, and verification logs.
- **Granular Document Chunks:** Traceable text segments (~700 tokens with 100 token overlap) with exact page number offsets, heading hierarchies, and subword token counts.
- **Vector Storage & PGVector Integration:** High-dimensional dense embeddings queried via Cosine Distance (`<=>`) with Hierarchical Navigable Small World (HNSW) indexing, fully aligned with Spring AI's `VectorStore` abstractions.
- **Asynchronous Ingestion Tracking:** Transparent audit logging of multi-stage PDF ingestion pipelines (extraction $\rightarrow$ chunking $\rightarrow$ embedding $\rightarrow$ vector upsert).
- **Research Evaluation & Benchmarking:** Ground-truth question datasets (such as `KV-DATASET-EN-RAW-001`), reference answers, human relevance judgements, experiment runs, and quantitative metric tracking (Recall@5, Precision@5, MRR@5, Latency).

---

## 2. Architectural Boundary & Separation of Concerns

A critical design principle for KrishiVaani AI is the explicit boundary between **Application-Managed Relational Tables** and **Spring AI-Managed Vector Storage**:

```
+--------------------------------------------------------------------------------------------------+
|                                    NEON POSTGRESQL DATABASE                                      |
|                                                                                                  |
|  [APPLICATION / JPA MANAGED SCHEMA]                  [SPRING AI / PGVECTOR MANAGED]              |
|  - agricultural_documents (Source Metadata)          - vector_store                              |
|  - document_chunks (Text Chunks & Provenance)          (id, content, metadata, embedding)        |
|  - ingestion_jobs (Pipeline Tracking)                                                            |
|  - questions (User Query Logs & Answers)             Linked logically via:                       |
|  - users (Authentication & Roles)                    metadata->>'chunk_id'                       |
|                                                      metadata->>'document_id'                    |
|  [RESEARCH & EVALUATION SCHEMA]                                                                  |
|  - evaluation_questions (Ground Truth Dataset)                                                   |
|  - evaluation_runs (Experiment Configurations)                                                   |
|  - evaluation_results (Metric Scores per Query)                                                  |
+--------------------------------------------------------------------------------------------------+
```

### Why Avoid Duplicate Vector Tables?
Spring AI provides the `PgVectorStore` abstraction, which expects a standardized schema (`id uuid`, `content text`, `metadata jsonb`, `embedding vector(dim)`). If application code creates its own custom vector table with custom column names and manual vector queries while also configuring `PgVectorStore`, the system incurs redundant index storage (each 1024-d index consumes significant RAM), synchronization discrepancies, and brittle custom SQL. 

By linking `document_chunks.chunk_id` to `vector_store.metadata->>'chunk_id'`, we retain full relational referential integrity in Spring Data JPA while delegating vector indexing and similarity search to Spring AI's battle-tested engine.

---

## 3. Entity Descriptions & Logical Data Model

### 3.1 Domain Group A: Core Production Relational Entities

#### 1. `agricultural_documents`
Represents verified, authoritative agricultural source publications (such as the *ICAR Kharif Agro-Advisories for Farmers 2025*).
- **Primary Key:** `document_id VARCHAR(64)` (natural stable identifier such as `ICAR_KHARIF_2025_EN`, matching `metadata/documents.json`).
- **Provenance Attributes:** `title`, `organization`, `publication_year`, `language`, `source_url`, `original_filename`, `document_type`.
- **Classification Attributes:** `category` (e.g., *Crop Production, Pest Management*), `season` (*Kharif, Rabi*), `region` (*National, Northern Hills, South Peninsular*).
- **Operational Lifecycle:** `verification_status` (*pending_review, verified, rejected*), `ingestion_status` (*registered, processing, ingested, failed*), timestamps.

#### 2. `document_chunks`
Represents individual text segments produced by the chunking preprocessor.
- **Primary Key:** `chunk_id VARCHAR(64)` (e.g., `ICAR_KHARIF_2025_EN_CHK_0012`).
- **Foreign Key:** `document_id` referencing `agricultural_documents(document_id)` on delete cascade.
- **Locational Grounding:** `chunk_index` (0-based sequence), `chunk_text` (unmodified text segment), `language` (`en`, `hi`, `kn`), `source_page_start`, `source_page_end`, `section_heading`.
- **Model Traceability:** `token_count` (measured via BPE/WordPiece tokenizer), `chunking_strategy` (e.g., `recursive_character_700_100_overlap`), `content_hash` (SHA-256 for deduplication).

#### 3. `ingestion_jobs`
Tracks batch and background processing of document extraction, chunking, and embedding runs.
- **Primary Key:** `job_id UUID`.
- **References:** `document_id` referencing `agricultural_documents(document_id)`.
- **Execution Lifecycle:** `job_status` (*QUEUED, EXTRACTING, CHUNKING, EMBEDDING, COMPLETED, FAILED*), `pipeline_version`, `total_chunks_created`, `error_message`, `started_at`, `completed_at`.

#### 4. `questions` (Extending Existing JPA Entity)
Stores farmer questions submitted through web or voice interfaces, preserving language choice, audit history, and generated responses.
- **Primary Key:** `id BIGSERIAL` (retaining backward compatibility with existing `Question.java`).
- **Attributes:** `content` (farmer query text), `language` (`en`, `hi`, `kn`), `audio_input_url` (optional voice recording path), `answer` (generated response), timestamps.

#### 5. `users` (Existing JPA Entity)
Manages user authentication, farmer profiles, and authorization tokens.
- **Primary Key:** `id BIGSERIAL`.
- **Attributes:** `name`, `email` (unique), `password`, `role` (`USER`, `ADMIN`, `EXTENSION_OFFICER`), timestamps.

---

### 3.2 Domain Group B: Vector Storage Layer (Spring AI PGVector)

#### 6. `vector_store`
The official vector persistence table managed by Spring AI's `PgVectorStore`.
- **Primary Key:** `id UUID` (generated by Spring AI).
- **Payload Columns:** `content TEXT` (the text chunk embedded for retrieval), `metadata JSONB` (stores structured properties for filtering: `document_id`, `chunk_id`, `language`, `topic`, `season`, `page`).
- **Vector Column:** `embedding VECTOR(dim)` (dimension determined by embedding model: 1024 for `BAAI/bge-m3`, 768 for `multilingual-e5-base`).
- **Index:** HNSW index on `embedding vector_cosine_ops` for millisecond-scale approximate nearest neighbor retrieval.

---

### 3.3 Domain Group C: Research & Offline Evaluation Schema

#### 7. `evaluation_questions`
Persistent database mirror of curated test benchmarks (such as `docs/research/datasets/raw_english_agricultural_questions.csv`).
- **Primary Key:** `question_id VARCHAR(32)` (e.g., `ENQ001`).
- **Attributes:** `question_text`, `language`, `topic`, `crop`, `region`, `season`, `expected_answer`, `source_doc_id`, `source_page`, `answerable_from_source`, `difficulty`, `notes`.

#### 8. `evaluation_runs`
Logs scientific experiment executions comparing retrieval and generation parameters.
- **Primary Key:** `run_id UUID`.
- **Configurations:** `run_name`, `system_mode` (`LLM_ONLY`, `MONOLINGUAL_RAG`, `CROSS_LINGUAL_RAG`), `embedding_model` (e.g., `BAAI/bge-m3`), `llm_model` (e.g., `gpt-oss-120b`, `llama-3.3-70b`), `top_k`, `temperature`, `notes`, `created_at`.

#### 9. `evaluation_results`
Stores fine-grained query-level performance metrics for each evaluation question within a run.
- **Primary Key:** `result_id BIGSERIAL`.
- **Foreign Keys:** `run_id` references `evaluation_runs(run_id)`, `question_id` references `evaluation_questions(question_id)`.
- **Metrics:** `retrieved_chunk_ids` (array of returned chunk identifiers), `is_hit_at_k` (boolean), `recall_at_k` (decimal), `precision_at_k` (decimal), `reciprocal_rank` (MRR component), `generation_answer` (actual LLM response), `retrieval_latency_ms`, `total_latency_ms`.

---

## 4. Entity-Relationship (ER) Diagram

```mermaid
erDiagram
    AGRICULTURAL_DOCUMENTS ||--o{ DOCUMENT_CHUNKS : "contains"
    AGRICULTURAL_DOCUMENTS ||--o{ INGESTION_JOBS : "processed by"
    DOCUMENT_CHUNKS ..o{ VECTOR_STORE : "indexed as (metadata link)"
    USERS ||--o{ QUESTIONS : "submits"
    EVALUATION_RUNS ||--o{ EVALUATION_RESULTS : "produces"
    EVALUATION_QUESTIONS ||--o{ EVALUATION_RESULTS : "tested against"

    AGRICULTURAL_DOCUMENTS {
        varchar(64) document_id PK
        varchar(500) title
        varchar(255) organization
        integer publication_year
        varchar(30) language
        text source_url
        varchar(255) original_filename
        varchar(100) document_type
        varchar(100) category
        varchar(50) season
        varchar(100) region
        varchar(50) verification_status
        varchar(50) ingestion_status
        timestamp created_at
        timestamp updated_at
    }

    DOCUMENT_CHUNKS {
        varchar(64) chunk_id PK
        varchar(64) document_id FK
        integer chunk_index
        text chunk_text
        varchar(30) language
        integer source_page_start
        integer source_page_end
        varchar(255) section_heading
        integer token_count
        varchar(100) chunking_strategy
        varchar(64) content_hash
        timestamp created_at
    }

    INGESTION_JOBS {
        uuid job_id PK
        varchar(64) document_id FK
        varchar(50) job_status
        varchar(50) pipeline_version
        integer total_chunks_created
        text error_message
        timestamp started_at
        timestamp completed_at
    }

    VECTOR_STORE {
        uuid id PK
        text content
        jsonb metadata
        vector embedding
    }

    QUESTIONS {
        bigserial id PK
        text content
        varchar(10) language
        varchar(500) audio_input_url
        text answer
        timestamp created_at
        timestamp updated_at
    }

    USERS {
        bigserial id PK
        varchar(50) name
        varchar(255) email UK
        varchar(255) password
        varchar(50) role
        timestamp created_at
    }

    EVALUATION_QUESTIONS {
        varchar(32) question_id PK
        text question_text
        varchar(10) language
        varchar(100) topic
        varchar(100) crop
        varchar(100) region
        varchar(50) season
        text expected_answer
        varchar(64) source_doc_id
        varchar(50) source_page
        boolean answerable_from_source
        varchar(20) difficulty
        text notes
        timestamp created_at
    }

    EVALUATION_RUNS {
        uuid run_id PK
        varchar(100) run_name
        varchar(50) system_mode
        varchar(100) embedding_model
        varchar(100) llm_model
        integer top_k
        decimal temperature
        text notes
        timestamp created_at
    }

    EVALUATION_RESULTS {
        bigserial result_id PK
        uuid run_id FK
        varchar(32) question_id FK
        text retrieved_chunk_ids
        boolean is_hit_at_k
        decimal recall_at_k
        decimal precision_at_k
        decimal reciprocal_rank
        text generation_answer
        integer retrieval_latency_ms
        integer total_latency_ms
    }
```

---

## 5. Table Specifications & Indexing Strategy

### 5.1 Index Types & Rationale
1. **B-Tree Indexes:**
   - Standard primary and foreign keys (`document_id`, `chunk_id`, `run_id`).
   - Categorical filters frequently applied in queries (`language`, `verification_status`, `category`).
   - Temporal sorting (`created_at`).
2. **GIN Indexes (JSONB):**
   - In `vector_store`, filtering is performed over JSONB fields (e.g., `WHERE metadata->>'language' = 'kn'`). A JSONB path ops index (`jsonb_path_ops`) drastically accelerates pre-filtering.
3. **HNSW Vector Index:**
   - Hierarchical Navigable Small World index on `embedding vector_cosine_ops`.
   - Tuned parameters: `m = 16` (connections per node) and `ef_construction = 64` (search depth during build).
   - Provides $O(\log N)$ approximate nearest neighbor retrieval latency, essential for interactive farmer voice assistants.

---

## 6. PGVector Dimension Strategy & Dynamic Handling

### 6.1 Vector Dimension Specification
As established in our multilingual embedding research ([`docs/research/multilingual_embedding_models_pgvector.md`](file:///c:/Krishivaani-AI/Krishivaani-AI/docs/research/multilingual_embedding_models_pgvector.md)):
- Leading Candidate (`BAAI/bge-m3`): **1024 dimensions**
- Comparison Baseline (`intfloat/multilingual-e5-base`): **768 dimensions**

> [!IMPORTANT]
> PostgreSQL with PGVector enforces strict dimension invariance: a column defined as `vector(1024)` will reject any vector of dimension 768.

### 6.2 Managing Embedding Model Migration or Re-Embedding
When re-indexing from a 768-d baseline to a 1024-d model:
- **Strategy 1 (Separate Vector Tables):** Create version-named vector stores (e.g., `vector_store_bge_m3_1024` vs `vector_store_e5_base_768`).
- **Strategy 2 (Spring AI Prefix / Table Injection):** Pass the active vector store table name dynamically via Spring AI configuration (`spring.ai.vectorstore.pgvector.table-name=vector_store_bge_m3`).
- **Document Chunk Re-sync:** Re-embedding runs regenerate chunks through the ingestion pipeline, assigning new embeddings without mutating raw document text.

---

## 7. Spring Data JPA Integration Plan

### 7.1 Entity Class Mapping

| Database Table | JPA Entity Class | Repository Interface | Mapping Notes |
| :--- | :--- | :--- | :--- |
| `agricultural_documents` | `AgriculturalDocument.java` | `AgriculturalDocumentRepository` | Updated to include `document_id`, provenance, and status fields. |
| `document_chunks` | `DocumentChunk.java` | `DocumentChunkRepository` | Many-to-One relationship with `AgriculturalDocument`. |
| `ingestion_jobs` | `IngestionJob.java` | `IngestionJobRepository` | Many-to-One relationship with `AgriculturalDocument`. |
| `questions` | `Question.java` | `QuestionRepository` | Preserves existing question logging; add optional user association. |
| `users` | `User.java` | `UserRepository` | Existing authentication entity. |
| `vector_store` | *None (Managed by Spring AI)* | *None (`PgVectorStore` Bean)* | Accessed through Spring AI's `VectorStore` interface; do **not** create a JPA entity for this table. |
| `evaluation_*` | *Optional Research Entities* | *EvaluationRepository* | Can be queried via JPA or directly via Python/Pandas for offline benchmarks. |

### 7.2 Entity Relationship Mapping Patterns
In `DocumentChunk.java`:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "document_id", nullable = false)
private AgriculturalDocument document;
```

In `AgriculturalDocument.java`:
```java
@OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
private List<DocumentChunk> chunks = new ArrayList<>();
```

---

## 8. Schema Migration Strategy

### Current Status vs Production Roadmap
- **Current Development Configuration:** `spring.jpa.hibernate.ddl-auto=update` in `application.properties`. This allows rapid prototyping on the Neon PostgreSQL development database.
- **Production Recommendation (Flyway Migration):**
  When advancing to production and shared staging environments, transition to Flyway:
  1. Set `spring.jpa.hibernate.ddl-auto=validate`.
  2. Add `flyway-core` and `flyway-database-postgresql` to `pom.xml`.
  3. Organize migrations in `src/main/resources/db/migration/`:
     - `V1__init_security_and_questions.sql`
     - `V2__create_document_and_chunk_tables.sql`
     - `V3__init_pgvector_extension_and_vector_store.sql`
     - `V4__create_evaluation_tables.sql`

---

## 9. Key Assumptions & Decisions Requiring Guide/Team Approval

1. **Embedding Dimension Decision:** The schema draft parameterizes vector dimension as `1024` for `BAAI/bge-m3` (the leading candidate), with alternative DDL provided for `768` (`multilingual-e5-base`). Final confirmation depends on the Week 2 offline benchmark results.
2. **Spring AI Native Vector Table:** We recommend using the default Spring AI table name `vector_store` rather than a custom JPA entity, ensuring maximum compatibility with Spring AI updates.
3. **Evaluation Table Hosting:** The evaluation schema can reside in the same Neon database under the public schema or in a dedicated `research` schema. The draft defines them cleanly in the public schema with `eval_` prefixes.
