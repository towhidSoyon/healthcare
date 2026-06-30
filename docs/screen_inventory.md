# Healthcare Ecosystem Screen Inventory

This document serves as the UI and architecture blueprint for the Patient Android App, Doctor Android App, Admin Web Panel, and Moderator Web Panel.

---

# Part 1: Patient Android App

## 1. Authentication Feature

### 1.1 Screen: Patient Login
- **Purpose:** Authenticate existing patients.
- **Entry Point:** App Launch (if not authenticated)
- **Exit Navigation:** `PatientHomeRoute`, `RegisterRoute`, `OtpVerifyRoute`
- **Accessible By:** Unauthenticated Users
- **Dependencies:** None
- **Primary Actions:** Submit Phone/Email + Password
- **Secondary Actions:** Forgot Password, Go to Register
- **API Endpoints Used:** `POST /api/v1/auth/login`
- **Offline Support:** No
- **Required Permissions:** Internet

**Mandatory UI Components:**
- Top App Bar: No
- Bottom Navigation: No
- Floating Action Button: No
- Dialogs: Forgot Password Dialog
- Bottom Sheets: None
- Permission Requests: None
- Loading States: Button Loading Spinner
- Empty States: None
- Error States: Inline text errors (Invalid credentials)
- Success States: Toast ("Login Successful")
- Skeleton Loading: No
- Pull To Refresh: No
- Pagination: No
- Search: No
- Filters: No
- Sorting: No

**Android Architecture Blueprint:**
- **Composable Name:** `PatientLoginScreen`
- **ViewModel Name:** `PatientLoginViewModel`
- **State Class:** `PatientLoginState`
- **Intent Class:** `PatientLoginIntent`
- **Navigation Route:** `auth/login`
- **Repository:** `AuthRepository`
- **Use Cases:** `LoginUserUseCase`, `ValidateCredentialsUseCase`
- **Expected API:** `AuthApi`
- **Expected Local Database Tables:** `sessions`
- **Expected Remote Models:** `LoginRequest`, `AuthResponse`

---

## 2. Telemedicine Feature

### 2.1 Screen: Patient Home (Dashboard)
- **Purpose:** Central hub for booking, viewing upcoming appointments, and quick SOS.
- **Entry Point:** Successful Login
- **Exit Navigation:** `BookAppointmentRoute`, `VideoCallRoute`, `SosRoute`, `ProfileRoute`
- **Accessible By:** Authenticated Patients
- **Dependencies:** User Profile, Appointment List
- **Primary Actions:** Search Doctors, Quick Book, Trigger SOS
- **Secondary Actions:** View Upcoming Consultations
- **API Endpoints Used:** `GET /api/v1/profile`, `GET /api/v1/appointments/upcoming`
- **Offline Support:** Yes (Cached upcoming appointments)
- **Required Permissions:** Location (for SOS readiness)

**Mandatory UI Components:**
- Top App Bar: Yes (Greeting + Profile Avatar)
- Bottom Navigation: Yes (Home, Records, Ambulances, Settings)
- Floating Action Button: Yes (Red SOS Panic Button)
- Dialogs: None
- Bottom Sheets: Speciality Selector
- Permission Requests: Foreground Location Request
- Loading States: Circular Progress Indicator on lists
- Empty States: "No upcoming appointments" Graphic
- Error States: Snackbar ("Failed to load data")
- Success States: None
- Skeleton Loading: Yes (Doctor list placeholder)
- Pull To Refresh: Yes
- Pagination: No
- Search: Yes (Global search bar)
- Filters: No
- Sorting: No

**Android Architecture Blueprint:**
- **Composable Name:** `PatientHomeScreen`
- **ViewModel Name:** `PatientHomeViewModel`
- **State Class:** `PatientHomeState`
- **Intent Class:** `PatientHomeIntent`
- **Navigation Route:** `main/home`
- **Repository:** `AppointmentRepository`, `UserRepository`
- **Use Cases:** `GetUpcomingAppointmentsUseCase`, `GetPatientProfileUseCase`
- **Expected API:** `AppointmentApi`, `UserApi`
- **Expected Local Database Tables:** `appointments`, `profiles`
- **Expected Remote Models:** `AppointmentDto`, `UserProfileDto`

