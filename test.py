from orchestrator import run_system
import json
import os

FILE_NAME = "output.json"

print("=== ElektraSage Multi-Agent System ===")

while True:
    query = input("\nEnter query (or exit): ")

    if query.lower() == "exit":
        break

    result = run_system(query)

    if result:
        print("\n✅ Final Output:\n")
        print(json.dumps(result, indent=4))

        # Save JSON
        if os.path.exists(FILE_NAME):
            with open(FILE_NAME, "r") as f:
                try:
                    data = json.load(f)
                except:
                    data = []
        else:
            data = []

        data.append(result)

        with open(FILE_NAME, "w") as f:
            json.dump(data, f, indent=4)

        print("\n💾 Saved to output.json")