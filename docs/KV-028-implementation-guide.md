# KV-028 — Configure Spring Data JPA
**Assignee:** Shantanu | **Priority:** Medium | **Status:** TO DO | **Points:** 3

> Audited against current codebase on 2026-10-03.

---

## What's Already Done (Don't Redo)

| Item | Status |
|------|--------|
| `spring-boot-starter-data-jpa` dependency in `pom.xml` | ✅ Done |
| `postgresql` driver in `pom.xml` | ✅ Done |
| `spring.datasource.*` config in `application.properties` | ✅ Done |
| `spring.jpa.hibernate.ddl-auto=update` | ✅ Done |
| `spring.jpa.show-sql=true` + `format_sql=true` | ✅ Done |
| `spring.jpa.open-in-view=false` | ✅ Done |
| `DatabaseConfig.java` with `@EnableJpaRepositories` + `@EnableTransactionManagement` | ✅ Done |
| `Question` entity with `@Entity`, `@Table`, `@Column`, `@PrePersist`, `@PreUpdate` | ✅ Done |
| `AgriculturalDocument` entity with `@Entity`, `@Table`, `@Column`, `@PrePersist` | ✅ Done |
| `QuestionRepository extends JpaRepository<Question, Long>` | ✅ Done |
| `AgriculturalDocumentRepository` with 2 custom query methods | ✅ Done |

---

## What KV-028 Still Requires

| # | Task | Why needed |
|---|------|------------|
| 1 | Add `@Index` on `Question.language` column | Queries will filter by language in the AI sprint — add it now |
| 2 | Add `@Index` on `AgriculturalDocument.category` column | `findByCategory()` is already defined in repo but has no DB index |
| 3 | Add custom query methods to `QuestionRepository` | Currently bare — needs `findByLanguage()` and `findByAnswerIsNull()` for RAG pipeline |
| 4 | Fix `ResourceNotFoundException` import in `QuestionService` | Fully-qualified class name is messy — replace with a real import |
| 5 | Add `application-test.properties` with H2 for tests | Without it, tests need a live DB to run |
| 6 | Verify `./mvnw compile` succeeds | Confirm no compile errors before marking done |

---

## Step 1 — Add `@Index` on `Question` entity

Open [`Question.java`](file:///media/shantanu-singh/CE0887F40887DA3B/Important_Files/Mini_Project/KrishiVaani%20AI/backend/src/main/java/com/example/krishivaanibackend/entity/Question.java)

**Change** the `@Table` annotation from:
```java
@Table(name = "questions")
```
**To:**
```java
@Table(
    name = "questions",
    indexes = {
        @Index(name = "idx_questions_language", columnList = "language"),
        @Index(name = "idx_questions_created_at", columnList = "created_at")
    }
)
```

Also add this import if not already present:
```java
import jakarta.persistence.Index;
```
(It's inside `jakarta.persistence.*` so if you already have `import jakarta.persistence.*;` it's already covered.)

---

## Step 2 — Add `@Index` on `AgriculturalDocument` entity

Open [`AgriculturalDocument.java`](file:///media/shantanu-singh/CE0887F40887DA3B/Important_Files/Mini_Project/KrishiVaani%20AI/backend/src/main/java/com/example/krishivaanibackend/entity/AgriculturalDocument.java)

**Change** the `@Table` annotation from:
```java
@Table(name = "agricultural_documents")
```
**To:**
```java
@Table(
    name = "agricultural_documents",
    indexes = {
        @Index(name = "idx_agrdoc_category", columnList = "category"),
        @Index(name = "idx_agrdoc_created_at", columnList = "created_at")
    }
)
```

---

## Step 3 — Add custom query methods to `QuestionRepository`

Open [`QuestionRepository.java`](file:///media/shantanu-singh/CE0887F40887DA3B/Important_Files/Mini_Project/KrishiVaani%20AI/backend/src/main/java/com/example/krishivaanibackend/repository/QuestionRepository.java)

**Replace the entire file** with:
```java
package com.example.krishivaanibackend.repository;

import com.example.krishivaanibackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Question entities.
 * Provides CRUD and custom query methods for the questions table.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** Find all questions submitted in a specific language (e.g. "hi", "en") */
    List<Question> findByLanguage(String language);

    /** Find all questions that have not yet received an AI answer */
    List<Question> findByAnswerIsNull();

    /** Find all questions that already have an AI answer */
    List<Question> findByAnswerIsNotNull();
}
```

---

## Step 4 — Fix `ResourceNotFoundException` import in `QuestionService`

Open [`QuestionService.java`](file:///media/shantanu-singh/CE0887F40887DA3B/Important_Files/Mini_Project/KrishiVaani%20AI/backend/src/main/java/com/example/krishivaanibackend/service/QuestionService.java)

**Add** this import at the top (with the other imports):
```java
import com.example.krishivaanibackend.exception.ResourceNotFoundException;
```

**Then replace** in the `updateAnswer()` method:
```java
// Before (ugly fully-qualified name):
.orElseThrow(() -> new com.example.krishivaanibackend.exception.ResourceNotFoundException("Question", id));

// After (clean):
.orElseThrow(() -> new ResourceNotFoundException("Question", id));
```

---

## Step 5 — Add `application-test.properties` for H2 in-memory test DB

**Create a new file:** `src/test/resources/application-test.properties`

```properties
# ── Test datasource (H2 in-memory, no live DB needed) ───────
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=false
```

Also add H2 dependency to `pom.xml` (inside `<dependencies>`):
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Step 6 — Verify: Compile + Start

```bash
cd backend

# 1. Compile — must succeed with no errors
./mvnw compile

# 2. Start (with env vars set)
export DB_URL="jdbc:postgresql://<your-neon-host>/neondb?sslmode=require&channel_binding=require"
export DB_USERNAME="neondb_owner"
export DB_PASSWORD="<your-password>"
./mvnw spring-boot:run
```

On startup, check the logs for:
- `Hibernate: create table questions ...` — tables created ✅
- `Hibernate: create index idx_questions_language ...` — indexes created ✅
- No `ERROR` lines ✅

---

## Files Changed

```
entity/
  Question.java                 ← add @Index on language + created_at
  AgriculturalDocument.java     ← add @Index on category + created_at

repository/
  QuestionRepository.java       ← add 3 custom query methods

service/
  QuestionService.java          ← fix ResourceNotFoundException import

src/test/resources/
  application-test.properties   ← NEW (H2 test config)

pom.xml                         ← add H2 test dependency
```

---

## Completion Checklist

- [ ] `@Index` added to `Question` entity (`language`, `created_at`)
- [ ] `@Index` added to `AgriculturalDocument` entity (`category`, `created_at`)
- [ ] `QuestionRepository` has `findByLanguage()`, `findByAnswerIsNull()`, `findByAnswerIsNotNull()`
- [ ] `ResourceNotFoundException` import cleaned up in `QuestionService`
- [ ] `src/test/resources/application-test.properties` created with H2 config
- [ ] `h2` test dependency added to `pom.xml`
- [ ] `./mvnw compile` → BUILD SUCCESS
- [ ] App starts, Hibernate creates indexes in logs
