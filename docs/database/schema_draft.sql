-- ============================================================================
-- KrishiVaani AI - PostgreSQL & PGVector Schema DDL (Draft Specification)
-- Target Database: Neon Serverless PostgreSQL 16+ with PGVector 0.7+
-- Note: Do NOT execute directly on the live database without migration review.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. EXTENSIONS
-- ----------------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector";

-- ----------------------------------------------------------------------------
-- 2. APPLICATION USER MANAGEMENT (Existing Baseline)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- ----------------------------------------------------------------------------
-- 3. AGRICULTURAL SOURCE DOCUMENTS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS agricultural_documents (
    document_id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    organization VARCHAR(255) NOT NULL,
    publication_year INTEGER NOT NULL,
    language VARCHAR(30) NOT NULL,
    source_url TEXT,
    original_filename VARCHAR(255) NOT NULL,
    document_type VARCHAR(100) NOT NULL DEFAULT 'Agro-Advisory',
    category VARCHAR(100),
    season VARCHAR(50),
    region VARCHAR(100),
    verification_status VARCHAR(50) NOT NULL DEFAULT 'pending_review',
    ingestion_status VARCHAR(50) NOT NULL DEFAULT 'registered',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_doc_language CHECK (language IN ('English', 'Hindi', 'Kannada', 'Multilingual')),
    CONSTRAINT chk_verification_status CHECK (verification_status IN ('pending_review', 'verified', 'rejected')),
    CONSTRAINT chk_ingestion_status CHECK (ingestion_status IN ('registered', 'processing', 'ingested', 'failed'))
);

CREATE INDEX IF NOT EXISTS idx_agrdoc_language ON agricultural_documents(language);
CREATE INDEX IF NOT EXISTS idx_agrdoc_category ON agricultural_documents(category);
CREATE INDEX IF NOT EXISTS idx_agrdoc_season ON agricultural_documents(season);
CREATE INDEX IF NOT EXISTS idx_agrdoc_verification ON agricultural_documents(verification_status);

-- ----------------------------------------------------------------------------
-- 4. DOCUMENT CHUNKS (Traceable Text Segments)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS document_chunks (
    chunk_id VARCHAR(64) PRIMARY KEY,
    document_id VARCHAR(64) NOT NULL,
    chunk_index INTEGER NOT NULL,
    chunk_text TEXT NOT NULL,
    language VARCHAR(30) NOT NULL,
    source_page_start INTEGER,
    source_page_end INTEGER,
    section_heading VARCHAR(255),
    token_count INTEGER,
    chunking_strategy VARCHAR(100) NOT NULL DEFAULT 'recursive_character_700_100',
    content_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chunks_document FOREIGN KEY (document_id) 
        REFERENCES agricultural_documents(document_id) ON DELETE CASCADE,
    CONSTRAINT uq_document_chunk_index UNIQUE (document_id, chunk_index)
);

CREATE INDEX IF NOT EXISTS idx_chunks_document_id ON document_chunks(document_id);
CREATE INDEX IF NOT EXISTS idx_chunks_language ON document_chunks(language);
CREATE INDEX IF NOT EXISTS idx_chunks_pages ON document_chunks(document_id, source_page_start);

