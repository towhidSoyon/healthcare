# Healthcare Ecosystem User Flows

This document outlines the complete user journeys and systemic flows for the Patient, Doctor, Admin, and Moderator interfaces using Mermaid diagrams.

---

## 1. User Journeys: Patient

### 1.1 Primary Journey: Booking a Consultation
```mermaid
graph TD
    A[Launch App] --> B{Authenticated?}
    B -- Yes --> C[Home Screen]
    B -- No --> D[Login]
    D --> C
    C --> E[Search Doctors]
    E --> F[View Doctor Profile]
    F --> G[Select Time Slot]
    G --> H[Proceed to Payment]
    H --> I[Payment Success]
    I --> J[Appointment Confirmed]
    J --> K[Return to Home]
```

### 1.2 Alternative Journey: Emergency SOS
```mermaid
graph TD
    A[Home Screen] --> B[Tap SOS Panic Button]
    B --> C[Confirm SOS]
    C --> D[Broadcast Location to Ambulances]
    D --> E[Ambulance Accepts]
    E --> F[Track Ambulance ETA]
    F --> G[Ambulance Arrives]
```

### 1.3 Failure Journey: Payment Failure
```mermaid
graph TD
    A[Proceed to Payment] --> B[Gateway Processing]
    B --> C{Payment Status}
    C -- Failed --> D[Show Error Snackbar]
    D --> E[Prompt Retry]
    E --> F{Retry?}
    F -- Yes --> B
    F -- No --> G[Cancel Booking]
```

### 1.4 Offline Journey: Viewing Schedule
```mermaid
graph TD
    A[Launch App] --> B{Network Available?}
    B -- No --> C[Load Cached Data]
    C --> D[Show 'Offline Mode' Banner]
    D --> E[View Upcoming Appointments]
    E --> F[Try Booking]
    F --> G[Show 'No Internet' Dialog]
```

### 1.5 Recovery Journey: Forgot Password
```mermaid
graph TD
    A[Login Screen] --> B[Tap Forgot Password]
    B --> C[Enter Phone/Email]
    C --> D[Send OTP]
    D --> E[Verify OTP]
    E --> F[Set New Password]
    F --> G[Login Automatically]
```

---

## 2. User Journeys: Doctor

### 2.1 Primary Journey: Conducting a Consultation
```mermaid
graph TD
    A[Dashboard] --> B[View Today's Appointments]
    B --> C[Select Next Appointment]
    C --> D[Review Patient History]
    D --> E[Start Video Call]
    E --> F[Conduct Consultation]
    F --> G[End Call]
    G --> H[Write Prescription]
    H --> I[Send to Patient]
```

### 2.2 Alternative Journey: Rescheduling
```mermaid
graph TD
    A[Dashboard] --> B[Select Appointment]
    B --> C[Select Reschedule]
    C --> D[Choose New Slot]
    D --> E[Add Reason Note]
    E --> F[Notify Patient]
    F --> G[Update Schedule]
```

### 2.3 Failure Journey: Network Drop During Call
```mermaid
graph TD
    A[Video Call Active] --> B[Network Drops]
    B --> C[Attempt Reconnection]
    C --> D{Reconnected within 30s?}
    D -- Yes --> E[Resume Video]
    D -- No --> F[Downgrade to Audio Call]
    F --> G[Notify Patient]
```

### 2.4 Offline Journey: Viewing Schedule
```mermaid
graph TD
    A[Launch App] --> B{Network?}
    B -- No --> C[Load Local Database]
    C --> D[Show Daily Roster]
    D --> E[Try Starting Call]
    E --> F[Block Action - 'Offline']
```

### 2.5 Recovery Journey: Disputed Consultation
```mermaid
graph TD
    A[Patient Reports Doctor] --> B[Notification Received]
    B --> C[Open Support Ticket]
    C --> D[Submit Proof/Notes to Moderator]
    D --> E[Moderator Reviews]
    E --> F[Resolution Reached]
```

---

## 3. User Journeys: Admin

### 3.1 Primary Journey: System Monitoring
```mermaid
graph TD
    A[Admin Login] --> B[View Dashboard]
    B --> C[Analyze Revenue Charts]
    C --> D[Filter by Date Range]
    D --> E[Export PDF Report]
```

### 3.2 Alternative Journey: Role Assignment
```mermaid
graph TD
    A[Sidebar] --> B[Role Management]
    B --> C[Select User]
    C --> D[Change Role from Moderator to Admin]
    D --> E[Confirm Action]
    E --> F[Log Audit Trail]
```

### 3.3 Failure Journey: Unauthorized Access
```mermaid
graph TD
    A[Admin Panel] --> B[Attempt to Delete Root Admin]
    B --> C[Permission Check]
    C --> D[Access Denied]
    D --> E[Log Failed Action]
    E --> F[Show Security Alert]
```

