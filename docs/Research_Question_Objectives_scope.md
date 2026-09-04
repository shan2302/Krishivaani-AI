# KrishiVaani AI — Research Question, Objectives and Scope

    ## 1. Problem Statement

        Indian farmers often face difficulties in accessing reliable and understandable agricultural information. Language barriers, limited digital literacy, and the complexity of existing agricultural information resources can make it difficult for farmers to obtain timely answers to their questions.
        Large Language Models (LLMs) provide a convenient way to ask questions in natural language. However, LLMs can generate incorrect, unsupported, or fabricated information, commonly referred to as hallucinations. This is particularly concerning in the agricultural domain, where incorrect recommendations about crops, fertilizers, irrigation, pests, or diseases may negatively affect farming decisions.
        Another challenge is multilingual accessibility. A large proportion of agricultural information systems and AI tools primarily focus on English, while many Indian farmers prefer to communicate in regional languages.
        KrishiVaani AI aims to address these challenges by developing a multilingual, voice-enabled agricultural question-answering system using Retrieval-Augmented Generation (RAG). Instead of relying only on the knowledge stored within an LLM, the system will retrieve relevant information from a curated agricultural knowledge base and use that information to generate grounded responses.
        The research will investigate whether a RAG-based approach can provide more accurate, relevant, and grounded agricultural answers while reducing hallucinations compared with an LLM-only approach.

    ## 2. Research Question

        ### 2.1 Main Research Question

            **Can a multilingual Retrieval-Augmented Generation (RAG) system provide more accurate, relevant, and grounded agricultural answers with fewer hallucinations than a standard Large Language Model (LLM) for users asking questions in English, Hindi, and Kannada?**

        ### 2.2 Supporting Research Questions

            1. Does Retrieval-Augmented Generation reduce hallucinations compared with an LLM-only approach?
            2. Does retrieving information from a curated agricultural knowledge base improve the accuracy and relevance of generated answers?
            3. How effectively does the RAG system handle agricultural questions across English, Hindi, and Kannada?
            4. How accurately can Whisper Speech-to-Text convert spoken agricultural questions into text?
            5. Does the performance of the RAG system vary across English, Hindi, and Kannada?
            6. How does the performance of the LLM-only baseline compare with the RAG-based system using quantitative evaluation metrics?

    ## 3. Research Objectives

    The primary objectives of KrishiVaani AI are:

        1. **To develop an end-to-end multilingual agricultural question-answering system** supporting English, Hindi, and Kannada.

        2. **To implement a Retrieval-Augmented Generation (RAG) pipeline** that retrieves relevant information from a curated agricultural knowledge base before generating responses.

        3. **To integrate Whisper Speech-to-Text (STT)** to enable users to submit agricultural questions through voice.

        4. **To develop a vector-based information retrieval system** using PostgreSQL and PGVector for storing and retrieving agricultural knowledge efficiently.

        5. **To establish an LLM-only baseline** and compare its performance with the proposed RAG-based approach.

        6. **To evaluate whether RAG reduces hallucinations** and improves the accuracy, relevance, and grounding of agricultural answers.

        7. **To evaluate multilingual performance** by testing the system across English, Hindi, and Kannada.

        8. **To quantitatively evaluate the system** using appropriate metrics including Precision, Recall, Word Error Rate (WER), and Hallucination Rate.

        9. **To analyze the results and document the findings** as part of a comprehensive academic research paper.

    ## 4. Project Scope

        ### 4.1 Supported Languages

            The initial version of KrishiVaani AI will support three languages:

            * English
            * Hindi
            * Kannada

            Users will be able to ask questions and receive responses according to the supported language workflow.

            The research will specifically investigate whether the RAG approach performs consistently across these three languages.

        ### 4.2 Input Methods

            The system will support two primary input methods:

            #### Text Input

                Users can type agricultural questions directly into the application.

            #### Voice Input

                Users can ask agricultural questions using their voice.

            The voice input will be processed using **Whisper Speech-to-Text/any Speech-To-Text which is free of cost**, which will convert spoken questions into text before they are processed by the RAG pipeline.

        ### 4.3 AI and NLP Components

        The project will investigate and integrate the following technologies:

            * Large Language Models (LLMs)
            * Retrieval-Augmented Generation (RAG)
            * Text embeddings
            * Vector similarity search
            * Whisper Speech-to-Text
            * Multilingual natural language processing
            * Prompt-based response generation

        The exact model configuration may be refined during experimentation based on availability, performance, and research requirements.

