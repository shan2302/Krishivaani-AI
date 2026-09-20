# Open-Source LLM Selection via Ollama

## Project: KrishiVaani-AI

**Task:** Research and select open-source LLM via Ollama  
**Epic:** Research & Planning  
**Week:** Week 1  
**Story Points:** 5  
**Assignee:** Vedh Nayak K  
**Repository:** KrishiVaani-AI  
**Selected LLM:** Qwen3 8B  
**Ollama Model Identifier:** `qwen3:8b`  

---

# 1. Objective

The objective of this task is to research, evaluate, and select a
suitable open-weight Large Language Model (LLM) that can be deployed
locally through Ollama and used as the language-generation component
of the KrishiVaani-AI system.

KrishiVaani-AI is designed as a multilingual agricultural AI system
that aims to provide reliable and understandable agricultural
information to Indian farmers.

The project focuses primarily on:

- English
- Hindi
- Kannada

The selected LLM must therefore be capable of handling multilingual
queries while being suitable for integration with the project's
Retrieval-Augmented Generation (RAG) architecture.

After discussion with the project team and consideration of the
system requirements, Qwen3 8B was selected as the LLM to be used for
the KrishiVaani-AI system.

The model will be deployed locally using Ollama and will serve
primarily as the response-generation component of the system.

---

# 2. Project Background

## 2.1 KrishiVaani-AI

KrishiVaani-AI is a multilingual agricultural question-answering
system intended to help users obtain reliable agricultural
information in a form that is easier to understand.

The system is being designed around a Retrieval-Augmented Generation
(RAG) architecture rather than relying exclusively on the knowledge
stored inside an LLM.

The planned system supports:

- Text-based agricultural queries
- Multilingual interaction
- English, Hindi, and Kannada
- Retrieval of information from a curated agricultural knowledge base
- Grounded response generation
- Evaluation of answer accuracy and relevance
- Reduction of unsupported or hallucinated responses

The project also considers voice interaction using Whisper for
speech-to-text processing.

---

# 3. KrishiVaani-AI Requirements

The selected LLM should satisfy the following project requirements.

## 3.1 Local Deployment

The model should be capable of running locally rather than requiring
every inference request to be sent to an external commercial API.

Local deployment is desirable for:

- Development
- Testing
- Privacy
- Reduced dependency on external APIs
- Reproducibility
- Potential offline or low-connectivity use cases

Ollama is being used as the local model runtime.

---

## 3.2 Multilingual Support

The system is designed primarily for:

1. English
2. Hindi
3. Kannada

The selected LLM therefore needs to support multilingual
instruction following and generation.

Qwen3 documentation states that the Qwen3 family supports 119
languages and dialects. The documented language list includes Hindi
and Kannada in addition to English.

---

## 3.3 Agricultural Question Answering

The LLM must be capable of generating understandable responses to
agricultural questions.

Example topics include:

- Crop cultivation
- Nutrient deficiencies
- Crop symptoms
- Soil-related questions
- Irrigation
- Pest and disease-related information
- General agricultural practices

The LLM itself is not treated as the authoritative agricultural
knowledge base.

Instead, relevant agricultural information will be retrieved from
the project's curated knowledge base and supplied to the LLM through
the RAG pipeline.

---

## 3.4 RAG Compatibility

The selected LLM must work effectively as the generation component
of a Retrieval-Augmented Generation pipeline.

The planned architecture is:

User Query
    ↓
Language / Query Processing
    ↓
Embedding Generation
    ↓
PGVector Retrieval
    ↓
Relevant Agricultural Documents
    ↓
Retrieved Context
    ↓
Qwen3 8B
    ↓
Grounded Response
    ↓
User

The model should therefore be capable of using retrieved context
provided in the prompt and generating an answer based on that context.

---

## 3.5 Grounded Responses

One of the important objectives of KrishiVaani-AI is to reduce
unsupported responses and hallucinations.

The system should encourage the LLM to:

- Use retrieved agricultural information
- Avoid inventing unsupported facts
- Clearly communicate uncertainty where appropriate
- Follow the provided context
- Produce understandable answers

The RAG pipeline and evaluation methodology will be used to assess
grounding rather than assuming that the LLM is automatically
hallucination-free.

---

## 3.6 Reasonable Resource Requirements

The model should be practical to run locally on the development
hardware available to the team.

