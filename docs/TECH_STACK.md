# Technology Stack: Healthcare Ecosystem

This document outlines the authoritative technology stack for the Healthcare Ecosystem, detailing our current choices, the rationale behind them, and future scaling paths.

---

## 1. Android (Patient & Doctor Apps)
*   **Technology:** Kotlin + Jetpack Compose + Koin (DI)
*   **Version:** Kotlin 1.9.x, Jetpack Compose 1.6.x, Koin 3.5.x
*   **Why it was selected:** Kotlin is the industry standard for Android. Jetpack Compose provides a declarative UI paradigm that drastically reduces boilerplate and UI bugs compared to XML. Koin is lightweight, Kotlin-native, and offers faster compile times than Dagger/Hilt.
*   **Alternatives considered:** 
    *   *Flutter/React Native:* Rejected due to the critical nature of the app (Emergency SOS, native background services, hardware integrations). Native Android guarantees the highest reliability and performance.
    *   *Hilt:* Rejected in favor of Koin due to Koin's pure Kotlin DSL and lack of kapt/ksp compilation overhead.
*   **Future migration path:** Migration to Kotlin Multiplatform (KMP) for shared business logic when expanding to iOS, allowing us to keep native UIs (Compose/SwiftUI) while sharing Domain and Data layers.

## 2. Backend
*   **Technology:** Kotlin + Ktor + Exposed (ORM)
*   **Version:** Kotlin 1.9.x, Ktor 2.3.x, Exposed 0.45.x
*   **Why it was selected:** Ktor is an asynchronous, highly performant web framework built natively for Kotlin coroutines. It provides exceptional throughput for high-concurrency tasks (like WebSockets for emergency tracking) with a minimal memory footprint compared to Spring Boot. Exposed offers a typesafe SQL DSL.
*   **Alternatives considered:** 
    *   *Spring Boot:* Rejected because it is heavier, has higher startup times, and relies heavily on reflection/annotations.
    *   *Node.js / Express:* Rejected because Kotlin provides stronger type safety, better concurrency scaling with Coroutines, and shared language semantics with the Android teams.
*   **Future migration path:** Splitting the modular monolith into independent Ktor microservices (Auth, Telemedicine, Emergency) communicating via gRPC as the engineering team and user base scale beyond 5 million.

## 3. Database
*   **Technology:** PostgreSQL
*   **Version:** 16.x
*   **Why it was selected:** PostgreSQL is the most advanced open-source relational database. It offers rock-solid ACID compliance (crucial for medical/financial data), robust JSONB support for unstructured medical notes, and powerful extensions like PostGIS.
*   **Alternatives considered:** 
    *   *MongoDB:* Rejected due to the highly relational nature of the data (Users -> Appointments -> Prescriptions -> Bills).
    *   *MySQL:* Rejected due to PostgreSQL's superior handling of concurrent writes, JSON processing, and geospatial querying.
*   **Future migration path:** Implementing table partitioning for historical audit logs and migrating geospatial SOS tracking to TimescaleDB or specialized PostGIS clusters.

## 4. Caching & Message Broker
*   **Technology:** Redis
*   **Version:** 7.2.x
*   **Why it was selected:** Redis offers sub-millisecond read/write latency. It is used for caching frequent DB queries (Doctor Availability), session management (JWT blacklists), and acts as a Pub/Sub message broker for async tasks (Notification dispatch).
*   **Alternatives considered:** 
    *   *Memcached:* Rejected as it lacks persistence, data structures (lists, sets), and Pub/Sub capabilities.
    *   *RabbitMQ/Kafka:* Rejected for initial phase as Redis Pub/Sub provides sufficient messaging capabilities without the operational overhead of Kafka.
*   **Future migration path:** Migrating heavy event-driven workloads (e.g., Audit Logging, Analytics streams) from Redis Pub/Sub to Apache Kafka for durable, replayable event sourcing.

## 5. Storage
*   **Technology:** Amazon S3 (Simple Storage Service)
*   **Version:** N/A (Managed AWS Service)
*   **Why it was selected:** S3 provides 99.999999911% durability, infinite scalability, and lifecycle policies. It is perfect for storing PDFs (Prescriptions), images (Avatars), and scanned medical records securely.
*   **Alternatives considered:** 
    *   *Local Block Storage (EBS):* Rejected as it does not scale horizontally and requires complex manual backups.
    *   *Google Cloud Storage:* Rejected to keep infrastructure consolidated within the AWS ecosystem.
*   **Future migration path:** Implementing S3 Glacier for long-term archiving of medical records older than 5 years to reduce storage costs while maintaining HIPAA compliance.

## 6. Authentication
*   **Technology:** Custom JWT Implementation + Twilio SMS for OTP
*   **Version:** JWT (RFC 7519)
*   **Why it was selected:** Stateless JWTs allow the API Gateway/Ktor to authenticate requests without hitting the database, massively reducing latency. Twilio is the industry leader for reliable, global SMS delivery.
*   **Alternatives considered:** 
    *   *Firebase Authentication:* Rejected because a healthcare platform requires absolute ownership and control over patient credentials and identity infrastructure, without vendor lock-in.
    *   *Auth0:* Rejected due to high costs at the 1 Million+ user scale.
*   **Future migration path:** Integration with biometric authentication standards (FIDO2/WebAuthn) for passwordless, highly secure logins on devices.

