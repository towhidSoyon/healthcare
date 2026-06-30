# System Architecture Document: Healthcare Ecosystem

This document defines the comprehensive architecture of the Healthcare Ecosystem, adhering to the principles outlined in the PRD, Feature Breakdown, and Project Rules.

---

## 1. High-Level Architecture

The system follows a scalable, decoupled architecture where Android clients and Web Panels interact with a central API Gateway routing to modular backend services.

```mermaid
graph TD
    subgraph Clients
        P[Patient Android App]
        D[Doctor Android App]
        A[Admin Web Panel]
        M[Moderator Web Panel]
    end

    subgraph Edge Layer
        NGINX[Nginx Reverse Proxy / Load Balancer]
        WAF[AWS WAF]
    end

    subgraph Backend Services Ktor
        API[API Gateway Module]
        AUTH[Auth Service]
        TELE[Telemedicine Service]
        EMERGENCY[Emergency & Ambulance]
        HEALTH[Health Records Service]
    end

    subgraph Data & Infra Layer
        PG[(PostgreSQL)]
        REDIS[(Redis Cache & Message Broker)]
        S3[AWS S3 - Document Storage]
        FCM[Firebase Cloud Messaging]
    end

    Clients --> WAF
    WAF --> NGINX
    NGINX --> API
    API --> AUTH
    API --> TELE
    API --> EMERGENCY
    API --> HEALTH
    
    AUTH --> PG
    TELE --> PG
    EMERGENCY --> PG
    HEALTH --> PG
    
    AUTH --> REDIS
    EMERGENCY --> REDIS
    API --> FCM
    HEALTH --> S3
```

---

## 2. Low-Level Architecture (Mobile)

The Android Apps utilize Clean Architecture, MVI (Model-View-Intent) for the presentation layer, and Koin for Dependency Injection.

```mermaid
graph TD
    subgraph Presentation Layer
        UI[Jetpack Compose UI]
        INTENT[User Intent]
        STATE[UI State]
        VM[ViewModel]
    end

    subgraph Domain Layer
        UC[Use Cases]
        DM[Domain Models]
        REPO_IF[Repository Interfaces]
    end

    subgraph Data Layer
        REPO_IMPL[Repository Impl]
        API_KTOR[Ktor Client / Retrofit]
        DB[Room Database]
        DTO[DTOs]
    end

    UI -- triggers --> INTENT
    INTENT --> VM
    VM -- observes/emits --> STATE
    STATE --> UI
    
    VM -- executes --> UC
    UC -- depends on --> REPO_IF
    REPO_IMPL -- implements --> REPO_IF
    
    REPO_IMPL -- requests --> API_KTOR
    REPO_IMPL -- queries --> DB
```

---

## 3. Component Diagram (Backend)

The Ktor Backend follows a layered domain-driven approach using Exposed ORM.

```mermaid
graph LR
    subgraph Ktor Application
        R[Routing Layer]
        C[Controllers]
        S[Domain Services / UseCases]
        REP[Repositories]
        DAO[Exposed DAO / Tables]
    end

    R --> C
    C --> S
    S --> REP
    REP --> DAO
    DAO --> PG[(PostgreSQL)]
```

---

## 4. Service Diagram

The backend is structured as a Modular Monolith, allowing easy splitting into Microservices if scaling demands it.

```mermaid
graph TD
    subgraph Modular Monolith
        GATEWAY[API Routing]
        
        subgraph Auth Module
            JWT[JWT Manager]
            OTP[OTP Service]
        end
        
        subgraph Appointment Module
            SCHED[Scheduler]
            SLOT[Slot Manager]
        end
        
        subgraph Emergency Module
            SOS[SOS Dispatcher]
            GEO[Geospatial Tracker]
        end
        
        subgraph Notification Module
            PUSH[Push Dispatcher]
            EMAIL[Email Sender]
        end
        
        GATEWAY --> Auth Module
        GATEWAY --> Appointment Module
        GATEWAY --> Emergency Module
        Appointment Module --> Notification Module
        Emergency Module --> Notification Module
    end
```

---

## 5. Deployment Diagram