The selected Qwen3 8B Ollama model is approximately 5.2 GB in size
and contains approximately 8.19 billion parameters.

Actual runtime memory and inference performance will depend on the
hardware, Ollama configuration, context length, and other runtime
conditions.

---

## 3.7 Licensing

The selected model should have a license that is suitable for the
project's intended research and development use.

The Qwen3 8B Ollama model is listed under the Apache License 2.0.

License information should still be reviewed against the exact model
version and any additional components used by the final system.

---

# 4. Selected Model

## 4.1 Qwen3 8B

The project team selected Qwen3 8B as the language-generation model
for KrishiVaani-AI.

### Model Details

| Property | Details |
|---|---|
| Model Family | Qwen3 |
| Selected Model | Qwen3 8B |
| Ollama Identifier | `qwen3:8b` |
| Parameters | Approximately 8.19B |
| Ollama Model Size | Approximately 5.2 GB |
| Quantization | Q4_K_M |
| Context Window in Ollama Listing | 40K tokens |
| Model Type | Dense language model |
| License | Apache License 2.0 |
| Runtime | Ollama |
| Primary Project Role | Response generation |
| Primary Languages | English, Hindi, Kannada |
| Project Domain | Agriculture |

---

# 5. Reason for Selection

Qwen3 8B was selected after discussion with the KrishiVaani-AI
project team and consideration of the project's technical
requirements.

The selection is based on the following project requirements:

## 5.1 Multilingual Capability

KrishiVaani-AI requires support for English, Hindi, and Kannada.

Qwen3 documentation identifies support for 119 languages and
dialects, including Hindi and Kannada.

This makes the model relevant to the multilingual requirements of
KrishiVaani-AI.

---

## 5.2 Local Ollama Deployment

Qwen3 8B is available through Ollama using the model identifier:

`qwen3:8b`

This allows the project to run the model locally during development
and testing.

The model can be obtained through Ollama using:

`ollama pull qwen3:8b`

and executed using:

`ollama run qwen3:8b`

---

## 5.3 Suitable Model Size

The selected 8B model provides a balance between model capability
and local deployment requirements.

The Ollama package is approximately 5.2 GB.

This makes Qwen3 8B more practical for local experimentation than
substantially larger Qwen3 variants.

The actual performance and memory requirements will be measured
during project testing.

---

## 5.4 RAG Integration

KrishiVaani-AI is based on a RAG architecture.

Qwen3 8B will be used as the generation model after relevant
agricultural information has been retrieved from the project's
knowledge base.

The intended flow is:

Retrieved Agricultural Context
        +
User Question
        ↓
Qwen3 8B
        ↓
Grounded Agricultural Response

---

## 5.5 Open-Weight Availability

Qwen3 is available as an open-weight model family and the selected
Qwen3 8B model is listed under the Apache License 2.0.

This is suitable for the project's research and development
requirements, subject to checking the applicable license terms for
the exact model and other system components.

---

# 6. Ollama Setup

## 6.1 Ollama Runtime

Ollama is used as the local runtime for Qwen3 8B.

The purpose of using Ollama is to provide a simple local interface
for downloading, running, and integrating the LLM into the
KrishiVaani-AI development environment.

---

## 6.2 Verify Ollama Installation

The installation can be verified using:

    ollama --version

If Ollama is correctly installed, the command should return the
installed Ollama version.

---

## 6.3 Download Qwen3 8B

The selected model can be downloaded using:

    ollama pull qwen3:8b

This downloads the Qwen3 8B model to the local Ollama environment.

---

## 6.4 Verify Downloaded Models

Installed models can be checked using:

    ollama list

The expected model entry is:

    qwen3:8b

---

## 6.5 Run Qwen3 8B

The model can be started using:

    ollama run qwen3:8b

A basic test question can then be entered to verify that the model
responds correctly.

Example:

    What is Retrieval-Augmented Generation?

---

# 7. Model Verification

After installation, the model should be verified before integration
into the KrishiVaani-AI backend.

The verification process includes:

1. Checking that Ollama is installed.
2. Checking that `qwen3:8b` is downloaded.
3. Starting the model successfully.
4. Sending a basic question.
5. Confirming that a response is generated.
6. Testing multilingual queries.
7. Testing agricultural questions.
8. Testing context-grounded responses.
9. Recording response-time and resource observations.

