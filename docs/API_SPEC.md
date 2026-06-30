# REST API Specification: Healthcare Ecosystem

This document provides the OpenAPI-style REST API specification for the Healthcare Ecosystem backend.

---

## 1. Authentication & QR Login

### 1.1 Request OTP
**URL:** `/api/v1/auth/request-otp`
**HTTP Method:** `POST`
**Headers:** `Content-Type: application/json`
**Authentication:** None
**Permissions:** None
**Rate Limits:** 3 requests / 5 minutes per IP

**Request Body:**
```json
{
  "phone": "+1234567890"
}
```
**Validation:** `phone` must be a valid E.164 formatted string.
**Response (200 OK):**
```json
{
  "status": "success",
  "message": "OTP sent successfully"
}
```
**Error Codes:**
*   `400 Bad Request`: Invalid phone format.
*   `429 Too Many Requests`: Rate limit exceeded.

### 1.2 Verify OTP & Login
**URL:** `/api/v1/auth/verify-otp`
**HTTP Method:** `POST`
**Headers:** `Content-Type: application/json`
**Authentication:** None
**Permissions:** None
**Rate Limits:** 5 requests / 15 minutes per IP

**Request Body:**
```json
{
  "phone": "+1234567890",
  "otp": "123456"
}
```
**Validation:** `otp` must be exactly 6 digits.
**Response (200 OK):**
```json
{
  "data": {
    "access_token": "eyJhbG...",
    "refresh_token": "dGVzd...",
    "user": {
      "id": "uuid-1234",
      "role": "PATIENT"
    }
  }
}
```
**Error Codes:**
*   `401 Unauthorized`: Invalid or expired OTP.

### 1.3 Generate QR Login Session (Web)
**URL:** `/api/v1/auth/qr/generate`
**HTTP Method:** `GET`
**Headers:** None
**Authentication:** None
**Permissions:** None
**Rate Limits:** 10 requests / hour per IP

**Request/Validation:** None
**Response (200 OK):**
```json
{
  "data": {
    "session_id": "qr-session-5678",
    "expires_in": 300
  }
}
```

---

## 2. Doctors & Patients

### 2.1 List Doctors
**URL:** `/api/v1/doctors`
**HTTP Method:** `GET`
**Headers:** `Authorization: Bearer <token>`
**Authentication:** Required (JWT)
**Permissions:** `PATIENT`, `ADMIN`
**Rate Limits:** 60 requests / minute per IP
**Pagination:** `page` (default: 1), `limit` (default: 20)
**Sorting:** `sort_by` (fee, rating), `order` (asc, desc)
**Filtering:** `specialization`, `is_verified`

**Example Request:** `GET /api/v1/doctors?specialization=Cardiology&page=1&limit=10`
**Response (200 OK):**
```json
{
  "data": [
    {
      "id": "uuid-doc-1",
      "name": "Dr. Smith",
      "specialization": "Cardiology",
      "fee": 150.00,
      "rating": 4.8
    }
  ],
  "meta": {
    "total": 45,
    "page": 1,
    "pages": 5
  }
}
```
**Error Codes:** `401 Unauthorized`.

---

## 3. Appointments & Prescriptions

### 3.1 Book Appointment
**URL:** `/api/v1/appointments`
**HTTP Method:** `POST`
**Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
**Authentication:** Required (JWT)
**Permissions:** `PATIENT`
**Rate Limits:** 5 requests / minute per User

**Request Body:**
```json
{
  "doctor_id": "uuid-doc-1",
  "schedule_date": "2026-07-01",
  "start_time": "10:00:00"
}
```
**Validation:** `schedule_date` must be in the future. Slot must be verified as available.
**Response (201 Created):**
```json
{
  "data": {
    "appointment_id": "uuid-appt-1",
    "status": "CONFIRMED"
  }
}
```
**Error Codes:**
*   `409 Conflict`: Slot already booked.
*   `400 Bad Request`: Invalid date/time format.

### 3.2 Create Prescription
**URL:** `/api/v1/prescriptions`
**HTTP Method:** `POST`
**Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
**Authentication:** Required (JWT)
**Permissions:** `DOCTOR`
**Rate Limits:** 10 requests / minute per User

