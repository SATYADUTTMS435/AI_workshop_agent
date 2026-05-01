from agent import requirements_agent, design_agent
import json


# 🧹 Clean LLM JSON
def clean_json(output):
    try:
        return json.loads(output)
    except:
        output = output.strip()

        if "```" in output:
            output = output.split("```")[1].split("```")[0]

        return json.loads(output)


# 🔗 Smart Datasheet Link Generator
def add_datasheet_link(design_json):

    ic_raw = str(design_json.get("ic_suggestion", "")).lower()

    # Normalize IC name
    ic = ic_raw.replace("ic", "").replace(" ", "").replace("-", "")

    links = {
        "74hc193": "https://www.ti.com/lit/ds/symlink/sn74hc193.pdf",
        "7490": "https://www.ti.com/lit/ds/symlink/sn7490a.pdf",
        "ne555": "https://www.ti.com/lit/ds/symlink/ne555.pdf",
        "555": "https://www.ti.com/lit/ds/symlink/ne555.pdf",
        "741": "https://www.ti.com/lit/ds/symlink/ua741.pdf"
    }

    # 🔍 Smart match
    for key in links:
        if key in ic:
            design_json["datasheet_link"] = links[key]
            return design_json

    # 🔁 Always fallback
    if ic_raw.strip() != "":
        search_link = f"https://www.google.com/search?q={ic_raw}+datasheet"
        design_json["datasheet_link"] = search_link

    return design_json


# 🌟 Default + Human-in-loop (Beginner + Expert)
def apply_defaults(req_json):

    domain = req_json.get("domain", "").lower()

    defaults = {
        "voltage": "5V",
        "frequency": "1kHz",
        "resistor": "1kΩ",
        "capacitor": "10µF",
        "bits": "4",
        "range": "0-15",
        "ic": "74HCxx series"
    }

    # Context-based selection
    if "analog" in domain:
        selected = {
            "voltage": defaults["voltage"],
            "frequency": defaults["frequency"],
            "resistor": defaults["resistor"],
            "capacitor": defaults["capacitor"]
        }

    elif "digital" in domain:
        selected = {
            "voltage": defaults["voltage"],
            "bits": defaults["bits"],
            "range": defaults["range"],
            "ic": defaults["ic"]
        }

    else:
        selected = defaults

    # 🧑‍💻 Show defaults clearly
    print("\n🔧 Proposed Default Design Parameters:\n")

    for k, v in selected.items():
        print(f"   {k.upper()} = {v}")

    print("\n👉 Press Enter to accept ALL defaults")
    print("👉 OR type parameter name to modify")

    choice = input("\nYour choice: ").strip().lower()

    if choice == "":
        print("\n✔ Using all default values")
        req_json["user_parameters"] = selected
        return req_json

    # 🔁 Expert override
    while choice in selected:
        new_val = input(f"Enter new value for {choice}: ")
        selected[choice] = new_val

        choice = input("Modify another? (or press Enter to finish): ").strip().lower()
        if choice == "":
            break

    req_json["user_parameters"] = selected
    return req_json


# 🎯 Main Orchestrator
def run_system(user_query):

    # Step 1: Requirements Agent
    req_output = requirements_agent(user_query)

    try:
        req_json = clean_json(req_output)
    except:
        print("❌ Requirements parsing failed")
        return None

    # Step 2: Human-in-loop defaults
    req_json = apply_defaults(req_json)

    # Step 3: Design Agent
    design_output = design_agent(req_json)

    try:
        design_json = clean_json(design_output)
    except:
        print("❌ Design parsing failed")
        return None

    # Step 4: Add datasheet link
    design_json = add_datasheet_link(design_json)

    # Final Output
    return {
        "requirements": req_json,
        "design": design_json
    }