### 2.2 Screen: Book Appointment
- **Purpose:** Allow patient to select a time slot for a specific doctor.
- **Entry Point:** Doctor Details Screen
- **Exit Navigation:** `PaymentRoute`, `PatientHomeRoute`
- **Accessible By:** Authenticated Patients
- **Dependencies:** Doctor ID, Payment Gateway
- **Primary Actions:** Select Date, Select Time Slot, Confirm Booking
- **Secondary Actions:** Cancel
- **API Endpoints Used:** `GET /api/v1/doctors/{id}/availability`, `POST /api/v1/appointments`
- **Offline Support:** No
- **Required Permissions:** None

**Mandatory UI Components:**
- Top App Bar: Yes (Title: "Book Appointment" + Back Arrow)
- Bottom Navigation: No
- Floating Action Button: No
- Dialogs: Confirmation Dialog
- Bottom Sheets: Payment Method Selector
- Permission Requests: None
- Loading States: Loading overlay during booking
- Empty States: "No available slots on this date"
- Error States: Snackbar ("Slot already booked")
- Success States: Full-screen success animation
- Skeleton Loading: Calendar slots skeleton
- Pull To Refresh: No
- Pagination: No
- Search: No
- Filters: No
- Sorting: No

**Android Architecture Blueprint:**
- **Composable Name:** `BookAppointmentScreen`
- **ViewModel Name:** `BookAppointmentViewModel`
- **State Class:** `BookAppointmentState`
- **Intent Class:** `BookAppointmentIntent`
- **Navigation Route:** `appointment/book/{doctorId}`
- **Repository:** `DoctorRepository`, `AppointmentRepository`
- **Use Cases:** `GetDoctorAvailabilityUseCase`, `CreateAppointmentUseCase`
- **Expected API:** `DoctorApi`, `AppointmentApi`
- **Expected Local Database Tables:** None (Transient)
- **Expected Remote Models:** `TimeSlotDto`, `CreateAppointmentRequest`

---

## 3. Emergency Feature

### 3.1 Screen: Emergency SOS Tracker
- **Purpose:** Display live ambulance tracking and alert status.
- **Entry Point:** Pressing SOS FAB
- **Exit Navigation:** `CancelEmergencyDialog` -> `PatientHomeRoute`
- **Accessible By:** Authenticated Patients
- **Dependencies:** Location Services, SMS Service
- **Primary Actions:** Track Ambulance, Cancel SOS
- **Secondary Actions:** Call Driver
- **API Endpoints Used:** `POST /api/v1/sos/trigger`, `GET /api/v1/ambulances/{id}/track`
- **Offline Support:** Yes (Falls back to SMS trigger)
- **Required Permissions:** Precise Location, Send SMS, Make Phone Call

**Mandatory UI Components:**
- Top App Bar: Yes (Flashing Red Warning State)
- Bottom Navigation: No
- Floating Action Button: No
- Dialogs: Cancel Confirmation Dialog
- Bottom Sheets: Driver Details & ETA
- Permission Requests: Location (Strict), Call Phone
- Loading States: "Locating nearby ambulance..." Map Overlay
- Empty States: None
- Error States: "Network failed. Sending SMS alert." Snackbar
- Success States: "Ambulance Dispatched" Toast
- Skeleton Loading: No
- Pull To Refresh: No
- Pagination: No
- Search: No
- Filters: No
- Sorting: No

**Android Architecture Blueprint:**
- **Composable Name:** `SosTrackerScreen`
- **ViewModel Name:** `SosTrackerViewModel`
- **State Class:** `SosTrackerState`
- **Intent Class:** `SosTrackerIntent`
- **Navigation Route:** `emergency/tracker`
- **Repository:** `EmergencyRepository`, `LocationRepository`
- **Use Cases:** `TriggerSosUseCase`, `TrackAmbulanceUseCase`, `SendSmsFallbackUseCase`
- **Expected API:** `EmergencyApi`
- **Expected Local Database Tables:** `sos_events`
- **Expected Remote Models:** `SosRequest`, `AmbulanceLocationUpdate`

---

# Part 2: Doctor Android App

## 1. Telemedicine Feature