## 7. Notifications
*   **Technology:** Firebase Cloud Messaging (FCM)
*   **Version:** FCM HTTP v1 API
*   **Why it was selected:** FCM is the definitive, battery-efficient standard for pushing notifications to Android devices. It handles offline queuing and background delivery natively.
*   **Alternatives considered:** 
    *   *Custom WebSocket Push:* Rejected because keeping persistent WS connections drains mobile battery and complicates OS-level background execution restrictions.
*   **Future migration path:** Integrating AWS Pinpoint or Braze for advanced, multi-channel marketing campaigns and localized push notifications.

## 8. Video Calling
*   **Technology:** WebRTC via Twilio Programmable Video
*   **Version:** WebRTC 1.0 (Twilio Android SDK latest)
*   **Why it was selected:** Twilio manages the complex STUN/TURN server infrastructure required for NAT traversal in WebRTC. It provides a HIPAA-eligible architecture, ensuring encrypted, high-quality P2P or SFU video streams.
*   **Alternatives considered:** 
    *   *Self-hosted WebRTC (Jitsi/Janus):* Rejected for initial launch due to the immense DevOps overhead required to scale video SFU servers globally and ensure low latency.
*   **Future migration path:** Transitioning to a custom self-hosted WebRTC infrastructure (e.g., Mediasoup or Pion) if Twilio per-minute costs become prohibitive at scale.

## 9. Maps & Geolocation
*   **Technology:** Google Maps SDK & Directions API
*   **Version:** Android SDK v18+
*   **Why it was selected:** Google Maps provides the most accurate real-time traffic data and routing, which is absolutely critical for the Emergency Ambulance routing feature.
*   **Alternatives considered:** 
    *   *Mapbox:* Strongly considered for UI customization and cost, but Google Maps edge out in real-time traffic accuracy in developing regions.
    *   *OpenStreetMap:* Rejected due to lack of reliable, out-of-the-box real-time routing engines.
*   **Future migration path:** Hybrid approach: using Mapbox for static UI map rendering (to save costs) and Google Directions API strictly for active ambulance routing.

## 10. Cloud Infrastructure
*   **Technology:** Amazon Web Services (AWS)
*   **Version:** N/A (ECS Fargate, RDS, ElastiCache)
*   **Why it was selected:** AWS provides the deepest suite of compliance certifications (HIPAA, SOC2). ECS Fargate allows serverless container execution without managing EC2 instances. RDS provides automated failover and backups.
*   **Alternatives considered:** 
    *   *Google Cloud Platform (GCP):* Viable alternative, but AWS has a wider availability of healthcare-specific integrations and a larger talent pool for DevOps.
*   **Future migration path:** Moving from ECS Fargate to Amazon EKS (Elastic Kubernetes Service) when microservice complexity demands service meshes (Istio) and advanced pod orchestration.

## 11. CI/CD
*   **Technology:** GitHub Actions
*   **Version:** v3/v4 workflows
*   **Why it was selected:** Natively integrated with our source control. It allows us to define Infrastructure-as-Code and CI/CD pipelines in YAML alongside the application code. Fast, secure, and supports custom runners.
*   **Alternatives considered:** 
    *   *Jenkins:* Rejected due to the high maintenance overhead of managing Jenkins master/worker nodes.
    *   *GitLab CI:* Excellent tool, but we are utilizing GitHub for repository hosting.
*   **Future migration path:** Integrating ArgoCD for GitOps-style continuous deployment once the infrastructure moves to Kubernetes (EKS).

## 12. Monitoring & APM
*   **Technology:** Datadog
*   **Version:** N/A (SaaS)
*   **Why it was selected:** Datadog provides a unified dashboard for APM (Application Performance Monitoring), Infrastructure metrics, and Real User Monitoring (RUM) for Android. It traces a request from the Android UI, through Ktor, down to the exact SQL query in RDS.
*   **Alternatives considered:** 
    *   *AWS CloudWatch + X-Ray:* Cheaper, but lacks the intuitive unified UI and advanced anomaly detection that Datadog provides.
    *   *Prometheus + Grafana:* Rejected for initial launch due to the setup and maintenance overhead of managing our own observability stack.
*   **Future migration path:** Transitioning internal infrastructure metrics to Prometheus/Grafana and OpenTelemetry to reduce Datadog licensing costs at massive scale.

## 13. Logging
*   **Technology:** ELK Stack (via AWS OpenSearch Service)
*   **Version:** OpenSearch 2.x
*   **Why it was selected:** Centralized log aggregation is mandatory for HIPAA compliance and debugging. Ktor logs are shipped via FluentBit to OpenSearch, allowing lightning-fast text searches across millions of log lines using Kibana.
*   **Alternatives considered:** 
    *   *Datadog Logging:* Very powerful but prohibitively expensive for high-volume ingestion.
*   **Future migration path:** Introducing a hot-warm-cold architecture where logs older than 30 days are automatically rolled into S3 standard-IA, and eventually Glacier, queryable via AWS Athena.

## 14. Analytics
*   **Technology:** Mixpanel
*   **Version:** N/A (SaaS)
*   **Why it was selected:** Mixpanel excels at event-based user journey tracking and funnel analysis. It is essential for tracking user drop-offs during the Appointment Booking or Registration flows.
*   **Alternatives considered:** 
    *   *Google Analytics (GA4):* Better for marketing attribution, but inferior to Mixpanel for deep, in-app behavioral funnel analysis.
*   **Future migration path:** Building an internal Data Lake using AWS Redshift, streaming events via Amazon Kinesis. This brings all patient behavioral data fully in-house for advanced Machine Learning models (e.g., predicting appointment no-shows).
