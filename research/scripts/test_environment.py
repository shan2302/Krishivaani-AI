"""Smoke test for the KrishiVaani AI research environment.

Verifies that numpy, pandas and matplotlib are installed and working.
Run from the project root:  python research/scripts/test_environment.py
"""

import sys
import tempfile
from pathlib import Path

import matplotlib

matplotlib.use("Agg")  # non-interactive backend: no GUI required
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


def main() -> int:
    print(f"Python     : {sys.version.split()[0]}")
    print(f"numpy      : {np.__version__}")
    print(f"pandas     : {pd.__version__}")
    print(f"matplotlib : {matplotlib.__version__}")

    # Pandas: small DataFrame
    df = pd.DataFrame(
        {
            "language": ["English", "Hindi", "Kannada"],
            "score": [0.80, 0.70, 0.60],
        }
    )
    assert df.shape == (3, 2), "DataFrame has unexpected shape"
    assert abs(df["score"].mean() - 0.70) < 1e-9, "Pandas mean is wrong"
    print("pandas     : DataFrame OK")

    # NumPy: simple calculation
    arr = np.array([1, 2, 3, 4, 5], dtype=float)
    assert arr.mean() == 3.0 and np.isclose(arr.std(), np.sqrt(2.0))
    print("numpy      : calculation OK")

    # Matplotlib: simple plot saved to a temp file (nothing written to the repo)
    fig, ax = plt.subplots()
    ax.bar(df["language"], df["score"])
    ax.set_title("Environment test plot")
    with tempfile.TemporaryDirectory() as tmp:
        out = Path(tmp) / "test_plot.png"
        fig.savefig(out)
        assert out.exists() and out.stat().st_size > 0, "Plot was not saved"
    plt.close(fig)
    print("matplotlib : plot OK")

    print("\nAll checks passed: environment is working correctly.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