### 1.1 Screen: Doctor Dashboard
- **Purpose:** Manage daily schedule and incoming consultation requests.
- **Entry Point:** App Launch / Login
- **Exit Navigation:** `VideoCallRoute`, `PatientHistoryRoute`
- **Accessible By:** Verified Doctors
- **Dependencies:** None
- **Primary Actions:** Start Call, View Patient Records
- **Secondary Actions:** Reschedule/Cancel Slot
- **API Endpoints Used:** `GET /api/v1/doctors/schedule/today`
- **Offline Support:** Yes (Read-only schedule)
- **Required Permissions:** None

**Mandatory UI Components:**
- Top App Bar: Yes (Date Picker Toggle)
- Bottom Navigation: Yes (Schedule, Patients, Earnings, Profile)
- Floating Action Button: No
- Dialogs: Cancel Appointment Warning
- Bottom Sheets: Appointment Quick Actions
- Permission Requests: None
- Loading States: Circular spinner for list
- Empty States: "No appointments today" Illustration
- Error States: Snackbar
- Success States: None
- Skeleton Loading: List item skeletons
- Pull To Refresh: Yes
- Pagination: Yes
- Search: Yes (Search by patient name)
- Filters: Yes (Status: Pending, Completed, Canceled)
- Sorting: Yes (Time ascending)

**Android Architecture Blueprint:**
- **Composable Name:** `DoctorDashboardScreen`
- **ViewModel Name:** `DoctorDashboardViewModel`
- **State Class:** `DoctorDashboardState`
- **Intent Class:** `DoctorDashboardIntent`
- **Navigation Route:** `doctor/dashboard`
- **Repository:** `ScheduleRepository`
- **Use Cases:** `GetDailyScheduleUseCase`, `UpdateAppointmentStatusUseCase`
- **Expected API:** `DoctorScheduleApi`
- **Expected Local Database Tables:** `doctor_schedules`
- **Expected Remote Models:** `ScheduleDto`, `StatusUpdateRequest`

### 1.2 Screen: Write Prescription
- **Purpose:** Issue digital medication and notes to the patient.
- **Entry Point:** End of Video/Audio Call
- **Exit Navigation:** `DoctorDashboardRoute`
- **Accessible By:** Verified Doctors
- **Dependencies:** Completed Appointment ID
- **Primary Actions:** Add Medication, Save & Send
- **Secondary Actions:** Add Notes, Skip
- **API Endpoints Used:** `POST /api/v1/prescriptions`
- **Offline Support:** Yes (Queued for sync)
- **Required Permissions:** None

**Mandatory UI Components:**
- Top App Bar: Yes (Title: "New Prescription")
- Bottom Navigation: No
- Floating Action Button: Yes (Add Medication Row)
- Dialogs: Confirm Sending Dialog
- Bottom Sheets: Medication Search/Suggestions
- Permission Requests: None
- Loading States: Overlay on "Send"
- Empty States: "No medications added"
- Error States: Inline validation (Dosage required)
- Success States: Toast ("Prescription Sent")
- Skeleton Loading: No
- Pull To Refresh: No
- Pagination: No
- Search: Yes (Medication database lookup)
- Filters: No
- Sorting: No

**Android Architecture Blueprint:**
- **Composable Name:** `WritePrescriptionScreen`
- **ViewModel Name:** `PrescriptionViewModel`
- **State Class:** `PrescriptionState`
- **Intent Class:** `PrescriptionIntent`
- **Navigation Route:** `consultation/prescription/{appointmentId}`
- **Repository:** `PrescriptionRepository`
- **Use Cases:** `SearchMedicationUseCase`, `SubmitPrescriptionUseCase`
- **Expected API:** `PrescriptionApi`
- **Expected Local Database Tables:** `prescription_items` (offline queue)
- **Expected Remote Models:** `CreatePrescriptionRequest`, `MedicationDto`

---

# Part 3: Admin Web Panel

## 1. System Operations

### 1.1 Screen: Admin Dashboard
- **Purpose:** High-level overview of ecosystem metrics and revenue.
- **Entry Point:** Web Login
- **Exit Navigation:** `UserManagement`, `FinancialReports`
- **Accessible By:** Super Admin, Admin
- **Dependencies:** Analytics Service
- **Primary Actions:** View Charts, Export Data
- **Secondary Actions:** Filter by Date Range
- **API Endpoints Used:** `GET /api/v1/admin/analytics/summary`
- **Offline Support:** No
- **Required Permissions:** Web Session Token

