<div align="center">

# 🎵 Harmony — Music Streaming Platform

**A cloud-native, microservice-based music streaming platform built with Spring Boot, React, Kafka, and Eureka.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://reactjs.org/)
[![Apache Kafka](https://img.shields.io/badge/Kafka-Event%20Streaming-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

</div>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Data Flow Diagram](#-data-flow-diagram)
- [Microservices](#-microservices)
- [API Reference](#-api-reference)
- [Swagger UI](#-swagger-ui)
- [Frontend](#-frontend)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)

---

## 🌟 Overview

Harmony is a full-stack music streaming platform that demonstrates modern microservice architecture patterns. Users can search for songs, stream audio, create playlists, and receive intelligent recommendations — all powered by an event-driven backend.

### ✨ Key Highlights

- **Microservice Architecture** — 6 independently deployable services communicating via REST and Kafka
- **Event-Driven Recommendations** — Real-time listening analytics fed through Apache Kafka
- **Service Discovery** — Netflix Eureka for dynamic service registration
- **Centralized Config** — Spring Cloud Config Server for externalized configuration
- **API Gateway** — Single entry point with Spring Cloud Gateway MVC
- **Unified API Docs** — Aggregated Swagger UI across all services

---

## 🏗 Architecture

```mermaid
graph TB
    subgraph CLIENT["CLIENT LAYER"]
        FE["React Frontend\n Vite - Port 5173"]
    end

    subgraph GATEWAY["API GATEWAY LAYER"]
        GW["API Gateway\n Port 8080"]
    end

    subgraph INFRA["INFRASTRUCTURE"]
        EUR["Eureka Discovery\n Port 8761"]
        CFG["Config Server\n Port 8888"]
        KFK["Apache Kafka\n Port 9092"]
        ZK["Zookeeper\n Port 2181"]
    end

    subgraph SERVICES["BUSINESS SERVICES"]
        MS["Music Search Service\n Port 8081"]
        SS["Streaming Service\n Port 8082"]
        PS["Playlist Service\n Port 8083"]
        RS["Recommendation Service\n Port 8084"]
    end

    subgraph EXTERNAL["EXTERNAL APIs"]
        YT["YouTube Data API"]
    end

    subgraph DATA["DATA STORES"]
        H2["H2 Database\n In-Memory"]
    end

    FE -->|"HTTP REST"| GW
    GW -->|"/music/**"| MS
    GW -->|"/stream/**"| SS
    GW -->|"/playlist/**"| PS
    GW -->|"/recommendation/**"| RS

    MS -->|"Search"| YT
    SS -->|"Stream URL"| YT
    SS -->|"Publish Events"| KFK
    RS -->|"Consume Events"| KFK
    PS -->|"CRUD"| H2
    KFK --> ZK

    MS -.->|"Register"| EUR
    SS -.->|"Register"| EUR
    PS -.->|"Register"| EUR
    RS -.->|"Register"| EUR
    GW -.->|"Register"| EUR

    MS -.->|"Fetch Config"| CFG
    SS -.->|"Fetch Config"| CFG
    PS -.->|"Fetch Config"| CFG
    RS -.->|"Fetch Config"| CFG

    style CLIENT fill:#e8f4fd,stroke:#333,stroke-width:2px,color:#000
    style GATEWAY fill:#fce4ec,stroke:#333,stroke-width:2px,color:#000
    style SERVICES fill:#e8f5e9,stroke:#333,stroke-width:2px,color:#000
    style INFRA fill:#f3e5f5,stroke:#333,stroke-width:2px,color:#000
    style EXTERNAL fill:#fff3e0,stroke:#333,stroke-width:2px,color:#000
    style DATA fill:#fce4ec,stroke:#333,stroke-width:2px,color:#000

    style FE fill:#42a5f5,stroke:#000,stroke-width:2px,color:#000
    style GW fill:#ef5350,stroke:#000,stroke-width:2px,color:#000
    style MS fill:#66bb6a,stroke:#000,stroke-width:2px,color:#000
    style SS fill:#66bb6a,stroke:#000,stroke-width:2px,color:#000
    style PS fill:#66bb6a,stroke:#000,stroke-width:2px,color:#000
    style RS fill:#66bb6a,stroke:#000,stroke-width:2px,color:#000
    style EUR fill:#ab47bc,stroke:#000,stroke-width:2px,color:#000
    style CFG fill:#ab47bc,stroke:#000,stroke-width:2px,color:#000
    style KFK fill:#ffa726,stroke:#000,stroke-width:2px,color:#000
    style ZK fill:#ffa726,stroke:#000,stroke-width:2px,color:#000
    style YT fill:#ef5350,stroke:#000,stroke-width:2px,color:#000
    style H2 fill:#ab47bc,stroke:#000,stroke-width:2px,color:#000
```

---

## 🔄 Data Flow Diagram

The diagram below traces how data flows through the system for core user operations, showing every API endpoint involved.

```mermaid
flowchart LR
    subgraph USER["USER ACTIONS"]
        U1["Search Song"]
        U2["Play Song"]
        U3["Skip to Next"]
        U4["Create Playlist"]
        U5["Add to Playlist"]
    end

    subgraph GATEWAY["API GATEWAY :8080"]
        G1["Route Request"]
    end

    subgraph SEARCH["MUSIC SEARCH :8081"]
        S1["GET /music/search?q=query"]
    end

    subgraph STREAM["STREAMING :8082"]
        ST1["GET /stream/videoId"]
        ST2["Publish SongPlayEvent"]
    end

    subgraph KAFKA["KAFKA BROKER"]
        K1["song-play-events topic"]
    end

    subgraph RECOMMEND["RECOMMENDATION :8084"]
        R1["Consume SongPlayEvent"]
        R2["Build Transition Graph"]
        R3["GET /recommendation/top"]
        R4["GET /recommendation/next/id"]
    end

    subgraph PLAYLIST["PLAYLIST :8083"]
        P1["POST /playlist"]
        P2["GET /playlist"]
        P3["GET /playlist/id"]
        P4["POST /playlist/id/songs/songId"]
    end

    subgraph YOUTUBE["YOUTUBE API"]
        YT1["Search Results"]
        YT2["Stream URL"]
    end

    U1 --> G1 --> S1 -->|"query"| YT1
    YT1 -->|"SongResponse[]"| S1

    U2 --> G1 --> ST1 -->|"videoId"| YT2
    YT2 -->|"streamUrl"| ST1
    ST1 -->|"previousVideoId"| ST2 --> K1

    K1 --> R1 --> R2
    U3 --> G1 --> R4
    R4 -->|"nextVideoId"| G1

    U4 --> G1 --> P1
    U5 --> G1 --> P4

    G1 --> R3
    G1 --> P2
    G1 --> P3

    style USER fill:#e8f4fd,stroke:#333,stroke-width:2px,color:#000
    style GATEWAY fill:#fce4ec,stroke:#333,stroke-width:2px,color:#000
    style SEARCH fill:#e8f5e9,stroke:#333,stroke-width:2px,color:#000
    style STREAM fill:#e8f5e9,stroke:#333,stroke-width:2px,color:#000
    style KAFKA fill:#fff3e0,stroke:#333,stroke-width:2px,color:#000
    style RECOMMEND fill:#e8f5e9,stroke:#333,stroke-width:2px,color:#000
    style PLAYLIST fill:#e8f5e9,stroke:#333,stroke-width:2px,color:#000
    style YOUTUBE fill:#fff3e0,stroke:#333,stroke-width:2px,color:#000

    style U1 fill:#42a5f5,stroke:#000,stroke-width:1px,color:#000
    style U2 fill:#42a5f5,stroke:#000,stroke-width:1px,color:#000
    style U3 fill:#42a5f5,stroke:#000,stroke-width:1px,color:#000
    style U4 fill:#42a5f5,stroke:#000,stroke-width:1px,color:#000
    style U5 fill:#42a5f5,stroke:#000,stroke-width:1px,color:#000
    style G1 fill:#ef5350,stroke:#000,stroke-width:1px,color:#000
    style S1 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style ST1 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style ST2 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style K1 fill:#ffa726,stroke:#000,stroke-width:1px,color:#000
    style R1 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style R2 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style R3 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style R4 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style P1 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style P2 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style P3 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style P4 fill:#66bb6a,stroke:#000,stroke-width:1px,color:#000
    style YT1 fill:#ef5350,stroke:#000,stroke-width:1px,color:#000
    style YT2 fill:#ef5350,stroke:#000,stroke-width:1px,color:#000
```

### 📋 Flow Breakdown

| # | User Action | API Calls (in order) | Data Flow |
|---|---|---|---|
| 1 | **Search for a song** | `GET /music/search?q=...` | Frontend → Gateway → Music Search → YouTube API → Response |
| 2 | **Play a song** | `GET /stream/{videoId}?previousVideoId=...` | Frontend → Gateway → Streaming → YouTube (stream URL) + Kafka (event) |
| 3 | **Skip to next song** | `GET /recommendation/next/{currentVideoId}` → `GET /music/search?q={nextId}` → `GET /stream/{nextId}` | Frontend → Gateway → Recommendation → (enrich via Search) → Stream |
| 4 | **View trending** | `GET /recommendation/top?limit=4` → `GET /music/search?q={id}` ×4 | Frontend → Gateway → Recommendation → (enrich each via Search) |
| 5 | **Create playlist** | `POST /playlist` | Frontend → Gateway → Playlist Service → H2 DB |
| 6 | **Add song to playlist** | `POST /playlist/{id}/songs/{songId}` | Frontend → Gateway → Playlist Service → H2 DB |
| 7 | **View playlist** | `GET /playlist/{id}` → `GET /music/search?q={songId}` ×N | Frontend → Gateway → Playlist → (enrich each via Search) |

---

## 🎤 Microservices

### 1. Discovery Service (Eureka Server)
| Property | Value |
|---|---|
| **Port** | `8761` |
| **Role** | Service registry for all microservices |
| **Dashboard** | `http://localhost:8761` |

### 2. Config Service
| Property | Value |
|---|---|
| **Port** | `8888` |
| **Role** | Centralized configuration management |

### 3. API Gateway
| Property | Value |
|---|---|
| **Port** | `8080` |
| **Role** | Single entry point, request routing, Swagger aggregation |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |

### 4. Music Search Service
| Property | Value |
|---|---|
| **Port** | `8081` |
| **Role** | Song discovery via YouTube API |
| **Endpoints** | `GET /music/search?q={query}` |

### 5. Streaming Service
| Property | Value |
|---|---|
| **Port** | `8082` |
| **Role** | Audio stream resolution + Kafka event publishing |
| **Endpoints** | `GET /stream/{videoId}?previousVideoId={id}` |

### 6. Playlist Service
| Property | Value |
|---|---|
| **Port** | `8083` |
| **Role** | CRUD operations for playlists (H2 database) |
| **Endpoints** | `POST /playlist`, `GET /playlist`, `GET /playlist/{id}`, `POST /playlist/{id}/songs/{songId}` |

### 7. Recommendation Service
| Property | Value |
|---|---|
| **Port** | `8084` |
| **Role** | Real-time song recommendations via Kafka events |
| **Endpoints** | `GET /recommendation/top`, `GET /recommendation/next/{videoId}` |

---

## 📡 API Reference

### Music Search API

| Method | Endpoint | Description | Parameters |
|---|---|---|---|
| `GET` | `/music/search` | Search for songs | `q` — search query string (required) |

**Response:** `SongResponse[]`
```json
[
  {
    "id": "dQw4w9WgXcQ",
    "title": "Rick Astley - Never Gonna Give You Up",
    "uploader": "Rick Astley",
    "thumbnailUrl": "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg"
  }
]
```

### Streaming API

| Method | Endpoint | Description | Parameters |
|---|---|---|---|
| `GET` | `/stream/{videoId}` | Get audio stream URL | `videoId` (path), `previousVideoId` (query, optional) |

**Response:** `StreamResponse`
```json
{
  "videoId": "dQw4w9WgXcQ",
  "streamUrl": "https://rr3---sn-example.googlevideo.com/videoplayback?..."
}
```

### Playlist API

| Method | Endpoint | Description | Parameters |
|---|---|---|---|
| `POST` | `/playlist` | Create a new playlist | Body: `{ "name": "...", "songIds": [...] }` |
| `GET` | `/playlist` | Get all playlists | — |
| `GET` | `/playlist/{id}` | Get playlist by ID | `id` (path) |
| `POST` | `/playlist/{id}/songs/{songId}` | Add song to playlist | `id`, `songId` (path) |

### Recommendation API

| Method | Endpoint | Description | Parameters |
|---|---|---|---|
| `GET` | `/recommendation/top` | Get top recommended songs | `limit` (query, default: 10) |
| `GET` | `/recommendation/next/{currentVideoId}` | Predict next song | `currentVideoId` (path) |

---

## 📸 Swagger UI

All API documentation is aggregated through the API Gateway at **`http://localhost:8080/swagger-ui.html`**.

### Music Search API Documentation
![Swagger UI — Music Search API](docs/screenshots/swagger_music_search.png)

### Playlist API Documentation
![Swagger UI — Playlist API](docs/screenshots/swagger_playlist.png)

### Streaming API Documentation
![Swagger UI — Streaming API](docs/screenshots/swagger_streaming.png)

### Recommendation API Documentation
![Swagger UI — Recommendation API](docs/screenshots/swagger_recommendation.png)

---

## 🎨 Frontend

The frontend is a **React + Vite** application with a bold neo-brutalist design. It communicates exclusively through the API Gateway.

### Main Interface
![Harmony Neo — Frontend UI](docs/screenshots/frontend_ui.png)

### Frontend Features

- 🔍 **Search** — Full-text song search with thumbnail previews
- ▶️ **Stream** — In-browser audio playback with progress bar
- ⏭️ **Skip Forward** — AI-powered next-song prediction
- 📋 **Playlists** — Create and manage custom mixtapes
- 🔥 **Hot Rankings** — Real-time trending songs leaderboard
- ❤️ **Quick Save** — Add currently playing song to any playlist

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 18, Vite, Axios, Lucide Icons |
| **API Gateway** | Spring Cloud Gateway MVC |
| **Backend** | Spring Boot 3.4.3, Java 21 |
| **Service Discovery** | Netflix Eureka |
| **Configuration** | Spring Cloud Config |
| **Messaging** | Apache Kafka (Confluent 7.3.2) |
| **Database** | H2 (In-Memory) |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Build Tool** | Maven |
| **Containerization** | Docker Compose |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21+** (JDK)
- **Node.js 18+** & npm
- **Docker & Docker Compose** (for Kafka)

### 1. Start Infrastructure

```bash
# Start Kafka & Zookeeper
docker-compose up -d
```

### 2. Start Microservices (in order)

```bash
# 1. Discovery Service
cd discovery-service && ./mvnw spring-boot:run

# 2. Config Service
cd config-service && ./mvnw spring-boot:run

# 3. Business Services (can start in parallel)
cd music-search-service && ./mvnw spring-boot:run
cd streaming-service && ./mvnw spring-boot:run
cd playlist-service && ./mvnw spring-boot:run
cd recommendation-service && ./mvnw spring-boot:run

# 4. API Gateway
cd api-gateway && ./mvnw spring-boot:run
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

### 4. Access the Application

| Component | URL |
|---|---|
| 🎵 **Frontend** | http://localhost:5173 |
| 🚪 **API Gateway** | http://localhost:8080 |
| 📖 **Swagger UI** | http://localhost:8080/swagger-ui.html |
| 🔍 **Eureka Dashboard** | http://localhost:8761 |

---

## 📁 Project Structure

```
Harmony/
├── api-gateway/                  # Spring Cloud Gateway MVC
├── config-service/               # Spring Cloud Config Server
├── discovery-service/            # Netflix Eureka Server
├── music-search-service/         # YouTube song search API
├── streaming-service/            # Audio streaming + Kafka producer
├── playlist-service/             # Playlist CRUD + H2 database
├── recommendation-service/       # Kafka consumer + prediction engine
├── frontend/                     # React + Vite frontend
├── docker-compose.yml            # Kafka & Zookeeper containers
├── docs/
│   └── screenshots/              # Swagger & frontend screenshots
└── README.md
```

---

<div align="center">

**Built with ❤️ using Spring Boot, React, and Kafka**

</div>
