# KrishiVaani AI – Research Environment

## Purpose
An isolated Python environment for the research and evaluation work of KrishiVaani AI
(multilingual agricultural RAG: English, Hindi, Kannada). It will be used to compare
LLM-only vs RAG vs Cross-Lingual RAG using Precision, Recall, WER, hallucination
percentage and other statistical metrics, and to visualise the results.

It is fully separate from the Spring Boot backend and the frontend. No Docker is used.

## Python version
Developed with **Python 3.13.14**. Python 3.11+ should work.

## Installed libraries
Direct dependencies (exact versions, plus all transitive dependencies, are pinned in `requirements.txt`):

| Library    | Version |
|------------|---------|
| numpy      | 2.5.3   |
| pandas     | 3.0.6   |
| matplotlib | 3.11.2  |
| jupyter    | 1.1.1   |

## Setup (run from the project root)

Create the virtual environment:
```powershell
python -m venv .venv-research
```

Activate it (Windows PowerShell):
```powershell
.\.venv-research\Scripts\Activate.ps1
```
If script execution is blocked, run once per session:
`Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass`

Install requirements:
```powershell
python -m pip install -r research/requirements.txt
```

Run the test script:
```powershell
python research/scripts/test_environment.py
```

Start Jupyter (optional):
```powershell
jupyter notebook research/notebooks
```

Deactivate with `deactivate`.

## Directory layout

- `notebooks/` – Jupyter notebooks for exploratory analysis and result visualisation.
- `scripts/` – Reusable Python scripts (evaluation metrics, data processing, environment test).
- `results/` – Outputs of real experiments (tables, figures). Kept empty until experiments are run.
- `requirements.txt` – Pinned dependencies for reproducibility.