---

## 7.1 Basic Functional Test

### Input

    What is an LLM?

### Expected Verification

The model should generate a coherent explanation of what a Large
Language Model is.

### Status

Pending actual local execution and verification.

---

# 8. Multilingual Testing

Multilingual testing is important because English, Hindi, and Kannada
are the three primary languages defined within the project's scope.

Testing should determine whether Qwen3 8B can understand questions
and produce appropriate responses in each target language.

---

## 8.1 English Test

### Input

    What are the common causes of yellowing leaves in rice plants?

### Purpose

To verify English agricultural question understanding and response
generation.

### Result

To be recorded after local testing.

---

## 8.2 Hindi Test

### Input

    धान की फसल में पत्तियों के पीले होने के सामान्य कारण क्या हैं?

### Purpose

To verify Hindi question understanding and response generation.

### Result

To be recorded after local testing.

---

## 8.3 Kannada Test

### Input

    ಭತ್ತದ ಬೆಳೆಯಲ್ಲಿ ಎಲೆಗಳು ಹಳದಿ ಬಣ್ಣಕ್ಕೆ ತಿರುಗಲು ಸಾಮಾನ್ಯ ಕಾರಣಗಳು ಯಾವುವು?

### Purpose

To verify Kannada question understanding and response generation.

### Result

To be recorded after local testing.

---

## 8.4 Multilingual Test Evaluation

The following characteristics should be observed:

| Evaluation Aspect | Description |
|---|---|
| Question Understanding | Whether the model correctly understands the question |
| Language Consistency | Whether the response is generated in the requested language |
| Relevance | Whether the response addresses the question |
| Clarity | Whether the response is understandable |
| Agricultural Relevance | Whether the response remains relevant to the agricultural domain |
| Unsupported Claims | Whether the response contains information not supported by provided context |
| Response Time | Approximate time required to generate the answer |

---

# 9. Agricultural Question Testing

The model should be tested using agricultural questions that are
representative of the KrishiVaani-AI use case.

---

## 9.1 Crop Symptom Test

### Question

    What are the possible causes of yellow leaves in a rice crop?

### Purpose

To determine whether the model can produce a relevant response to a
basic agricultural question.

### Result

To be recorded after testing.

---

## 9.2 Nutrient Deficiency Test

### Question

    What are the common symptoms of nitrogen deficiency in rice?

### Purpose

To test agricultural knowledge related to crop nutrition.

### Result

To be recorded after testing.

---

## 9.3 Farmer-Friendly Explanation Test

### Question

    What are the possible causes of yellowing in rice leaves?
    Explain it in simple language suitable for an Indian farmer.

### Purpose

To evaluate whether the model can produce a clear and accessible
response rather than unnecessarily technical language.

### Result

To be recorded after testing.

---

## 9.4 Multilingual Agricultural Test

The same agricultural concept should be tested in:

- English
- Hindi
- Kannada

This allows the team to observe whether the model maintains the
meaning of the agricultural question across languages.

---

# 10. RAG Compatibility Testing

RAG is a central component of KrishiVaani-AI.

The purpose of this test is not to determine whether Qwen3 8B knows
agriculture independently, but whether it can generate an answer
using agricultural information supplied as retrieved context.

---

## 10.1 RAG Architecture

The planned RAG flow is:

    User Question
          ↓
    Query Processing
          ↓
    Embedding Generation
          ↓
    PGVector Search
          ↓
    Relevant Agricultural Documents
          ↓
    Retrieved Context
          ↓
    Qwen3 8B
          ↓
    Grounded Answer

---

## 10.2 Context-Grounding Test

### Context

    Nitrogen deficiency in rice can cause older leaves to become
    pale or yellow. Nitrogen is important for vegetative growth.

### Question

    Why might older rice leaves become yellow?

### Instruction

    Answer only using the information provided in the context.

### Purpose

To verify whether Qwen3 8B can generate an answer using supplied
context.

### Expected Behavior

The response should be based on the supplied information and should
not introduce unrelated unsupported facts.

### Result

To be recorded after testing.

---

## 10.3 Unsupported Information Test

### Context

    Nitrogen deficiency can cause older rice leaves to become pale
    or yellow.

### Question

    What fertilizer should the farmer definitely apply, and what
    exact quantity should be applied?

### Purpose

