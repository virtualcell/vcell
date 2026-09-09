<h1 align="center">VCell AI Platform</h1>

An AI-powered platform for discovering, analyzing, and exploring biomodels from the VCell database. Built as part of Google Summer of Code (GSoC), this project combines modern web technologies with AI capabilities to provide an intuitive interface for scientific model research.

## Features
- **AI-Powered Chatbot**: Natural language interface for querying and analyzing biomodels using LLMs with tool calling
- **Comprehensive Biomodel Search**: Advanced search capabilities with filtering and sorting options using the VCell API
- **Visual Diagrams**: biomodel diagrams and visualizations
- **Knowledge Base**: Vector-based document storage and retrieval using Qdrant
- **Modern UI**: Beautiful interface built with Next.js and Tailwind CSS

## Architecture
This is a monolithic repository containing both frontend and backend services:
- **Frontend**: Next.js 15 with TypeScript, Tailwind CSS, and Radix UI components
- **Backend**: FastAPI with Python 3.12+, Poetry for dependency management
- **Vector Database**: Qdrant for knowledge base storage and retrieval
- **Containerization**: Docker and Docker Compose

## Project Structure
```
VCell-GSoC/
├── frontend/               # Next.js frontend application
│   ├── app/                  # App router pages and components
│   ├── components/           # Reusable UI components
│   ├── hooks/                # Custom React hooks
│   └── lib/                  # Utility functions and configurations
├── backend/                # FastAPI backend application
│   ├── app/                  # Main application code
│   │   ├── routes/             # API route definitions
│   │   ├── controllers/        # Business logic controllers
│   │   ├── services/           # External service integrations
│   │   └── schemas/            # Pydantic data models
│   └── tests/                # Backend test suite
└── docker-compose.yml      # Multi-service container orchestration
```

## Tech Stack
### Frontend
- **Framework**: Next.js
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: Shadcn UI
- **Markdown**: React Markdown with KaTeX for math rendering

### Backend
- **Framework**: FastAPI
- **Language**: Python 3.12+
- **Dependency Management**: Poetry
- **Database**: Qdrant Vector Database
- **AI/ML**: OpenAI API, LangChain
- **File Processing**: PyPDF, Markitdown
- **Testing**: Pytest

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Vector Database**: Qdrant
- **Authentication**: Auth0
- **API Documentation**: FastAPI auto-generated docs

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ (for local development)
- Python 3.12+ (for local development)

### Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/virtualcell/VCell-AI.git
   cd VCell-GSoC
   ```

2. **Set up environment variables**
   ```bash
   # Copy and configure environment files
   cp backend/.env.example backend/.env
   cp frontend/.env.example frontend/.env
   ```

3. **Start all services**
   ```bash
   docker compose up --build -d
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8000
   - API Documentation (SWAGGER): http://localhost:8000/docs
   - Qdrant Dashboard: http://localhost:6333/dashboard

### Local Development

#### Backend Setup
```bash
cd backend
poetry install --no-root
poetry run uvicorn app.main:app --port 8000
# poetry run uvicorn app.main:app --reload
```

#### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### LLM calls
 - The system prompt for all LLM queries is defined in backend/app/utils/system_prompt.py
 - LLM queries in Search page are defined at frontend/app/search/[bmid]/page.tsx, value is prefixed with the text defined in the ChatBox component in the same file.
 -  LLM queries in Chat page are defined at frontend/app/chat/[bmid]/page.tsx, value is prefixed with the text defined in the ChatBox component in the same file.
 -  The returned results are formatted in frontend/components, e.g. hyperlinks to model details are inserted in frontend/components/ChatBox.tsx
 -  The RAG is using the knowledge base populated by backend/populate_db.ipynb


## 📄 License
This project is part of Google Summer of Code and is licensed under the MIT License.