**Mandatory UI Components:**
- Top App Bar: Yes (Global Search, Profile)
- Bottom Navigation: No (Left Sidebar Menu instead)
- Floating Action Button: No
- Dialogs: Export Config Dialog
- Bottom Sheets: No
- Permission Requests: None
- Loading States: Chart area spinners
- Empty States: "No data available for this range"
- Error States: Alert banner at top
- Success States: Toast notifications
- Skeleton Loading: Dashboard metric cards
- Pull To Refresh: No (Manual refresh button)
- Pagination: No
- Search: No
- Filters: Yes (Date Range Picker)
- Sorting: No

*(Android details omitted as this is a Web Panel)*

### 1.2 Screen: Role Management
- **Purpose:** Assign roles and granular permissions to staff.
- **Entry Point:** Sidebar -> Security
- **Exit Navigation:** Back to Dashboard
- **Accessible By:** Super Admin Only
- **Dependencies:** IAM Service
- **Primary Actions:** Create Role, Edit Permissions
- **Secondary Actions:** Delete Role
- **API Endpoints Used:** `GET /api/v1/roles`, `PUT /api/v1/roles/{id}`
- **Offline Support:** No
- **Required Permissions:** IAM.Manage

**Mandatory UI Components:**
- Top App Bar: Yes
- Bottom Navigation: No
- Floating Action Button: Yes (Add New Role)
- Dialogs: Confirm Deletion Dialog
- Bottom Sheets: No
- Permission Requests: None
- Loading States: Button spinners
- Empty States: None
- Error States: Form validation errors
- Success States: Success Banner
- Skeleton Loading: Table row skeleton
- Pull To Refresh: No
- Pagination: Yes (Role list)
- Search: Yes
- Filters: No
- Sorting: Yes (By Role Name)

---

# Part 4: Moderator Web Panel

## 1. Moderation Feature

### 1.1 Screen: Doctor Verification Queue
- **Purpose:** Review and approve pending doctor registrations.
- **Entry Point:** Moderator Login
- **Exit Navigation:** `DoctorProfileReview`
- **Accessible By:** Moderator, Admin
- **Dependencies:** Document Storage (S3)
- **Primary Actions:** View Documents, Approve, Reject
- **Secondary Actions:** Request More Info
- **API Endpoints Used:** `GET /api/v1/moderator/queue`, `POST /api/v1/moderator/verify/{id}`
- **Offline Support:** No
- **Required Permissions:** Moderator Session Token

**Mandatory UI Components:**
- Top App Bar: Yes
- Bottom Navigation: No (Left Sidebar Menu)
- Floating Action Button: No
- Dialogs: Rejection Reason Dialog
- Bottom Sheets: Document Viewer (PDF/Image Modal)
- Permission Requests: None
- Loading States: Table row loading overlay
- Empty States: "Queue is empty. Great job!"
- Error States: Toast error message
- Success States: Green checkmark animation on row
- Skeleton Loading: Table skeleton
- Pull To Refresh: No
- Pagination: Yes
- Search: Yes (By Doctor Name/Email)
- Filters: Yes (Status: Pending, Flagged)
- Sorting: Yes (By Application Date)

*(Android details omitted as this is a Web Panel)*

### 1.2 Screen: Dispute Resolution Center
- **Purpose:** Handle user reports and canceled consultation refunds.
- **Entry Point:** Sidebar -> Tickets
- **Exit Navigation:** `TicketDetails`
- **Accessible By:** Moderator, Admin
- **Dependencies:** Support Ticket System
- **Primary Actions:** Open Ticket, Issue Refund, Flag User
- **Secondary Actions:** Send Message to User
- **API Endpoints Used:** `GET /api/v1/moderator/tickets`, `POST /api/v1/moderator/resolve-ticket`
- **Offline Support:** No
- **Required Permissions:** Moderator Session Token

**Mandatory UI Components:**
- Top App Bar: Yes
- Bottom Navigation: No
- Floating Action Button: No
- Dialogs: Confirm Refund Dialog
- Bottom Sheets: No
- Permission Requests: None
- Loading States: Message sending spinner
- Empty States: "No active tickets"
- Error States: Inline alert banner
- Success States: Ticket status turns green (Resolved)
- Skeleton Loading: Chat/Log skeleton
- Pull To Refresh: No
- Pagination: Yes
- Search: Yes (By Ticket ID, User ID)
- Filters: Yes (Severity, Status)
- Sorting: Yes (By Update Time)