Deployed entirely on AWS using Docker containers orchestrated via ECS/EKS or straightforward Docker-Compose on EC2 for initial phases.

```mermaid
architecture-beta
    group aws(cloud)[AWS Cloud]

    service route53(internet)[Route 53] in aws
    service alb(server)[Application Load Balancer] in aws
    service ecs(server)[ECS Cluster - Ktor Backend] in aws
    service rds(database)[RDS PostgreSQL] in aws
    service redis(database)[ElastiCache Redis] in aws
    service s3(disk)[S3 Bucket] in aws

    route53:R --> alb:L
    alb:R --> ecs:L
    ecs:R --> rds:L
    ecs:T --> redis:B
    ecs:B --> s3:T
```

---

## 6. Data Flow

```mermaid
sequenceDiagram
    participant App as Android Client
    participant LB as Nginx Load Balancer
    participant Ktor as Backend (Ktor)
    participant Redis as Redis Cache
    participant PG as PostgreSQL (Exposed)

    App->>LB: GET /api/v1/doctors/123
    LB->>Ktor: Forward Request
    Ktor->>Redis: Check Cache (doctor:123)
    alt Cache Hit
        Redis-->>Ktor: Return Cached JSON
    else Cache Miss
        Ktor->>PG: SELECT * FROM doctors WHERE id=123
        PG-->>Ktor: Return Row
        Ktor->>Redis: SET doctor:123 (TTL 1hr)
    end
    Ktor-->>LB: HTTP 200 OK
    LB-->>App: JSON Response
```

---

## 7. Authentication Flow (JWT + OTP)

```mermaid
sequenceDiagram
    participant User
    participant App
    participant Auth as Auth Service
    participant SMS as SMS Gateway
    participant DB as Database

    User->>App: Enter Phone Number
    App->>Auth: POST /auth/request-otp
    Auth->>SMS: Dispatch OTP
    Auth->>DB: Store Hash & Expiry
    App-->>User: Show OTP Input
    User->>App: Enter OTP
    App->>Auth: POST /auth/verify-otp
    Auth->>DB: Validate Hash
    Auth-->>App: Return JWT (Access & Refresh)
    App->>App: Store securely in EncryptedSharedPreferences
```

---

## 8. Notification Flow

```mermaid
sequenceDiagram
    participant Service as Appointment Service
    participant Redis as Redis Pub/Sub
    participant Worker as Notification Worker
    participant FCM as Firebase Cloud Messaging
    participant Device as Patient Android App

    Service->>Redis: Publish Message (Topic: AppointmentReminder)
    Redis->>Worker: Consume Message
    Worker->>Worker: Format Payload
    Worker->>FCM: POST to FCM API
    FCM->>Device: Deliver Push Notification
```

---

## 9. Emergency Flow (Low Latency)

```mermaid
sequenceDiagram
    participant Patient
    participant Ktor as Emergency Service
    participant Redis as Redis (Geo)
    participant Ambulance

    Patient->>Ktor: POST /sos/trigger (Lat, Lng)
    Ktor->>Redis: GEORADIUS find nearby ambulances
    Redis-->>Ktor: Return [Amb_1, Amb_2]
    Ktor->>Ambulance: Push WebSockets/FCM Alert
    Ambulance->>Ktor: POST /sos/accept
    Ktor->>Patient: Return Ambulance ETA & Details
```

---

## 10. Video Call Flow (WebRTC via Signaling)

```mermaid
sequenceDiagram
    participant Doctor
    participant Sig as Ktor Signaling Server
    participant Patient
    participant TURN as STUN/TURN Server

    Doctor->>Sig: Join Room (Appt_ID)
    Patient->>Sig: Join Room (Appt_ID)
    Doctor->>Sig: Send SDP Offer
    Sig->>Patient: Route SDP Offer
    Patient->>Sig: Send SDP Answer
    Sig->>Doctor: Route SDP Answer
    Doctor->>TURN: ICE Candidate Negotiation
    Patient->>TURN: ICE Candidate Negotiation
    Doctor<<->>Patient: P2P Encrypted Video/Audio Stream
```

---

## 11. File Upload Flow (Presigned URLs)

