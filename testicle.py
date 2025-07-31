import subprocess
import os
import sys
import time
from pathlib import Path
import threading

def loading_screen(stop_event, label):
    spinner = ['|', '/', '-', '\\']
    i = 0
    start = time.perf_counter()
    while not stop_event.is_set():
        elapsed = time.perf_counter() - start
        print(f"\r{label} {spinner[i % 4]} Elapsed: {elapsed:.6f}s", end='', flush=True)
        i += 1
        time.sleep(0.1)
    print()

def find_jar(libs_path: Path):
    if not libs_path.exists() or not libs_path.is_dir():
        return None
    jars = sorted(libs_path.glob("*.jar"), key=os.path.getmtime, reverse=True)
    return jars[0] if jars else None

gradlew = "gradlew.bat" if os.name == "nt" else "./gradlew"
project_root = Path(__file__).resolve().parent
libs_dir = project_root / "app" / "build" / "libs"

os.chdir(project_root)

stop_event = threading.Event()
thread = threading.Thread(target=loading_screen, args=(stop_event, "Building..."))
start_build = time.perf_counter()
thread.start()
try:
    subprocess.run([gradlew, "clean", "shadowJar"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
except subprocess.CalledProcessError:
    stop_event.set()
    thread.join()
    print("Failed to build Fat Jar")
    sys.exit(1)
stop_event.set()
thread.join()
build_duration = time.perf_counter() - start_build

jar = find_jar(libs_dir)
if not jar:
    print("Failed to find Fat Jar")
    sys.exit(1)

jar_size_kb = jar.stat().st_size / 1024
os.chdir(libs_dir)

try:
    subprocess.run(["java", "-jar", str(jar)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
    print(f"Finished successfully; size of Fat Jar {jar_size_kb:.6f} KB")
except subprocess.CalledProcessError:
    print("\nTest failed during execution of Fat Jar")
except FileNotFoundError:
    print("\nJava runtime not found")
finally:
    os.chdir(project_root)