The supplied context does not provide a fertilizer type or quantity.

This test is intended to observe whether the model recognizes the
information limitation instead of inventing a specific fertilizer
recommendation or quantity.

### Result

To be recorded after testing.

---

# 11. Role of Qwen3 8B in the Overall System

Qwen3 8B is intended to function as the response-generation model.

It is not the entire KrishiVaani-AI system.

The planned components include:

| Component | Purpose |
|---|---|
| User Interface | Accept agricultural questions |
| Whisper | Speech-to-text for voice input |
| Query Processing | Process user questions |
| Embedding Model | Convert text into vectors |
| PostgreSQL + PGVector | Store and retrieve vectorized agricultural information |
| Agricultural Knowledge Base | Provide domain-specific information |
| Qwen3 8B | Generate responses using retrieved context |
| Evaluation Module | Measure accuracy, relevance, grounding, hallucination and related metrics |

---

# 12. Distinction Between LLM and Embedding Model

Qwen3 8B is selected as the language-generation model.

It should not be confused with the embedding model used for vector
retrieval.

The embedding model is responsible for converting:

    Agricultural Document
            ↓
        Vector Embedding
            ↓
          PGVector

Qwen3 8B is responsible for:

    Retrieved Context
          +
    User Question
          ↓
       Qwen3 8B
          ↓
    Generated Response

The embedding model will therefore be selected and documented
separately.

---

# 13. Resource and Performance Observations

The selected Qwen3 8B Ollama model is approximately 5.2 GB and
contains approximately 8.19 billion parameters.

The Ollama listing identifies the model as Q4_K_M quantized.

The following measurements should be recorded during local testing.

| Metric | Observation |
|---|---|
| Model Size | Approximately 5.2 GB |
| Parameters | Approximately 8.19B |
| Quantization | Q4_K_M |
| Context Window | 40K tokens in current Ollama listing |
| Download Time | To be measured |
| Model Load Time | To be measured |
| First Response Time | To be measured |
| Average Response Time | To be measured |
| RAM Usage | To be measured |
| GPU/CPU Usage | To be measured |
| Hardware Used | To be recorded |
| Concurrent Requests | To be evaluated later |

Performance measurements should be collected on the actual hardware
used by the development team rather than assumed from the model
specification.

---

# 14. Expected Integration

After successful verification, Qwen3 8B will be integrated into the
KrishiVaani-AI backend.

The planned high-level integration is:

    Frontend
       ↓
    User Query
       ↓
    Backend
       ↓
    Query Processing
       ↓
    Retriever / PGVector
       ↓
    Retrieved Agricultural Context
       ↓
    Qwen3 8B through Ollama
       ↓
    Generated Response
       ↓
    Backend
       ↓
    Frontend

The backend will communicate with the locally running Ollama
service to submit prompts and receive generated responses.

---

# 15. Prompting Strategy

The final system prompt should instruct Qwen3 8B to behave as an
agricultural information assistant and to prioritize retrieved
context.

A preliminary prompt structure is:

    You are KrishiVaani, an agricultural information assistant.

    Answer the user's question using the retrieved agricultural
    context provided below.

    Prefer information supported by the retrieved context.

    Do not invent facts, measurements, recommendations, or
    quantities that are not supported by the available information.

    If the available context is insufficient to answer the question,
    clearly state that the available information is insufficient.

    Respond in the same language as the user's question.

    Retrieved Context:
    {context}

    User Question:
    {question}

This prompt is a preliminary design and will be refined during
system implementation and evaluation.

---

# 16. Evaluation Plan

Qwen3 8B will be evaluated as part of the broader KrishiVaani-AI
experimental methodology.

The project will compare the behavior of:

1. LLM-only generation
2. RAG-based generation
3. Multilingual RAG generation

The evaluation will consider:

- Accuracy
- Relevance
- Grounding
- Hallucination rate
- Response quality
- Multilingual performance
- Retrieval effectiveness
- Response time

The exact evaluation dataset and scoring methodology will be
defined separately as part of the project's experimental methodology.

---

# 17. Limitations

The selection of Qwen3 8B does not guarantee that every agricultural
answer will be correct.

The following limitations must be considered:

1. The model is a general-purpose LLM and is not itself an
   authoritative agricultural knowledge base.

2. LLM-generated responses can contain incorrect or unsupported
   information.