*(Admin/Moderators do not have Offline Journeys as web panels strictly require internet)*

---

## 4. User Journeys: Moderator

### 4.1 Primary Journey: Verifying a Doctor
```mermaid
graph TD
    A[Moderator Login] --> B[Open Verification Queue]
    B --> C[Select Pending Doctor]
    C --> D[Review License Document]
    D --> E{Valid?}
    E -- Yes --> F[Approve Application]
    E -- No --> G[Reject with Reason]
    F --> H[Doctor Profile Activated]
```

### 4.2 Alternative Journey: Resolving Dispute
```mermaid
graph TD
    A[Open Support Tickets] --> B[Select Refund Request]
    B --> C[Review Consultation Logs]
    C --> D[Issue Partial Refund]
    D --> E[Close Ticket]
    E --> F[Notify Patient and Doctor]
```

### 4.3 Failure Journey: Document Fetch Error
```mermaid
graph TD
    A[Select Pending Doctor] --> B[Fetch S3 Document]
    B --> C[S3 Timeout]
    C --> D[Show 'Failed to Load Document']
    D --> E[Retry Fetch]
```

---

## 5. Systemic Flows

### 5.1 Navigation Graph (Patient App)
```mermaid
graph LR
    Splash --> Auth
    Auth --> Home
    Home --> Search[Search Doctors]
    Home --> Appts[Appointments]
    Home --> Records[Medical Records]
    Home --> SOS[Emergency]
    Search --> DocProfile[Doctor Profile]
    DocProfile --> Booking[Book Slot]
```

### 5.2 Authentication Flow (Cross-Platform)
```mermaid
sequenceDiagram
    participant User
    participant App as Mobile App
    participant Web as Web Panel
    participant Auth as Auth Server

    User->>App: Open Scanner
    User->>Web: Request QR Code
    Web->>Auth: Generate QR Session
    Auth-->>Web: Display QR
    App->>Web: Scan QR Code
    App->>Auth: Authorize Session ID
    Auth-->>Web: Broadcast 'Authenticated'
    Web-->>User: Redirect to Dashboard
```

### 5.3 Appointment Flow
```mermaid
stateDiagram-v2
    [*] --> Pending
    Pending --> Confirmed: Payment Success
    Pending --> Canceled: Timeout/Failed Payment
    Confirmed --> Rescheduled: Doctor/Patient Request
    Rescheduled --> Confirmed
    Confirmed --> Completed: Call Ended
    Confirmed --> Canceled: Canceled before start
    Completed --> [*]
    Canceled --> [*]
```

### 5.4 Emergency Flow
```mermaid
sequenceDiagram
    participant Patient
    participant System
    participant Ambulance

    Patient->>System: Trigger SOS
    System->>System: Identify Location
    System->>Ambulance: Broadcast Distress Signal
    Ambulance->>System: Accept Request
    System-->>Patient: Send Ambulance ETA
    System->>Patient: Share Driver Contact
```

### 5.5 Video Call Flow
```mermaid
sequenceDiagram
    participant Doctor
    participant Server
    participant WebRTC
    participant Patient

    Doctor->>Server: Start Consultation
    Server->>Patient: Send Push Notification (Incoming Call)
    Patient->>Server: Accept Call
    Server->>WebRTC: Generate Room Tokens
    WebRTC-->>Doctor: Connect
    WebRTC-->>Patient: Connect
    Doctor->>Patient: P2P Video Stream
```

### 5.6 Prescription Flow
```mermaid
graph TD
    A[Call Ends] --> B[Doctor Opens Prescription Pad]
    B --> C[Add Medicines & Dosages]
    C --> D[Digitally Sign]
    D --> E[Generate PDF]
    E --> F[Save to Database]
    F --> G[Send Notification to Patient]
    G --> H[Patient Downloads PDF]
```

### 5.7 Notification Flow
```mermaid
graph TD
    A[System Event Triggered] --> B{Check User Settings}
    B -- Enabled --> C[Determine Priority]
    C -- High --> D[Send Push + SMS]
    C -- Normal --> E[Send Push]
    D --> F[Firebase Cloud Messaging]
    E --> F
    F --> G[Deliver to Device]
```

### 5.8 High-Level Decision Tree (Triage)
```mermaid
graph TD
    A{Is it an Emergency?}
    A -- Yes --> B[Trigger SOS & Call Ambulance]
    A -- No --> C{Require Specialist?}
    C -- Yes --> D[Search by Speciality]
    C -- No --> E[Book General Physician]
    D --> F[Book Appointment]
    E --> F
```