```mermaid
sequenceDiagram
    participant Client
    participant Ktor as Backend
    participant S3 as AWS S3

    Client->>Ktor: GET /upload/presigned-url?type=prescription
    Ktor->>Ktor: Validate Auth & MIME Type
    Ktor-->>Client: Return S3 Presigned URL + File Key
    Client->>S3: PUT binary data direct to URL
    S3-->>Client: 200 OK
    Client->>Ktor: POST /medical-records (Save File Key)
    Ktor-->>Client: Record Saved Successfully
```

---

## 12. QR Login Flow

```mermaid
sequenceDiagram
    participant Web as Admin/Moderator Web
    participant Ktor as Auth Service
    participant App as Authenticated App

    Web->>Ktor: GET /auth/qr/generate
    Ktor-->>Web: Session ID & QR Payload
    Web->>Ktor: Open WebSocket (Subscribe to Session ID)
    App->>App: Scan QR Code
    App->>Ktor: POST /auth/qr/scan (Session ID, JWT)
    Ktor->>Ktor: Validate Mobile JWT
    Ktor->>Web: WS Message: { status: 'Authenticated', new_jwt: '...' }
    Web->>Web: Store JWT & Redirect to Dashboard
```

---

## 13. Security Layers

*   **Edge:** AWS WAF to block SQLi, XSS, and DDoS attacks.
*   **Transport:** Strict HTTPS (TLS 1.3) requirement.
*   **Application:** 
    *   Stateless JWT Authentication.
    *   Role-Based Access Control (RBAC) middleware verifying permissions on every route.
*   **Data:** 
    *   Passwords and PINs hashed with Argon2.
    *   AWS KMS used for encrypting sensitive PHI (Personal Health Information) columns at rest.
*   **Device:** EncryptedSharedPreferences on Android for token storage. Root detection to prevent execution on compromised devices.

---

## 14. Caching Strategy

*   **L1 (In-Memory App Cache):** Room database caches patient records and upcoming schedules for offline support.
*   **L2 (Distributed Cache):** Redis stores frequent read-heavy payloads (e.g., Doctor Profiles, Blood Bank Inventories) and handles session blacklisting.
*   **TTL Configuration:** Static data (Specializations) caches for 24h. Dynamic data (Doctor availability) caches for 1-5 minutes.

---

## 15. Scalability Strategy

*   **Horizontal Scaling:** Stateless Ktor containers scaled automatically via AWS Auto Scaling Groups based on CPU/Memory thresholds.
*   **Database Scaling:** PostgreSQL Primary instance for Writes; Read Replicas configured for heavy analytics queries (Admin Dashboard).
*   **Connection Pooling:** HikariCP configured in Ktor to manage database connections efficiently.
*   **Asynchronous Processing:** Heavy tasks (PDF generation, bulk emails) are pushed to Redis queues and processed by background worker instances.

---

## 16. Monitoring Strategy

*   **APM:** Datadog or New Relic integrated into Ktor for tracing request latency and identifying bottlenecks.
*   **Infrastructure Metrics:** AWS CloudWatch monitoring EC2/RDS CPU, Memory, and Disk IO.
*   **Client Monitoring:** Firebase Crashlytics to catch Android unhandled exceptions; Sentry for Web Panel JS errors.

---

## 17. Logging Strategy

*   **Format:** Structured JSON logging (Logback in Ktor).
*   **Aggregation:** ELK Stack (Elasticsearch, Logstash, Kibana) or AWS CloudWatch Logs.
*   **Context:** Every log entry must include an injected `correlation_id` (passed from Edge -> Service -> DB) and a `user_id` (if authenticated).
*   **Masking:** Strict filters to strip PHI and auth tokens before logs are flushed.

---

## 18. Disaster Recovery

*   **Database Backups:** Automated continuous incremental backups via AWS RDS with a 35-day retention period, plus daily automated snapshots.
*   **Failover:** Multi-AZ deployment for PostgreSQL and Redis ensures automatic failover in case of a zone outage.
*   **Infrastructure as Code:** Terraform/CloudFormation scripts maintain the exact AWS state, allowing full environment recreation in a secondary region (e.g., from `us-east-1` to `us-west-2`) within 30 minutes.