3. Multilingual capability does not guarantee identical response
   quality across English, Hindi, and Kannada.

4. Local inference performance depends on the available hardware.

5. Response time may vary depending on prompt length, context size,
   hardware, and runtime configuration.

6. RAG quality depends on the quality of the agricultural documents,
   embeddings, retrieval system, and retrieved context.

7. An incorrect or incomplete retrieval result can affect the final
   generated answer.

8. The system should not be treated as a replacement for qualified
   agricultural experts.

9. Agricultural recommendations that require precise local,
   environmental, or expert knowledge should be handled carefully.

10. The model-selection decision should be revisited if experimental
    evaluation demonstrates that the selected model does not meet
    the project's required performance.

---

# 18. Security and Responsible Use Considerations

KrishiVaani-AI is intended to provide agricultural information.

The system should therefore:

- Avoid presenting unsupported information as established fact.
- Prefer retrieved and verified agricultural information.
- Clearly communicate uncertainty when sufficient information is not
  available.
- Avoid fabricating sources or agricultural recommendations.
- Preserve the distinction between retrieved evidence and generated
  explanation.
- Log evaluation results during development.
- Avoid exposing sensitive system information through prompts or
  generated responses.
- Validate model outputs before using them in production workflows.

The RAG system should be designed so that the model is guided by the
curated agricultural knowledge base rather than relying entirely on
its internal model knowledge.

---

# 19. Future Improvements

Possible future improvements include:

- Fine-tuning or domain adaptation if required by evaluation.
- Improving the agricultural knowledge base.
- Improving document chunking and retrieval.
- Evaluating different embedding models.
- Improving multilingual retrieval.
- Adding better Kannada and Hindi evaluation datasets.
- Measuring hallucination rates using a larger evaluation set.
- Optimizing Ollama inference performance.
- Evaluating different quantization levels if hardware permits.
- Adding automated evaluation pipelines.
- Integrating voice input through Whisper.
- Improving response formatting for farmer-friendly communication.

These improvements are outside the immediate scope of the model
selection task unless assigned separately.

---

# 20. Conclusion

After reviewing the requirements of the KrishiVaani-AI system and
discussing the available options with the project team, Qwen3 8B was
selected as the project's open-weight language-generation model to
be deployed through Ollama.

The selection is aligned with the project's requirements for local
deployment, multilingual interaction, RAG-based response
generation, and reasonable local resource requirements.

Qwen3 supports a broad range of languages and dialects, including
English, Hindi, and Kannada, which are the primary languages defined
for KrishiVaani-AI.

The model will be used as the generation component of the RAG
pipeline. Agricultural information will be retrieved from the
project's curated knowledge base and supplied to Qwen3 8B as
context.

The final effectiveness of the model will be determined through
actual multilingual, agricultural, RAG-grounding, hallucination,
accuracy, relevance, and performance testing.

Therefore, the current project decision is:

    Selected LLM: Qwen3 8B
    Runtime: Ollama
    Ollama Identifier: qwen3:8b
    Primary Role: RAG response generation
    Languages: English, Hindi, Kannada

---

# 21. References

## [1] Ollama – Qwen3 Model Library

Ollama model listing for the Qwen3 family and available model
variants.

## [2] Qwen – Qwen3: Think Deeper, Act Faster

Official Qwen3 documentation describing the Qwen3 model family,
multilingual capabilities, and supported languages.

## [3] KrishiVaani-AI Research Questions, Objectives and Scope

Project documentation defining the research problem, languages,
RAG architecture, evaluation objectives, and project scope.

## [4] KrishiVaani-AI Literature Review on Multilingual RAG and
Agricultural AI

Project literature review covering multilingual RAG, agricultural AI,
LLM-based question answering, speech-to-text, and related research.

---

# 22. Task Completion Record

**Jira Task:** Research and select open-source LLM via Ollama

**Epic:** Research & Planning

**Assignee:** Vedh Nayak K

**Selected Model:** Qwen3 8B

**Runtime:** Ollama

**Documentation:** `docs/llm_selection_ollama.md`

**Git Branch:** `feature/vedh`

**Implementation Status:** Model selected; local verification and
backend integration to be completed as subsequent development work.

**Research Status:** Selection documented.

**Final Decision:** Qwen3 8B selected by the project team for
KrishiVaani-AI.