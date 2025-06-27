import sys
import matplotlib
matplotlib.use('TkAgg')
import pandas as pd
import matplotlib.pyplot as plt
from tkinter import Tk, Text, BOTH, END, Frame, LEFT, RIGHT, Y, Scrollbar, VERTICAL
import numpy as np
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

def plot_and_show_stats(data):
    root = Tk()
    root.title("Performance Plot and Statistics")
    root.geometry("900x500")

    plot_frame = Frame(root)
    plot_frame.pack(side=LEFT, fill=BOTH, expand=True)

    stats_frame = Frame(root)
    stats_frame.pack(side=RIGHT, fill=Y)

    fig, ax = plt.subplots(figsize=(6,4))
    ax.plot(data['index'], data['elapsed_time'], marker='o', linestyle='-', color='teal')
    ax.set_title("Performance Timing")
    ax.set_xlabel("Sample Index")
    ax.set_ylabel("Elapsed Time (ns)")
    ax.grid(True)
    fig.tight_layout()

    canvas = FigureCanvasTkAgg(fig, master=plot_frame)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=BOTH, expand=True)
    plt.close(fig)  # Important!

    text = Text(stats_frame, width=40)
    scrollbar = Scrollbar(stats_frame, orient=VERTICAL, command=text.yview)
    text.config(yscrollcommand=scrollbar.set)
    scrollbar.pack(side=RIGHT, fill=Y)
    text.pack(side=LEFT, fill=BOTH, expand=True)

    elapsed = data['elapsed_time']
    stat_lines = [
        f"Total Samples: {len(elapsed)}",
        f"Minimum Time: {elapsed.min():,.0f} ns",
        f"Maximum Time: {elapsed.max():,.0f} ns",
        f"Mean Time:    {elapsed.mean():,.2f} ns",
        f"Median Time:  {elapsed.median():,.2f} ns",
        f"Std Dev:      {elapsed.std():,.2f} ns",
        f"95th Percentile: {np.percentile(elapsed, 95):,.2f} ns",
    ]
    text.insert(END, "\n".join(stat_lines))

    root.mainloop()

def plot_csv(file_path):
    data = pd.read_csv(file_path, skipinitialspace=True, encoding='utf-8-sig')
    data.columns = data.columns.str.strip()

    if 'index' not in data.columns or 'elapsed_time' not in data.columns:
        raise ValueError("CSV must have 'index' and 'elapsed_time' columns")

    plot_and_show_stats(data)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python your_script.py path/to/posteriori.csv")
        sys.exit(1)

    file_path = sys.argv[1]
    plot_csv(file_path)