**Request Body:**
```json
{
  "appointment_id": "uuid-appt-1",
  "notes": "Rest for 3 days.",
  "items": [
    { "medicine_name": "Paracetamol", "dosage": "500mg", "duration": "3 days", "instructions": "Twice daily after meals" }
  ]
}
```
**Validation:** `appointment_id` must belong to the requesting doctor and be in `COMPLETED` status.
**Response (201 Created):**
```json
{
  "data": {
    "prescription_id": "uuid-rx-1",
    "pdf_url": "https://s3.aws.com/bucket/rx/uuid-rx-1.pdf"
  }
}
```

---

## 4. Emergency & Ambulance

### 4.1 Trigger SOS
**URL:** `/api/v1/sos/trigger`
**HTTP Method:** `POST`
**Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
**Authentication:** Required (JWT)
**Permissions:** `PATIENT`
**Rate Limits:** 2 requests / minute per User

**Request Body:**
```json
{
  "lat": 34.0522,
  "lng": -118.2437
}
```
**Validation:** `lat` and `lng` must be valid decimal coordinates.
**Response (202 Accepted):**
```json
{
  "data": {
    "sos_id": "uuid-sos-1",
    "status": "DISPATCHING",
    "assigned_ambulance": {
      "driver_name": "John Doe",
      "phone": "+1987654321",
      "eta_minutes": 5
    }
  }
}
```

---

## 5. Blood Bank

### 5.1 Search Blood Inventory
**URL:** `/api/v1/blood-banks/inventory`
**HTTP Method:** `GET`
**Headers:** `Authorization: Bearer <token>`
**Authentication:** Required (JWT)
**Permissions:** `PATIENT`, `DOCTOR`, `ADMIN`
**Rate Limits:** 30 requests / minute per IP
**Filtering:** `blood_group` (required), `lat`, `lng`, `radius_km`

**Example Request:** `GET /api/v1/blood-banks/inventory?blood_group=O-&lat=34.05&lng=-118.24&radius_km=10`
**Response (200 OK):**
```json
{
  "data": [
    {
      "blood_bank_id": "uuid-bb-1",
      "name": "City General Blood Bank",
      "distance_km": 3.2,
      "units_available": 12
    }
  ]
}
```

---

## 6. File Upload

### 6.1 Get Presigned S3 URL
**URL:** `/api/v1/upload/presigned-url`
**HTTP Method:** `GET`
**Headers:** `Authorization: Bearer <token>`
**Authentication:** Required (JWT)
**Permissions:** `PATIENT`, `DOCTOR`
**Rate Limits:** 10 requests / minute per User
**Filtering:** `file_type` (e.g., `image/jpeg`, `application/pdf`)

**Response (200 OK):**
```json
{
  "data": {
    "upload_url": "https://s3.aws.com/bucket/...&Signature=...",
    "file_key": "records/uuid-user/uuid-file.pdf"
  }
}
```

---

## 7. Audit Logs & Analytics

### 7.1 View System Analytics (Admin)
**URL:** `/api/v1/admin/analytics`
**HTTP Method:** `GET`
**Headers:** `Authorization: Bearer <token>`
**Authentication:** Required (JWT)
**Permissions:** `ADMIN`
**Rate Limits:** 10 requests / minute per IP
**Filtering:** `start_date`, `end_date`

**Example Request:** `GET /api/v1/admin/analytics?start_date=2026-06-01&end_date=2026-06-30`
**Response (200 OK):**
```json
{
  "data": {
    "total_appointments": 1450,
    "revenue": 217500.00,
    "new_users": 350
  }
}
```

### 7.2 Fetch Audit Logs
**URL:** `/api/v1/admin/audit-logs`
**HTTP Method:** `GET`
**Headers:** `Authorization: Bearer <token>`
**Authentication:** Required (JWT)
**Permissions:** `ADMIN`
**Pagination:** `page`, `limit`
**Filtering:** `action_type`, `user_id`

**Response (200 OK):**
```json
{
  "data": [
    {
      "id": "uuid-log-1",
      "timestamp": "2026-06-30T10:00:00Z",
      "actor_id": "uuid-admin-1",
      "action": "ROLE_CHANGED",
      "target_id": "uuid-user-2",
      "ip_address": "192.168.1.1"
    }
  ]
}
```
