cd papersense-project\papersense-backend# PaperSense

**AI-Powered Research Paper Understanding & Research Gap Discovery Platform**

PaperSense lets you upload a research paper PDF and get an AI-generated breakdown of the paper — summary, problem statement, methodology, strengths, limitations — plus its flagship feature: the **Research Gap Finder**, which highlights unexplored weaknesses in the paper and suggests meaningful future research directions.

---

## 1. Project Overview

Researchers and students often spend hours reading a paper just to figure out what it actually contributes and where its weaknesses lie. PaperSense automates that first pass:

1. You upload a PDF.
2. The backend extracts the text (Apache PDFBox).
3. The text is sent to an AI model with a structured prompt.
4. The AI returns a structured analysis, which is saved and displayed in a dashboard.

This is a monolithic, beginner-friendly Java 17 + Spring Boot + React project — no microservices, no message queues, no reactive programming, no vector databases. Just controllers, services, repositories, and models.

---

## 2. Features

- 📄 **PDF Upload** — drag & drop or click to upload, with validation (PDF-only, size limit).
- 📋 **Paper Overview** — plain-language summary of what the paper is about.
- 🎯 **Problem Statement** — the core problem the paper addresses.
- 🛠️ **Technologies / AI Techniques** — extracted list of methods used.
- 🧠 **AI Explanation** — beginner-friendly explanation of the AI/ML techniques.
- 🔬 **Methodology** — how the authors approached the problem.
- ✅ **Strengths** — the paper's advantages.
- ⚠️ **Limitations** — the paper's weaknesses.
- 🔍 **Research Gap Finder** *(flagship feature)* — critical analysis of dataset limitations, sample size, missing comparisons, reproducibility issues, and more.
- 💡 **Suggested Improvements** — concrete suggestions for what could be changed.
- 🚀 **Future Research Directions** — AI-suggested next steps building on the paper.
- 🗂️ **Paper History** — view, re-analyze, or delete previously uploaded papers.
- 📦 **Large PDF handling** — long papers are automatically split into chunks and the analyses are merged.

---

## 3. Architecture

```
Frontend (React + Vite)
        │
        ▼
   REST API (JSON)
        │
        ▼
Spring Boot Backend
  ├── Controller     (PaperController, AnalysisController)
  ├── Service        (PaperService, PdfService, AIService)
  ├── Repository     (PaperRepository, AnalysisRepository)
  ├── Model          (Paper, Analysis)
  ├── DTO            (UploadResponse, AnalysisResponse, PaperSummary)
  └── Exception      (GlobalExceptionHandler + custom exceptions)
        │
        ▼
     MySQL
```

**Processing flow:**

```
User uploads PDF
    → PaperController.uploadPaper()
    → PaperService.uploadPaper()
    → PdfService.validateFile() + saveFile() + extractText()
    → Paper saved to MySQL (with extracted text)
    → uploadResponse returned

User clicks "Analyze"
    → PaperController.analyzePaper()
    → PaperService.analyzePaper()
    → AIService.analyzePaper()  (chunking if needed)
    → LLM API called
    → JSON parsed into AnalysisResult
    → Analysis saved to MySQL
    → AnalysisResponse returned
    → React dashboard renders the cards
```

---

## 4. Technology Stack

| Layer      | Technology                                   |
|------------|-----------------------------------------------|
| Backend    | Java 17, Spring Boot 3.3, Gradle (Gradle Wrapper) |
| Database   | MySQL, Spring Data JPA / Hibernate             |
| PDF        | Apache PDFBox 2.0.31                           |
| AI         | External LLM API (Anthropic Messages API by default) |
| Frontend   | React 18, Vite 5, React Router 6, plain CSS    |

---

## 5. Folder Structure

```
papersense-backend/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/main/
    ├── java/com/papersense/
    │   ├── PaperSenseApplication.java
    │   ├── config/
    │   │   └── WebConfig.java            (CORS)
    │   ├── controller/
    │   │   ├── PaperController.java
    │   │   └── AnalysisController.java
    │   ├── service/
    │   │   ├── PaperService.java
    │   │   ├── PdfService.java
    │   │   └── AIService.java
    │   ├── model/
    │   │   ├── Paper.java
    │   │   └── Analysis.java
    │   ├── repository/
    │   │   ├── PaperRepository.java
    │   │   └── AnalysisRepository.java
    │   ├── dto/
    │   │   ├── UploadResponse.java
    │   │   ├── AnalysisResponse.java
    │   │   └── PaperSummary.java
    │   └── exception/
    │       ├── GlobalExceptionHandler.java
    │       ├── PaperSenseException.java
    │       ├── InvalidFileException.java
    │       ├── ResourceNotFoundException.java
    │       ├── PdfProcessingException.java
    │       └── AIServiceException.java
    └── resources/
        └── application.properties

papersense-frontend/
├── package.json
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx
    ├── App.jsx
    ├── api/
    │   └── papersenseApi.js
    ├── components/
    │   ├── Home.jsx
    │   ├── UploadPaper.jsx
    │   ├── Processing.jsx
    │   ├── AnalysisDashboard.jsx
    │   └── PaperHistory.jsx
    └── styles/
        └── global.css
```

