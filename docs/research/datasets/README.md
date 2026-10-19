# Raw English Agricultural Question Dataset

**File:** `docs/research/datasets/raw_english_agricultural_questions.csv`  
**Dataset Identifier:** `KV-DATASET-EN-RAW-001`  
**Project:** KrishiVaani AI (Multilingual Agricultural RAG)  
**Total Records:** 50  
**Source-Grounded Answerable Questions:** 48 (`answerable_from_source = true`)  
**Unanswerable / Out-of-Scope Control Questions:** 2 (`answerable_from_source = false`)  
**Date of Creation:** October 2026  

---

## 1. Dataset Purpose

KrishiVaani AI is an 8-week research and engineering project comparing:
1. LLM-only baselines
2. Monolingual RAG (language-matched retrieval)
3. Cross-lingual RAG (queries in Hindi or Kannada retrieving English canonical ICAR documents)

This dataset provides the initial **ground-truth evaluation set** of realistic English agricultural questions. It enables reproducible benchmarking of retrieval accuracy (Recall@5, Precision@5, MRR@5), generation factual correctness, and hallucination reduction against verified Indian government agricultural documents.

---

## 2. Source Documents Used

All answerable questions are directly grounded in the official publication:
- **Title:** *ICAR Kharif Agro-Advisories for Farmers 2025*
- **Issuing Organization:** Indian Council of Agricultural Research (ICAR), Department of Agricultural Research and Education (DARE), Ministry of Agriculture and Farmers Welfare, Government of India.
- **Repository Metadata ID:** `ICAR_KHARIF_2025_EN` (defined in [`docs/agricultural_sources/metadata/documents.json`](file:///c:/Krishivaani-AI/Krishivaani-AI/docs/agricultural_sources/metadata/documents.json))
- **Local Source Path:** `docs/agricultural_sources/raw/ICAR/English/ICAR-En-Kharif-Agro-Advisories-for-Farmers-2025.pdf`
- **Verification Status:** Verified against published digital copy (`verification_log.csv`).

---

## 3. Dataset Schema & Column Definitions

The CSV file contains exactly 13 columns in this specified order:

| Column # | Column Name | Type | Description |
| :--- | :--- | :--- | :--- |
| 1 | `question_id` | String | Unique, stable identifier (`ENQ001` to `ENQ050`). |
| 2 | `question_en` | String | Natural English agricultural question formulated as a realistic farmer/extension query. |
| 3 | `topic` | String | Categorical domain topic (1 of 10 standard agricultural categories). |
| 4 | `crop` | String | Target crop name (e.g., `Paddy`, `Maize`, `Groundnut`), or blank for general advisories. |
| 5 | `region` | String | Specific state or agro-climatic zone (e.g., `Andhra Pradesh`, `Assam`, `Himachal Pradesh`). |
| 6 | `season` | String | Agricultural season (e.g., `Kharif`). |
| 7 | `expected_answer` | String | Concise, factually verified reference answer grounded in the cited source page. |
| 8 | `source_doc_id` | String | Identifier matching `metadata/documents.json` (`ICAR_KHARIF_2025_EN`). |
| 9 | `source_title` | String | Full title of the supporting source document. |
| 10 | `source_page` | String | Exact PDF page number containing the supporting factual claim. |
| 11 | `answerable_from_source` | Boolean | `true` if sufficient factual support exists in the source; `false` otherwise. |
| 12 | `difficulty` | String | Cognitive and retrieval complexity: `easy` (factual lookup), `medium` (procedural/multi-condition), `hard` (complex dosage/multi-part). |
| 13 | `notes` | String | Extraction notes, caveats, or reasons why an unanswerable record lacks source support. |

---

## 4. How Questions and Reference Answers Were Created

1. **Direct PDF Stream Extraction & CMap Decoding:**
   - The primary PDF (`ICAR-En-Kharif-Agro-Advisories-for-Farmers-2025.pdf`, 310 pages, 17.7 MB) utilizes Type0 fonts with custom glyph identifiers.
   - Text streams and ToUnicode CMaps were programmatically parsed to reconstruct clean, human-readable text corresponding to exact page numbers.
2. **Farmer-Centric Question Formulation:**
   - Realistic questions were authored reflecting inquiries commonly encountered at Krishi Vigyan Kendras (KVKs), farmer call centers, and agricultural extension offices.
   - Questions cover critical decision points: sowing windows, seed treatment dosages, herbicide timing, pest economic threshold levels (ETL), and contingent weather planning.
3. **Strict Source Grounding:**
   - In accordance with research integrity standards, **zero fertilizer rates or pesticide dosages were invented or assumed**.
   - Chemical formulations (e.g., *Azadirachtin 1500 ppm at 3 ml/L*, *Atrazine 50% WP at 2 kg/ha*) and botanical species names faithfully reproduce the ICAR advisory.
4. **Unanswerable Control Records:**
   - Questions `ENQ049` and `ENQ050` were intentionally included as negative controls (`answerable_from_source = false`).
   - They represent realistic farmer inquiries (polyhouse strawberry subsidies in Karnataka, minimum export prices for basmati rice) that cannot be answered from the agronomic advisory, allowing future RAG pipelines to evaluate hallucination prevention and abstention behavior.

---

## 5. Topic & Difficulty Distribution

### Distribution by Topic
- **Pest and disease management:** 10 questions
- **Sowing and planting practices:** 8 questions
- **Fertilizer and nutrient management:** 7 questions
- **Crop selection and suitable growing conditions:** 6 questions
- **Seed treatment and seed selection:** 5 questions
- **Weed management:** 4 questions
- **Weather-related agricultural advice:** 3 questions
- **Harvesting and post-harvest practices:** 3 questions
- **Crop-specific seasonal advisories:** 3 questions
- **Irrigation and water management:** 1 question

### Distribution by Difficulty
- **Medium:** 30 questions (60%)
- **Easy:** 12 questions (24%)
- **Hard:** 8 questions (16%)

---

## 6. How to Load and Inspect Using Python & Pandas

```python
import pandas as pd
from pathlib import Path

# Load dataset
dataset_path = Path("docs/research/datasets/raw_english_agricultural_questions.csv")
df = pd.read_csv(dataset_path)

# Verify shape
print(f"Shape: {df.shape}")  # (50, 13)

# Filter answerable questions
answerable_df = df[df["answerable_from_source"] == True]
print(f"Answerable records: {len(answerable_df)}")

# Group by topic
print(df.groupby(["topic", "difficulty"]).size())
```

---

## 7. How to Validate the Dataset

Run the automated validation script from the repository root:

```bash
python research/scripts/validate_english_dataset.py
```

The validator verifies:
- Exactly 13 required columns in exact order
- 100% unique, non-empty `question_id` values
- Absence of duplicate or near-duplicate questions
- Valid allowed topics and difficulties
- Completeness of `expected_answer`, `source_doc_id`, `source_title`, and `source_page` for all answerable rows

---

## 8. Role in Later RAG Evaluation

In Week 2 and subsequent phases of KrishiVaani AI:
1. **Embedding Model Comparison:** This dataset will be used with candidate embedding models (`bge-m3`, `multilingual-e5-base`, `jina-embeddings-v3`) to evaluate vector retrieval metrics (Recall@5, MRR@5) in PGVector.
2. **Cross-Lingual Benchmarking:** Corresponding Hindi and Kannada translations of these questions will evaluate whether regional language farmer queries can retrieve the same canonical English ICAR chunks.
3. **Hallucination Detection:** The negative control records (`ENQ049`, `ENQ050`) will test whether the generator accurately refrains from answering out-of-corpus queries.
