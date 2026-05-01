from langchain_community.llms import Ollama

# Load model
llm = Ollama(model="mistral")


# 🧑‍💼 Requirements Agent
def requirements_agent(query):
    prompt = f"""
You are a beginner-friendly Electronics Requirements Assistant.

User Query: {query}

Your job:
- Understand user intent (even if vague)
- Identify domain (analog or digital)
- Identify circuit type (block)

Return ONLY JSON:

{{
  "intent": "what user wants",
  "domain": "analog or digital",
  "block": "circuit type"
}}

Rules:
- Keep it simple
- Do NOT ask complex questions
- Output ONLY JSON
"""
    return llm.invoke(prompt)


# 🤖 Design Agent
def design_agent(req_json):
    prompt = f"""
You are a Design Engineer.

Input Requirements:
{req_json}

Generate a clean structured design.

Return ONLY JSON:

{{
  "type": "...",
  "block": "...",
  "subtype": "...",
  "description": "...",
  "application": "...",
  "ic_suggestion": "...",
  "components": [
    {{
      "name": "...",
      "type": "...",
      "value": "..."
    }}
  ],
  "specifications": {{
    "frequency": "...",
    "bits": "...",
    "range": "...",
    "voltage": "...",
    "other": "..."
  }}
}}
"""
    return llm.invoke(prompt)