-- ----------------------------------------------------------------------------
-- 5. ASYNCHRONOUS INGESTION JOBS
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingestion_jobs (
    job_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id VARCHAR(64) NOT NULL,
    job_status VARCHAR(50) NOT NULL DEFAULT 'QUEUED',
    pipeline_version VARCHAR(50) NOT NULL DEFAULT '1.0.0',
    total_chunks_created INTEGER DEFAULT 0,
    error_message TEXT,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_jobs_document FOREIGN KEY (document_id)
        REFERENCES agricultural_documents(document_id) ON DELETE CASCADE,
    CONSTRAINT chk_job_status CHECK (job_status IN ('QUEUED', 'EXTRACTING', 'CHUNKING', 'EMBEDDING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_ingestion_jobs_doc ON ingestion_jobs(document_id);
CREATE INDEX IF NOT EXISTS idx_ingestion_jobs_status ON ingestion_jobs(job_status);

-- ----------------------------------------------------------------------------
-- 6. SPRING AI DEFAULT PGVECTOR STORE SCHEMA
-- Compatible with org.springframework.ai.vectorstore.pgvector.PgVectorStore
-- Note: Dimension set to 1024 for BAAI/bge-m3; change to 768 for multilingual-e5-base
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding VECTOR(1024) NOT NULL
);

-- HNSW Index for fast approximate cosine similarity search
CREATE INDEX IF NOT EXISTS idx_vector_store_hnsw 
ON vector_store 
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

-- GIN Index for rapid metadata filtering (e.g. by language, topic, document_id)
CREATE INDEX IF NOT EXISTS idx_vector_store_metadata 
ON vector_store 
USING gin (metadata jsonb_path_ops);

-- ----------------------------------------------------------------------------
-- 7. USER INQUIRIES & GENERATION AUDIT LOG (Existing Baseline Enhanced)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    content TEXT NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    audio_input_url VARCHAR(500),
    answer TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_questions_language ON questions(language);
CREATE INDEX IF NOT EXISTS idx_questions_created_at ON questions(created_at);

-- ----------------------------------------------------------------------------
-- 8. RESEARCH EVALUATION BENCHMARK SCHEMA
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS evaluation_questions (
    question_id VARCHAR(32) PRIMARY KEY,
    question_text TEXT NOT NULL,
    language VARCHAR(10) NOT NULL,
    topic VARCHAR(100) NOT NULL,
    crop VARCHAR(100),
    region VARCHAR(100),
    season VARCHAR(50),
    expected_answer TEXT,
    source_doc_id VARCHAR(64),
    source_page VARCHAR(50),
    answerable_from_source BOOLEAN NOT NULL DEFAULT TRUE,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'medium',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_eval_difficulty CHECK (difficulty IN ('easy', 'medium', 'hard'))
);

CREATE INDEX IF NOT EXISTS idx_eval_q_topic ON evaluation_questions(topic);
CREATE INDEX IF NOT EXISTS idx_eval_q_lang ON evaluation_questions(language);

CREATE TABLE IF NOT EXISTS evaluation_runs (
    run_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    run_name VARCHAR(100) NOT NULL,
    system_mode VARCHAR(50) NOT NULL,
    embedding_model VARCHAR(100) NOT NULL,
    llm_model VARCHAR(100) NOT NULL,
    top_k INTEGER NOT NULL DEFAULT 5,
    temperature NUMERIC(3, 2) NOT NULL DEFAULT 0.30,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_eval_system_mode CHECK (system_mode IN ('LLM_ONLY', 'MONOLINGUAL_RAG', 'CROSS_LINGUAL_RAG'))
);

CREATE TABLE IF NOT EXISTS evaluation_results (
    result_id BIGSERIAL PRIMARY KEY,
    run_id UUID NOT NULL,
    question_id VARCHAR(32) NOT NULL,
    retrieved_chunk_ids TEXT, -- Comma-separated or JSON array of retrieved chunk IDs
    is_hit_at_k BOOLEAN NOT NULL,
    recall_at_k NUMERIC(5, 4) NOT NULL,
    precision_at_k NUMERIC(5, 4) NOT NULL,
    reciprocal_rank NUMERIC(5, 4) NOT NULL,
    generation_answer TEXT,
    retrieval_latency_ms INTEGER,
    total_latency_ms INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eval_results_run FOREIGN KEY (run_id) 
        REFERENCES evaluation_runs(run_id) ON DELETE CASCADE,
    CONSTRAINT fk_eval_results_question FOREIGN KEY (question_id) 
        REFERENCES evaluation_questions(question_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_eval_res_run ON evaluation_results(run_id);
CREATE INDEX IF NOT EXISTS idx_eval_res_question ON evaluation_results(question_id);
