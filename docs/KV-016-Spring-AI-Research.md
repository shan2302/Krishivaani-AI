# KV-016 - Spring AI Capabilities and Integration Patterns

## 1. Objective

--> The objective of this Research is to study the capabilities of Spring AI and identify suitable integration patterns for the KrishiVaani AI systems..

--> KrishiVaani is a multilingual agricultural information system designed to provide reliable agricultural information to Indian farmers using Verified agricultural knowledge sources

--> The System is planned to support:
    -> English,Hindi and Kannada text queries
    -> Voice-based input using Speech-to-Text
    -> Large Language Model(LLM) based responses
    -> Retrieval-Augmented Generation(RAG)
    -> PostgreSQL with PGVector for vector storage
    -> Retrieval of information from verified agricultural documents
    -> Comparison between LLM-only and RAG-based approaches
    -> Cross-lingual retrieval and response generation
-->This research focuses specifically on understanding how Spring AI can be used to integrate these components into the KrishiVaani backend
--> The Main questions investigated are:
    1.1) How can Spring AI communicate with an LLM>
    1.2) How can Spring AI generate and manage embeddings?
    1.3) How can PostgreSQL/PG Vector be used as a vector store?
    1.4) How can RAG be implemented using Spring AI?
    1.5) How can multilingual queries be handled?
    1.6) What integration pattern is appropriate for KrishiVaani?
    1.7) What are the limitations and risks of using Spring AI for this system?

## 2. What is Spring AI?
--> SPring AI is a Spring-based application framework that provides abstractions for integrating Artificial Intelligence capabilities into Java and Spring Boot Applications
--> Instead of implementing communication with every AI provider separately, Spring AI provides common interfaces and abstractions for AI-related operations.
--> Important Concepts include:
    -> ChatClient
    -> ChatModel
    -> EmbeddingModel
    -> VectorStore
    -> Document
    -> Advisors
    -> Retrieval-Augmented Generation ( RAG )
    -> Structured output
    -> Prompt templates
    -> Tool/function calling
--> For KrishiVaani, the most important components are:

                Spring Boot
                    |
                |------------|
                |            |
                v            v
             ChatModel  EmbeddingModel 
                |            |
                v            v
               LLM          PGVector
                             |
                             v
                            Retrieved Data

## 3. Spring AI Capabilities relevant to KrishiVaani

The following Spring AI capabilities are relevant to the KrishiVaani architecture.

    3.1 ChatClient
        -> ChatClient provides a convenient API for interacting with chat-based AI models
        -> It can be used to:
            -> Send user questions to an LLM
            -> Construct prompts
            -> Add System instructions
            -> Include retrieved context
            -> Process model responses
        For KrishiVaani, ChatClient can be used as the primary interface for generating the final agricultural answer.
        Conceptually:
        Farmer Question
            |
            v
        ChatClient
            |
            v
        Chat Model / LLM
            |
            v
        Generated Answer
    3.2 ChatModel
        -> The ChatModel abstraction represents the underlying chat-capable AI model
        -> This abstraction helps reduce direct dependency on a specific model implementation
        -> The application can therefore keep the business logic relatively independent from the particular LLM provider.
        -> For KrishiVaani,the ChatModel will be responsible for generating responses from:
            User Queries
            System Instructions
            Retrieved agricultural context
            Conversation information, where applicable
    3.3 EmbeddingModel
        --> An EmbeddingModel converts text into numerical vectors.
        --> These vectors represent semantic information about the text
        "How often should rice fields be irrigated?"
                    |
                    v
             EmbeddingModel
                    |
                    v
          [0.12, -0.31, 0.74, ...]
        --> The resulting vector can be stored in PG Vector
        --> When a user asks a similar question, the query can also be converted into a vector and compared with stored document vectors.
        --> This enables semantic search rather than relying only on exact keyword matching.
    3.4 VectorStore
        --> Spring AI provides the VectorStore abstraction for storing and searching vector representationa of documents.
        --> For KrishiVaani, PostgreSQL with the PGVector extension is planned as the Vector database
        The general flow is:
            Agricultural Documents
                    |
                    v
            Document Processing
                    |
                    v
            EmbeddingModel
                    |
                    v
            Vector Embeddings
                    |
                    v
            PostgreSQL + PGVector
        During a user query:
            User Question
                |
                v
            EmbeddingModel
                |
                v
            Query Vector
                |
                v
            PGVector Similarity Search
                |
                v
            Relevant Documents
    3.5 Document abstraction
        Spring AI provides a Document abstraction for representing pieces of information that can be processed and retrieved.

        A document can contain:
            Text content
            Metadata
            Additional information useful during retrieval

        For KrishiVaani, agricultural sources can be converted into documents before being embedded and stored.

        Example conceptual metadata:
            source = agricultural_department
            language = hindi
            crop = rice
            topic = irrigation

        Metadata can later help filter or identify relevant information.
    3.6 Advisors
        Spring AI provides an advisor mechanism that can modify or enhance ChatClient interactions.

        Advisors can be used for functionality such as:

            Retrieval
            Conversation memory
            Request processing
            Response processing

        For RAG, a retrieval advisor can be used to retrieve relevant information from a vector store and provide it to the LLM as context.

        This is particularly relevant to KrishiVaani because retrieval should happen before the final answer is generated.