---

        ### 4.4 Agricultural Knowledge Base

        The RAG system will use a curated agricultural knowledge base containing reliable agricultural information.

        The initial knowledge areas may include:
            * Crop cultivation
            * Crop diseases
            * Pest management
            * Fertilizer usage
            * Irrigation
            * Soil management
            * Crop selection
            * Agricultural practices
            * Basic crop-care information

        The retrieved information will be used as supporting context for the LLM so that generated responses are grounded in the available knowledge base.

        ### 4.5 Backend and Database Scope

        The backend will be developed using:

            * Spring Boot
            * Spring AI
            * PostgreSQL
            * PGVector

        PostgreSQL will be used for structured data storage, while PGVector will support vector embeddings and similarity-based retrieval.

        The backend will manage the overall workflow from receiving a user query to retrieving relevant information and generating the final response.

        ### 4.6 RAG Pipeline Scope

        The proposed RAG workflow is:

            User Question
                ↓
            Text or Voice Input
                ↓
            Whisper STT (for voice)/Any STT(which is free)
                ↓
            Question Processing
                ↓
            Embedding Generation
                ↓
            PGVector Similarity Search
                ↓
            Relevant Agricultural Information
                ↓
            LLM + Retrieved Context
                ↓
            Grounded Response
                ↓
            User

        For text input, the Whisper step will be skipped.

    ## 5. Research Experiments

    The research will consist of three primary experiments.

        ### Experiment A — LLM-Only Baseline

        The agricultural question will be sent directly to the selected LLM without retrieving information from the external knowledge base.
            Question → LLM → Answer
        This experiment will establish the baseline performance of the system.

        ### Experiment B — RAG-Grounded Evaluation

        The same or equivalent questions will be processed using the RAG pipeline.

            Question
            ↓
            Retrieve Relevant Information
            ↓
            LLM + Retrieved Context
            ↓
            Grounded Answer

        The results will be compared with the LLM-only baseline.

        The purpose is to determine whether retrieval improves answer quality and reduces hallucinations.


        ### Experiment C — Cross-Lingual RAG Evaluation

        The RAG system will be evaluated using agricultural questions in:

            * English
            * Hindi
            * Kannada

        The experiment will investigate how effectively the system retrieves relevant information and generates appropriate answers across different languages.

    ## 6. Evaluation Metrics

    The project will use quantitative and qualitative evaluation methods.

        ### 6.1 Precision
            Precision will be used to evaluate the proportion of retrieved information that is relevant to the user's query.

        ### 6.2 Recall
            Recall will measure how effectively the retrieval system finds relevant information available in the knowledge base.

        ### 6.3 Word Error Rate (WER)
            WER will be used to evaluate the accuracy of Whisper Speech-to-Text when converting spoken agricultural questions into text.

        ### 6.4 Hallucination Rate
            Hallucination Rate will be used to measure how frequently the generated answer contains information that is unsupported, incorrect, or not grounded in the retrieved knowledge.

        ### 6.5 Answer Relevance
            The generated response will also be evaluated based on whether it directly addresses the user's agricultural question.

        ### 6.6 Groundedness
            The response will be evaluated based on whether its claims are supported by the information retrieved from the agricultural knowledge base.

    ## 7. Out of Scope

    The following features are outside the scope of the initial 8-week research and engineering project:

        7.1. Supporting every Indian language. The initial research will focus only on English, Hindi, and Kannada.

        7.2. Replacing agricultural experts, agricultural officers, or professional agronomists.

        7.3. Guaranteeing 100% accuracy for every agricultural recommendation.

        7.4. Automated control of agricultural machinery, irrigation systems, drones, or other physical equipment.

        7.5. Real-time farm monitoring using IoT sensors.

        7.6. Large-scale satellite-image analysis or remote-sensing-based farm monitoring.

        7.7. Automatic crop disease diagnosis using agricultural images.

        7.8. Providing financial, legal, insurance, or loan-related agricultural services.

        7.9. Large-scale commercial deployment across all regions of India.

        7.10. Building a complete agricultural marketplace or e-commerce platform.

        7.11. Developing a complete crop-yield prediction system.

        7.12. The primary research focus will remain on multilingual RAG, agricultural question answering, voice input, and hallucination reduction rather than developing a full commercial agricultural ecosystem.

---

    ## 8. Expected Research Contribution

    The primary research contribution of KrishiVaani AI will be an experimental evaluation of multilingual Retrieval-Augmented Generation for agricultural question answering.

    The study will compare an LLM-only approach with a RAG-based approach to determine whether retrieving information from a curated agricultural knowledge base can improve answer quality and reduce hallucinations.

    The research will also investigate the performance of the system across English, Hindi, and Kannada and evaluate the effectiveness of voice-based interaction using Whisper Speech-to-Text.

    The findings will provide evidence regarding the benefits, limitations, and challenges of applying multilingual RAG to accessible agricultural information systems in the Indian context.

    The resulting experimental findings will form the foundation for the project's academic research paper.

---

    ## 9. Research Hypothesis

        ### 9.1 Alternative Hypothesis (H1)

            **A multilingual Retrieval-Augmented Generation system will provide more accurate, relevant, and grounded agricultural answers and produce fewer hallucinations than an LLM-only system.**

        ### 9.2 Null Hypothesis (H0)

            **There is no significant difference in answer quality, accuracy, relevance, or hallucination rate between the LLM-only approach and the Retrieval-Augmented Generation approach.**

---

    ## 10. Research Boundaries

    The research will focus specifically on evaluating whether a multilingual RAG architecture can improve agricultural question answering.

    The project will therefore prioritize:

        * Reliable agricultural information retrieval
        * Multilingual question answering
        * Voice-to-text processing
        * Hallucination reduction
        * Retrieval quality
        * Answer quality
        * Comparative evaluation
        * Reproducible experiments
        * Academic documentation

    The project will not attempt to solve every problem associated with digital agriculture. Instead, it will use a clearly defined research problem that can be implemented, tested, evaluated, and documented within the planned 8-week project duration.

---

    ## 11. Summary

        KrishiVaani AI proposes a multilingual and voice-enabled agricultural question-answering system based on Retrieval-Augmented Generation.

        The research will investigate whether combining a curated agricultural knowledge base with an LLM can produce better-grounded and less hallucinated answers than an LLM operating without external retrieval.

        The system will support English, Hindi, and Kannada and will accept both text and voice-based questions. Voice queries will be converted to text using Whisper Speech-to-Text, while PostgreSQL and PGVector will support the storage and retrieval of agricultural knowledge.

        The research will compare LLM-only, RAG-based, and multilingual RAG approaches using metrics such as Precision, Recall, Word Error Rate, Hallucination Rate, answer relevance, and groundedness.

        The findings will be used to evaluate the effectiveness and limitations of multilingual RAG for agricultural information access in the Indian context and will contribute to the project's final academic research paper.