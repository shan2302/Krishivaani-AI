# Database Design Decisions & Architecture Trade-Offs

**Project:** KrishiVaani AI  
**Document Status:** Approved Architecture & Decision Log  
**File Path:** `docs/database/schema_decisions.md`  

---

## 1. Decision 1: Relational Chunk Metadata vs Native Spring AI Vector Table

### Problem Statement
Should we store vector embeddings inside a custom application JPA entity table (e.g., `document_chunks.embedding`), or maintain a clean separation between the relational `document_chunks` table and Spring AI's native `vector_store` table?

### Options Considered
- **Option A (Custom Monolithic Vector Entity):** Map vectors directly in JPA via custom Hibernate UserTypes or extensions (`pgvector-java`), querying them with native SQL queries.
- **Option B (Separate Relational & Vector Stores - Selected):** Keep `document_chunks` as a pure relational entity in Spring Data JPA and utilize Spring AI's official `vector_store` table managed by `PgVectorStore`. The two layers link logically through `vector_store.metadata->>'chunk_id'`.

### Rationale & Trade-Offs
- **Pros of Option B:**
  1. Full compatibility with Spring AI 2.0's `VectorStore` API, `SearchRequest`, similarity thresholds, and automated query vectorization.
  2. Prevents JVM heap bloating: Vector float arrays (1024 floats per chunk) do not need to be loaded into Hibernate's first-level cache when managing document workflows.
  3. Seamless model upgrades: Vector tables can be re-indexed or swapped without touching the core document management entities.
- **Cons:** Requires a two-step join or JSONB metadata lookup when hydrating full document metadata during retrieval. However, Spring AI already supports returning metadata payloads directly from the vector store search.

---

## 2. Decision 2: Vector Dimension Parameterization (1024 vs 768)

### Problem Statement
PostgreSQL `vector` types require fixed dimension specifications at table creation time (`vector(1024)` vs `vector(768)`). The final choice of embedding model is pending offline evaluation between `BAAI/bge-m3` (1024-d) and `multilingual-e5-base` (768-d).

### Rationale & Mitigation
- The DDL draft sets the default to `1024` (accommodating our leading candidate `bge-m3`).
- If benchmarking selects `multilingual-e5-base`, the DDL only requires altering the dimension definition in `vector_store`.
- For multi-model evaluation experiments, the project can maintain dedicated vector tables (`vector_store_bgem3_1024` and `vector_store_e5base_768`) and configure the active table dynamically via `spring.ai.vectorstore.pgvector.table-name`.

---

## 3. Decision 3: Natural Primary Key (`document_id`) vs Synthetic Identity

### Problem Statement
Should `agricultural_documents` use a synthetic sequence (`id BIGSERIAL`) or the existing stable string identifier (`document_id VARCHAR(64)`)?

### Decision
- **Selected:** Natural String Primary Key (`document_id VARCHAR(64)`).
- **Rationale:** All verified agricultural source assets in [`metadata/documents.json`](file:///c:/Krishivaani-AI/Krishivaani-AI/docs/agricultural_sources/metadata/documents.json), [`metadata/verification_log.csv`](file:///c:/Krishivaani-AI/Krishivaani-AI/docs/agricultural_sources/metadata/verification_log.csv), and [`datasets/raw_english_agricultural_questions.csv`](file:///c:/Krishivaani-AI/Krishivaani-AI/docs/research/datasets/raw_english_agricultural_questions.csv) explicitly reference stable keys such as `ICAR_KHARIF_2025_EN`. Using this natural key simplifies cross-referencing between research datasets, PDF assets, and database rows.

---

## 4. Decision 4: Index Selection (HNSW vs IVFFlat)

### Problem Statement
Which PGVector index type should be provisioned for semantic similarity search?

### Comparison
| Feature | HNSW (Selected) | IVFFlat |
| :--- | :--- | :--- |
| **Search Speed** | Sub-millisecond ($O(\log N)$) | Slower, requires clustering |
| **Recall / Accuracy**| High recall (>95%) | Sensitive to list count & training |
| **Build Time & RAM** | Higher build time and memory | Lower memory footprint |
| **Build Trigger** | Can build immediately on empty table | Requires substantial data to cluster |

### Decision
- **Selected:** **HNSW** (`vector_cosine_ops`, `m = 16`, `ef_construction = 64`).
- **Rationale:** For agricultural farmer question answering, rapid voice and text response times are paramount. Furthermore, HNSW does not require training clusters, allowing smooth ingestion as new ICAR bulletins are uploaded incrementally.

---

## 5. Decision 5: Dedicated Evaluation Schema vs External File-Only Tracking

### Problem Statement
Should research benchmarks and evaluation runs reside in database tables or strictly in CSV/JSON files?

### Decision
- **Hybrid Approach:**
  - Canonical raw benchmarks remain versioned in git as CSV files (`docs/research/datasets/raw_english_agricultural_questions.csv`).
  - Optional evaluation tables (`evaluation_questions`, `evaluation_runs`, `evaluation_results`) are formally defined in the PostgreSQL schema.
  - This allows automated Python or Java test harnesses to persist run configurations, retrieval latency, and query-level hit rates directly into Neon PostgreSQL for reporting and dashboard visualization.