## 4. ChatClient and LLM Integration
    --> The proposed KrishiVaani backend will use Spring AI's ChatClient as the application-facing interface for interacting with the LLM.
    REST API
        |
        v
    KrishiVaani Service
        |
        v
    ChatClient
        |
        v
    ChatModel
        |
        v
       LLM
    --> The application should avoid tightly coupling business logic to a specific model implementation.
    --> The service layer should be responsible for preparing the request, while Spring AI handles communication with the configured model.
    --> LLM-only experiment
            One of the research experiments is the LLM-only baseline.
            In this experiment, the user question is sent directly to the LLM without retrieving external agricultural documents.
        User Query
            |
            v
        ChatClient
            |
            v
        LLM
            |
            v
        Answer
    --> This experiment establishes a baseline against which the RAG system can be evaluated.

    --> The baseline is important because it allows the project to measure whether retrieval from verified sources improves answer reliability.
## 5. EmbeddingModel
-->Embeddings are numerical representations of text that allow semantically similar content to be compared.
For example:- 
Query:"How much water does paddy require?"
Document: "Rice cultivation requires appropriate irrigation throughout different growth stages."
Although the exact words are different, their meanings are related.
An embedding model can represent both texts as vectors in the same semantic space.
Similarity can then be calculated between the query vector and document vectors.

KrishiVaani embedding pipeline
Verified Agricultural Document
        |
        v
    Text Extraction
        |
        v
    Chunking
        |
        v
    EmbeddingModel
        |
        v
    Vector Embedding
        |
        v
    PostgreSQL/PGVector
During retrieval:
    Farmer Query
        |
        v
    EmbeddingModel
        |
        v
    Query Embedding
        |
        v
    PGVector Similarity Search
    
    -->The selected embedding model should support the languages required by KrishiVaani, particularly Hindi and Kannada, or the project should evaluate the quality of cross-lingual embeddings experimentally.
    -->Embedding quality is therefore an important research consideration.
## 6. VectorStore and PGVector
    --> KrishiVaani will use PostgreSQL with the PGVector extension as the vector storage layer.
    --> PGVector allows PostgreSQL to store vector embeddings and perform similarity searches.
    --> The Conceptual database structure is:- 
        PostgreSQL
            |
            +-- Agricultural Document Data
            |
            +-- Document Metadata
            |
            +-- Vector Embeddings
            |
            +-- PGVector Similarity Search
        Spring AI can interact with the vector database through its VectorStore abstraction.
    --> Indexing flow
    The initial ingestion process is:
        Verified Source
            |
            v
        Document Reader
            |
            v
        Document
            |
            v
        Text Chunking / Transformation
            |
            v
        EmbeddingModel
            |
            v
        VectorStore
            |
            v
        PGVector
    --> Retrieval flow
    When a farmer asks a question:
    Question
        |
        v
    EmbeddingModel
        |
        v
    Query Vector
        |
        v
    VectorStore
        |
        v
    PGVector
        |
        v
    Top Relevant Documents
    The retrieved documents are then supplied as context to the LLM.
