"""Validation script for the Raw English Agricultural Question Dataset (CSV).

Verifies schema compliance, unique IDs, data types, duplicate questions,
allowed categorical values, and strict source grounding.
"""

import sys
import pandas as pd
from pathlib import Path

REQUIRED_COLUMNS = [
    "question_id",
    "question_en",
    "topic",
    "crop",
    "region",
    "season",
    "expected_answer",
    "source_doc_id",
    "source_title",
    "source_page",
    "answerable_from_source",
    "difficulty",
    "notes"
]

ALLOWED_TOPICS = {
    "Crop selection and suitable growing conditions",
    "Sowing and planting practices",
    "Seed treatment and seed selection",
    "Fertilizer and nutrient management",
    "Irrigation and water management",
    "Pest and disease management",
    "Weed management",
    "Weather-related agricultural advice",
    "Harvesting and post-harvest practices",
    "Crop-specific seasonal advisories"
}

ALLOWED_DIFFICULTY = {"easy", "medium", "hard"}
ALLOWED_ANSWERABLE = {"true", "false", True, False}


def validate_dataset(csv_path: Path) -> int:
    print(f"Loading dataset from: {csv_path}")
    if not csv_path.exists():
        print(f"ERROR: Dataset file not found at {csv_path}")
        return 1

    df = pd.read_csv(csv_path, dtype=str).fillna("")

    errors = []
    warnings = []

    # 1. Column verification
    cols = list(df.columns)
    if cols != REQUIRED_COLUMNS:
        errors.append(f"Column schema mismatch! Expected:\n  {REQUIRED_COLUMNS}\nFound:\n  {cols}")
    else:
        print("PASS: Schema and column order matched.")

    # 2. Check total rows
    total_rows = len(df)
    print(f"INFO: Total records in dataset: {total_rows}")
    if total_rows < 1:
        errors.append("Dataset is empty!")

    # 3. Question IDs uniqueness & non-empty
    q_ids = df["question_id"].tolist()
    if any(not qid.strip() for qid in q_ids):
        errors.append("Found empty question_id values.")
    if len(q_ids) != len(set(q_ids)):
        duplicates = [x for x in q_ids if q_ids.count(x) > 1]
        errors.append(f"Duplicate question_id values found: {set(duplicates)}")
    else:
        print("PASS: All question_id values are non-empty and unique.")

    # 4. Question non-empty and duplicate detection
    questions = df["question_en"].str.strip().tolist()
    if any(not q for q in questions):
        errors.append("Found empty question_en values.")
    
    # Check exact duplicates
    seen_q = set()
    dup_q = set()
    for q in questions:
        q_norm = q.lower()
        if q_norm in seen_q:
            dup_q.add(q)
        seen_q.add(q_norm)
    if dup_q:
        errors.append(f"Exact duplicate questions found: {dup_q}")
    else:
        print("PASS: No duplicate questions detected.")

    # 5. Categorical validation
    for idx, row in df.iterrows():
        row_id = row["question_id"]
        
        # Difficulty
        diff = row["difficulty"].lower()
        if diff not in ALLOWED_DIFFICULTY:
            errors.append(f"Row {row_id}: Invalid difficulty '{row['difficulty']}'. Must be in {ALLOWED_DIFFICULTY}")
            
        # Topic
        topic = row["topic"]
        if topic not in ALLOWED_TOPICS:
            errors.append(f"Row {row_id}: Unknown topic '{topic}'. Must be in ALLOWED_TOPICS.")

        # Answerable
        ans_val = str(row["answerable_from_source"]).lower()
        if ans_val not in {"true", "false"}:
            errors.append(f"Row {row_id}: Invalid answerable_from_source '{row['answerable_from_source']}'. Must be true/false.")
            
        # Grounding checks for answerable rows
        if ans_val == "true":
            if not row["expected_answer"].strip():
                errors.append(f"Row {row_id}: Marked answerable_from_source=true but expected_answer is empty.")
            if not row["source_doc_id"].strip():
                errors.append(f"Row {row_id}: Marked answerable_from_source=true but source_doc_id is empty.")
            if not row["source_title"].strip():
                errors.append(f"Row {row_id}: Marked answerable_from_source=true but source_title is empty.")
            if not row["source_page"].strip():
                errors.append(f"Row {row_id}: Marked answerable_from_source=true but source_page is missing.")
            else:
                # check page format
                p = row["source_page"].strip()
                if not (p.isdigit() or "-" in p):
                    warnings.append(f"Row {row_id}: source_page '{p}' is not standard integer or range.")
        else:
            # Unanswerable checks
            if row["expected_answer"].strip():
                warnings.append(f"Row {row_id}: Marked answerable_from_source=false but contains expected_answer.")

    # Summary Statistics
    print("\n--- DATASET SUMMARY STATISTICS ---")
    print("Answerability counts:")
    print(df["answerable_from_source"].value_counts().to_string())
    print("\nTopic distribution:")
    print(df["topic"].value_counts().to_string())
    print("\nDifficulty distribution:")
    print(df["difficulty"].value_counts().to_string())

    if warnings:
        print(f"\nWARNINGS ({len(warnings)}):")
        for w in warnings[:10]:
            print(f" - {w}")

    if errors:
        print(f"\nVALIDATION FAILED with {len(errors)} error(s):")
        for e in errors:
            print(f" - {e}")
        return 1

    print("\nPASS: All dataset validation checks passed successfully!")
    return 0


if __name__ == "__main__":
    csv_file = Path(__file__).resolve().parent.parent.parent / "docs" / "research" / "datasets" / "raw_english_agricultural_questions.csv"
    sys.exit(validate_dataset(csv_file))