---

## 6. Database Setup

PaperSense uses MySQL with two tables, automatically created by Hibernate (`ddl-auto=update`) the first time the backend runs.

1. Install MySQL and make sure it's running.
2. Create the database (optional — the backend will auto-create it via `createDatabaseIfNotExist=true`, but you can also do it manually):

```sql
CREATE DATABASE papersense_db;
```

**Tables:**

`papers`
| Column         | Type          |
|----------------|---------------|
| id             | BIGINT, PK    |
| title          | VARCHAR       |
| file_name      | VARCHAR       |
| file_path      | VARCHAR       |
| extracted_text | LONGTEXT      |
| uploaded_at    | DATETIME      |

`analyses`
| Column                  | Type       |
|-------------------------|------------|
| id                      | BIGINT, PK |
| paper_id                | BIGINT, FK → papers.id |
| summary                 | LONGTEXT   |
| problem_statement       | LONGTEXT   |
| technologies            | LONGTEXT (JSON array string) |
| ai_explanation          | LONGTEXT   |
| methodology             | LONGTEXT   |
| advantages              | LONGTEXT (JSON array string) |
| limitations             | LONGTEXT (JSON array string) |
| research_gaps           | LONGTEXT (JSON array string) |
| suggested_improvements  | LONGTEXT (JSON array string) |
| future_scope            | LONGTEXT (JSON array string) |
| created_at              | DATETIME   |

---

## 7. Backend Setup

**Requirements:** Java 17, Gradle Wrapper (included with project), MySQL running locally.

1. Navigate to the backend folder:
   ```bash
   cd papersense-backend
   ```