## 7. RAG Integration
    --> Retrieval-Augmented Generation (RAG) combines information retrieval with LLM-based generation.
    --> Instead of asking the LLM to answer entirely from its learned knowledge, the application first retrieves relevant information from an external knowledge base.
    --> KrishiVaani RAG pipeline
                    +----------------------+
                    | Verified Agriculture |
                    | Knowledge Sources    |
                    +----------+-----------+
                               |
                               v
                         Document Processing
                               |
                               v
                         EmbeddingModel
                               |
                               v
                         PostgreSQL/PGVector
                               |
                               |
 Farmer Query                  |
      |                        |
      v                        |
 EmbeddingModel                |
      |                        |
      v                        |
 Query Vector -----------------+
              |
              v
       Similarity Search
              |
              v
     Relevant Documents
              |
              v
          ChatClient
              |
              v
             LLM
              |
              v
      Grounded Response
    
    --> The retrieved context should be clearly separated from the user's original query.
    --> A conceptual prompt structure is:
        System Instructions:
        Answer using the supplied agricultural context.
        Do not invent unsupported facts.

        Retrieved Context:
        [Relevant agricultural information]

        User Question:
        [Farmer's question]
        The purpose is to reduce unsupported generation and improve grounding in verified agricultural information.
    --> RAG experiment
        The second experiment will evaluate the RAG-based system.
        User Query
            |
            v
        Retrieve Relevant Documents
            |
            v
        Provide Context to LLM
            |
            v
        Generate Answer
        The results can then be compared with the LLM-only baseline.
## 8. Multilingual Considerations
 -->KrishiVaani is designed to support English, Hindi, and Kannada.
 -->Therefore, multilingual support must be considered at multiple stages.
 -->8.1 User input
    The user may provide:
        English text
        Hindi text
        Kannada text
        Voice input
    Voice input will first be converted into text using the Speech-to-Text component.
            Voice
            |
            v
        Whisper STT
            |
            v
        Hindi/Kannada/English Text
    The resulting text can then enter the same AI pipeline.
 -->8.2 Multilingual embeddings
    The embedding model should be evaluated for its ability to represent the supported languages.
    This is particularly important for cross-lingual retrieval.
            Hindi Query
                |
                v
            Multilingual Embedding
                |
                v
            Vector Search
                |
                v
            English/Kannada/Hindi Documents
    If the embedding model places semantically equivalent multilingual sentences close together in vector space, cross-lingual retrieval becomes possible.
    This capability must be verified experimentally rather than assumed.
 -->8.3 Cross-lingual RAG experiment
    The third planned experiment is cross-lingual RAG.
        Hindi Question
            |
            v
        Multilingual Embedding
            |
            v
        PGVector
            |
            v
        English/Kannada/Hindi Documents
            |
            v
        Relevant Context
            |
            v
           LLM
            |
            v
        Hindi Answer
    This experiment will evaluate whether information can be retrieved across languages while maintaining answer relevance and correctness.
  -->8.4 Response language
    The final answer should preferably be generated in the language requested by the user.
        Hindi Query  -> Hindi Response
        Kannada Query -> Kannada Response
        English Query -> English Response
    The system should explicitly communicate the expected response language to the LLM through the prompt or application logic.
## 9. Proposed KrishiVaani architecture 
                         +----------------+
                         | Farmer / User  |
                         +-------+--------+
                                 |
                         Text / Voice Input
                                 |
                  +--------------+--------------+
                  |                             |
                  v                             v
             Text Query                    Whisper STT
                  |                             |
                  +--------------+--------------+
                                 |
                                 v
                       Spring Boot REST API
                                 |
                                 v
                     KrishiVaani Service Layer
                                 |
                                 v
                            Spring AI
                                 |
                 +---------------+---------------+
                 |                               |
                 v                               v
          EmbeddingModel                    ChatClient
                 |                               |
                 v                               v
            VectorStore                      ChatModel
                 |                               |
                 v                               v
          PostgreSQL/PGVector                    LLM
                 |
                 v
       Relevant Agricultural Context
                 |
                 +---------------+
                                 |
                                 v
                           Final Response
    Knowledge Ingestion Architecture
        Verified Agricultural Sources
                |
                v
        Document Ingestion
                |
                v
        Document Chunking
                |
                v
        EmbeddingModel
                |
                v
        PostgreSQL/PGVector
    This separates the knowledge ingestion process from the runtime question-answering process.
## 10. Recommended Integration Pattern
    The recommended integration pattern for KrishiVaani is a layered architecture.
    Controller Layer
       |
       v
    Service Layer
       |
       v
    Spring AI Integration
       |
       +-------------------+
       |                   |
       v                   v
   ChatClient         VectorStore
       |                   |
       v                   v
   Chat Model           PGVector
       |
       v
      LLM
    Responsibilities
    Controller
    Responsible for:
        Receiving HTTP requests
        Validating basic request data
        Returning API responses
    
    Service Layer
    Responsible for:
        Processing the user query
        Selecting the appropriate processing flow
        Coordinating retrieval and generation
        Applying application-level business rules
    Spring AI Layer
    Responsible for:
        LLM interaction
        Embedding generation
        Vector store interaction
        Retrieval integration
        Prompt construction

    PostgreSQL/PGVector
    Responsible for:
        Storing document metadata
        Storing embeddings
        Performing similarity search
    
    Knowledge Ingestion
    Responsible for:
        Reading verified agricultural sources
        Splitting documents into appropriate chunks
        Generating embeddings
        Storing documents and embeddings
    This separation should make the system easier to test and modify.
## 11. Risks and Limitations
    Although Spring AI provides useful abstractions, several risks must be considered.
    11.1 LLM hallucination
        An LLM can generate information that is not supported by the retrieved context.
        RAG can reduce this risk but cannot guarantee zero hallucinations.
        Therefore, the project should measure hallucination rather than claim that the system completely eliminates it.
    11.2 Retrieval quality
        Poor retrieval can result in irrelevant documents being supplied to the LLM.
        This means that even a powerful LLM may produce a poor answer if the retrieved context is incorrect or incomplete.
        Retrieval quality depends on:
            Embedding model
            Document chunking
            Metadata
            Similarity search
            Knowledge-source quality
    11.3 Multilingual retrieval
        Cross-lingual retrieval may not perform equally well across English, Hindi, and Kannada.
        The embedding model must therefore be experimentally evaluated.
    11.4 Knowledge-source quality
        RAG does not automatically make information reliable.
        If the source documents contain outdated or incorrect agricultural information, the system may retrieve and reproduce that information.
        Therefore, verified agricultural sources and appropriate document management are essential.
    11.5 Model dependency
        The final response quality depends on the selected LLM.
        Different models may produce different results for:
            Instruction following
            Multilingual generation
            Grounding
            Reasoning
            Agricultural terminology
        Therefore, the selected model should be documented as part of the experimental setup
    11.6 Context limitations
        Retrieved documents consume the LLM's context window.
        Retrieving too many documents can increase:
            Prompt size
            Processing cost
            Latency
            Irrelevant information
        Therefore, retrieval should return an appropriate number of relevant chunks.
    11.7 Latency
        User Query
            |
            v
        Embedding
            |
            v
        Vector Search
            |
            v
        Prompt Construction
            |
            v
        LLM Generation
        This can increase response latency compared with a direct LLM-only request.
        Latency should therefore be measured during the experiments.
## 12. Verification/Proof of Concept
    The Spring AI integration should be verified through a small Proof of Concept (PoC) before integrating the complete pipeline.
    PoC 1 - Basic LLM communication
    Goal: Verify that Spring Boot can communicate with the configured LLM through Spring AI.
    Expected flow:
    HTTP Request
        |
        v
    Spring Boot
        |
        v
    ChatClient
        |
        v
       LLM
        |
        v
    HTTP Response

    Example test question: What are common factors that affect crop growth?
    The PoC is successful if the backend receives a valid response from the model.
    PoC 2 - Embedding generation
    Goal:Verify that the configured embedding model can convert agricultural text into vectors.
    Example:
    Input: "Rice requires appropriate irrigation during its growth stages."
    Output: Numerical embedding vector
    The generated vector should be successfully returned without an embedding API/model error.
    PoC 3 - PGVector storage
    Goal:Verify that an embedding can be stored in PostgreSQL/PGVector and retrieved using similarity search.
    Flow:
            Text
            |
            v
        EmbeddingModel
            |
            v
          Vector
            |
            v
         PGVector
            |
            v
         Similarity Search
            |
            v
        Relevant Document
    PoC 4 - RAG
    Goal: Verify that retrieved agricultural information can be supplied to the LLM as context.
    Test flow:

                Question
                |
                v
            Vector Search
                |
                v
            Relevant Context
                |
                v
            ChatClient
                |
                v
               LLM
                |
                v
            Answer
    The response should be evaluated to determine whether it is supported by the retrieved context.
    PoC 5 - Multilingual query
    Example test cases should include equivalent questions in:
        English
        Hindi
        Kannada
    The following should be evaluated:
        Query understanding
        Retrieval relevance
        Response language
        Answer correctness
        Response consistency
    Experimental comparison
    The PoC should support the three planned experimental conditions:

    Experiment	                        Retrieval	                    Purpose
    Experiment A - LLM-only	             No	                        Establish baseline
    Experiment B - RAG	                Same-language retrieval	    Evaluate grounded generation
    Experiment C - Cross-lingual RAG	Cross-language retrieval	Evaluate multilingual retrieval

    The experiments can be evaluated using the project's planned metrics:
            Precision
            Recall
            Word Error Rate (WER) for voice input
            Hallucination percentage

    Additional engineering measurements such as response latency can also be recorded.
## 13. Conclusion
    Spring AI provides a suitable integration layer for connecting the KrishiVaani Spring Boot backend with LLMs, embedding models, and vector stores.

    The most relevant components for KrishiVaani are:
        ChatClient for LLM interaction
        ChatModel for chat model abstraction
        EmbeddingModel for vector generation
        VectorStore for vector storage and retrieval
        Advisors and retrieval mechanisms for RAG integration
        Document abstractions for knowledge ingestion

    The proposed architecture uses PostgreSQL with PGVector as the vector storage layer and Spring AI as the AI integration layer.

    The research also identifies multilingual embeddings and cross-lingual retrieval as important areas requiring experimental verification.

        Spring AI should therefore be integrated incrementally:

        Basic LLM Integration
                |
                v
        Embedding Integration
                |
                v
        PGVector Integration
                |
                v
        RAG Integration
                |
                v
        Multilingual RAG
                |
                v
        Experimental Evaluation

    The final effectiveness of the system should be determined through the planned experiments rather than assumed from the framework capabilities alone.
## 14. References

    Spring AI Official Documentation
    https://docs.spring.io/spring-ai/reference/
    Spring AI GitHub Repository
    https://github.com/spring-projects/spring-ai
    Spring Boot Documentation
    https://docs.spring.io/spring-boot/
    PostgreSQL Documentation
    https://www.postgresql.org/docs/
    PGVector
    https://github.com/pgvector/pgvector
    Retrieval-Augmented Generation
    Lewis, P. et al. "Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks", 2020.
    Whisper
    Radford, A. et al. "Robust Speech Recognition via Large-Scale Weak Supervision", 2022.