2. Set your environment variables (see [Environment Variables](#9-environment-variables) below).

3. Build and run:

   **Windows (PowerShell / Command Prompt):**
   ```powershell
   .\gradlew.bat build
   .\gradlew.bat bootRun
   ```

   **Linux / macOS:**
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

   To run tests:
   ```powershell
   .\gradlew.bat test
   ```

   The backend starts on **http://localhost:8080**.

4. Uploaded PDFs are stored in an `uploads/` folder created next to where you run the app.

---

## 8. Frontend Setup

**Requirements:** Node.js 18+.

1. Navigate to the frontend folder:
   ```bash
   cd papersense-frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the dev server:
   ```bash
   npm run dev
   ```

   The frontend starts on **http://localhost:5173** and calls the backend at `http://localhost:8080/api` (configured in `src/api/papersenseApi.js`).

---

## 9. Environment Variables

Set these before starting the backend. Never commit real values to source control.

| Variable       | Required | Description                                             | Default |
|----------------|----------|-----------------------------------------------------------|---------|
| `AI_API_KEY`   | Yes      | Your LLM API key.                                          | *(none)* |
| `AI_API_URL`   | No       | LLM API endpoint.                                          | `https://api.anthropic.com/v1/messages` |
| `AI_API_MODEL` | No       | Model name to use.                                         | `claude-sonnet-4-6` |
| `DB_USERNAME`  | No       | MySQL username.                                            | `root` |
| `DB_PASSWORD`  | No       | MySQL password.                                            | `root` |

Example (Linux/Mac):
```bash
export AI_API_KEY=your_real_api_key_here
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
```

Example (Windows PowerShell):
```powershell
$env:AI_API_KEY="your_real_api_key_here"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
```

These map into `application.properties` via `${AI_API_KEY}` style placeholders — the key is **never** hardcoded and **never** sent to the frontend.

---

## 10. How to Run (Quick Start)

```bash
# Terminal 1 — MySQL should already be running

# Terminal 2 — backend
cd papersense-backend
# Set your AI API key (Windows: $env:AI_API_KEY="your_key_here", Linux/Mac: export AI_API_KEY="your_key_here")
.\gradlew.bat bootRun

# Terminal 3 — frontend
cd papersense-frontend
npm install
npm run dev
```

Then open **http://localhost:5173** in your browser.

---

## 11. API Endpoints

| Method | Endpoint                     | Description                          |
|--------|-------------------------------|---------------------------------------|
| POST   | `/api/papers/upload`          | Upload a PDF (`multipart/form-data`, field name `paper`) |
| POST   | `/api/papers/{id}/analyze`    | Run AI analysis on an uploaded paper  |
| GET    | `/api/papers/{id}/analysis`   | Get the saved analysis for a paper    |
| GET    | `/api/papers`                 | List all uploaded papers              |
| DELETE | `/api/papers/{id}`            | Delete a paper and its analysis       |
| GET    | `/api/analyses/{analysisId}`  | Look up an analysis by its own id     |

---

## 12. Example API Requests

**Upload a paper (curl):**
```bash
curl -X POST http://localhost:8080/api/papers/upload \
  -F "paper=@/path/to/paper.pdf"
```

Response:
```json
{
  "paperId": 1,
  "fileName": "paper.pdf",
  "message": "Paper uploaded successfully"
}
```

**Analyze the paper:**
```bash
curl -X POST http://localhost:8080/api/papers/1/analyze
```

**Get the analysis:**
```bash
curl http://localhost:8080/api/papers/1/analysis
```

**List all papers:**
```bash
curl http://localhost:8080/api/papers
```

**Delete a paper:**
```bash
curl -X DELETE http://localhost:8080/api/papers/1
```

---

## 13. Example AI Response

After calling `/api/papers/1/analyze`, you get back something like:

```json
{
  "paperId": 1,
  "summary": "This paper proposes a convolutional neural network approach for classifying satellite images into land-use categories...",
  "problemStatement": "Existing land-use classification methods struggle with limited labeled data and poor generalization across regions.",
  "technologies": ["Convolutional Neural Networks", "Transfer Learning", "ResNet-50"],
  "aiExplanation": "A CNN is a type of neural network especially good at recognizing patterns in images by scanning them with small filters that detect edges, textures, and shapes.",
  "methodology": "The authors fine-tuned a pretrained ResNet-50 model on a labeled satellite image dataset, using data augmentation and a train/validation/test split of 70/15/15.",
  "advantages": ["Achieves high accuracy compared to baseline CNNs", "Uses transfer learning to reduce training time"],
  "limitations": ["Evaluated on a single geographic region", "No comparison against more recent transformer-based models"],
  "researchGaps": [
    "The dataset only covers one country, so generalization to other regions is untested",
    "No ablation study isolating the effect of data augmentation",
    "Missing comparison against Vision Transformer baselines",
    "Reproducibility is limited since hyperparameters are not fully specified"
  ],
  "suggestedImprovements": [
    "Evaluate the model on satellite imagery from multiple continents to test generalization",
    "Add a baseline comparison against at least one transformer-based image classifier"
  ],
  "futureScope": [
    "Extend the approach to multi-label classification for mixed land-use areas",
    "Explore self-supervised pretraining to reduce reliance on labeled data"
  ]
}
```

---

## 14. Troubleshooting

| Problem | Likely Cause | Fix |
|---|---|---|
| Backend fails to start with a DB connection error | MySQL isn't running, or wrong credentials | Confirm MySQL is running and `DB_USERNAME` / `DB_PASSWORD` are correct |
| `"AI API key is not configured"` error | `AI_API_KEY` not set | Export the environment variable before starting the backend |
| `"Only PDF files are supported."` | Uploaded a non-PDF file | Upload a `.pdf` file |
| `"No readable text could be extracted from this PDF."` | The PDF is a scanned image with no text layer | Use a text-based PDF, or run OCR first (out of scope for v1) |
| Frontend shows network/CORS errors | Backend not running, or CORS origin mismatch | Make sure the backend is running on port 8080 and the frontend on 5173 (see `WebConfig.java`) |
| `"AI API returned an error (status 401)"` | Invalid or missing API key | Double-check `AI_API_KEY` |
| Analysis takes a long time on large papers | Expected — large papers are split into chunks and analyzed sequentially | Wait for it to finish; this is by design (see Large PDF Handling) |
| `MaxUploadSizeExceededException` | File over 15MB | Upload a smaller PDF, or raise `spring.servlet.multipart.max-file-size` |

---

## 15. Future Improvements

- OCR support for scanned/image-based PDFs.
- User accounts and per-user paper libraries.
- Side-by-side comparison of multiple papers.
- Exportable PDF/Word reports of the analysis.
- Citation graph / related-work discovery.
- Caching AI responses to avoid re-analyzing unchanged papers.
- Streaming the AI response to the frontend for faster perceived performance on large papers.

---

## API Testing with Postman

1. Create a new request: `POST http://localhost:8080/api/papers/upload`
2. Body → `form-data` → key `paper`, type `File`, value: select your PDF.
3. Send. Copy the `paperId` from the response.
4. New request: `POST http://localhost:8080/api/papers/{paperId}/analyze` (no body needed).
5. New request: `GET http://localhost:8080/api/papers/{paperId}/analysis` to re-fetch the saved result any time.
6. `GET http://localhost:8080/api/papers` to see all papers.
7. `DELETE http://localhost:8080/api/papers/{paperId}` to remove a paper.

## End-to-End Testing Checklist

- [ ] Backend starts without errors and connects to MySQL.
- [ ] Frontend starts and loads the Home page.
- [ ] Uploading a non-PDF file shows a clear error.
- [ ] Uploading a valid PDF succeeds and shows file name/size/status.
- [ ] Clicking "Analyze" shows the loading/processing state.
- [ ] Analysis dashboard renders all 9 sections, with Research Gaps visually emphasized.
- [ ] Paper History lists the uploaded paper with correct "Analyzed" status.
- [ ] Re-visiting an analyzed paper from History loads the saved analysis (no re-call to AI).
- [ ] Deleting a paper removes it from History and from the database.
- [ ] Uploading a very large PDF triggers chunked analysis and still returns a complete result.
