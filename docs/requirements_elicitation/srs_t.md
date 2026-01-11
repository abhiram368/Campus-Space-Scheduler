
# SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

## **CHL Book – Classroom, Hall & Lab Booking System**

**Version:** 1.0
**Prepared by:** CHL Book Project Team
**Course:** Software Engineering Lab
**Department:** Computer Science & Engineering (CSED)

---

## Revision History

| Name          | Date | Reason for Changes  | Version |
| ------------- | ---- | ------------------- | ------- |
| CHL Book Team | 2026 | Initial & Final SRS | 1.0     |

---

## 1. Introduction

### 1.1 Purpose

The purpose of this document is to specify the software requirements for **CHL Book**, version 1.0. CHL Book is a college-internal application designed to manage the booking of **classrooms, laboratories, and halls** in the CSED department. This SRS defines the system’s functional and non-functional requirements to guide development, validation, and evaluation.

### 1.2 Intended Audience and Reading Suggestions

This document is intended for:

* Project developers
* Course instructors
* Teaching assistants
* Evaluators

Readers are advised to begin with Sections 1 and 2 for system context, followed by Section 4 for functional requirements and Section 5 for non-functional requirements.

### 1.3 Product Scope

CHL Book replaces the existing manual booking process with a centralized digital system. It supports booking requests, hierarchical approvals, notifications, calendar integration, and audit logging to ensure transparency, discipline, and efficient utilization of departmental resources.

### 1.4 Definitions, Acronyms, and Abbreviations

* **LOR** – Letter of Recommendation
* **HOD** – Head of Department
* **Instructional Hours** – Official academic hours
* **Non-Instructional Hours** – Holidays and mid-exam days

### 1.5 References

* IEEE Software Requirements Specification Guidelines
* Firebase Documentation
* Sample SRS: Group_1_SRS (WHO’ DAT)

---

## 2. Overall Description

### 2.1 Product Perspective

CHL Book is a standalone, college-internal application. It integrates with Google Authentication, Firebase backend services, email notification services, and Google Calendar APIs.

### 2.2 Product Functions

Major system functions include:

* User authentication and role identification
* Classroom booking and cancellation
* Laboratory booking with hierarchical approval
* Hall booking with hierarchical approval
* Calendar-based availability visualization
* Email and Google Calendar notifications
* Audit log generation and access

### 2.3 User Classes and Characteristics

* **Student:** Can request bookings, upload LOR (where required), view booking status
* **Faculty (Normal Faculty):** Can request bookings and view booking details; **cannot approve requests**
* **Approval Authorities (Role-based Faculty):**

  * Staff In-charge
  * Faculty In-charge
  * Hall In-charge
  * HOD
    *Approval permissions depend strictly on role assignment*
* **App Admin:** Manages users, roles, resources, and calendars; cannot perform bookings

### 2.4 Operating Environment

* College-internal application
* Backend & Database: **Firebase**
* Client platform: Mobile application (mobile-first acceptable)

### 2.5 Design and Implementation Constraints

* Google Authentication is mandatory
* Firebase must be used as backend and database
* Instructional hours must be blocked automatically
* App Admin cannot override bookings

### 2.6 Assumptions and Dependencies

* Users possess institutional Google accounts
* Continuous internet connectivity is available
* Academic calendar is accurately maintained

---

## 3. External Interface Requirements

### 3.1 User Interfaces

* Login interface (Google Authentication + CHL password)
* Dashboard interface (Classroom / Lab / Hall selection)
* Monthly calendar interface
* Booking request interface
* **Cancellation request interface**
* **User profile interface**
* Approval interface
* Booking history interface

### 3.2 Hardware Interfaces

No direct hardware interfaces are required.

### 3.3 Software Interfaces

* Firebase Authentication and Firestore
* Google Calendar API
* Email notification service

### 3.4 Communications Interfaces

* Secure HTTPS communication
* OAuth-based authentication

---

## 4. System Features (Functional Requirements)

### FR1: Download Application

**Description:** A user shall be able to download the CHL Book application for institutional use.

---

### FR2: User Registration

**Description:** Given that a user downloads the application, the user shall register using Google Authentication and be mapped to one or more roles using the system database.

---

### FR3: User Login

**Description:** Given that a user is registered, the user shall be able to log in using Google Authentication and a CHL Book password.

---

### FR4: Role Identification

**Description:** The system shall identify the user’s role(s) from a separate role database upon successful login.

---

### FR5: Classroom Booking

**Description:** Given that a classroom slot is available, students and faculty shall be able to book the classroom for a selected time slot.

---

### FR6: Classroom Cancellation

**Description:** Users shall be able to submit cancellation requests for classroom bookings at any time, making the slot available upon cancellation approval.

---

### FR7: Student Lab Booking

**Description:** Given that a student selects a laboratory slot, the system shall require the student to upload a valid LOR before submitting the booking request.

---

### FR8: Faculty Lab Booking

**Description:** Faculty members shall be able to submit laboratory booking requests using a digital form without uploading an LOR.

---

### FR9: Lab Approval Workflow

**Description:** Laboratory booking requests shall follow the approval hierarchy:
**Staff In-charge → Faculty In-charge → HOD**.
Approval by any one authority shall finalize the booking.

---

### FR10: Hall Booking Approval Workflow

**Description:** Hall booking requests shall follow the approval hierarchy:
**Hall In-charge → HOD**.
Approval by any one authority shall finalize the booking.
The approval protocol is identical to laboratory booking, except that only Hall In-charge and HOD are involved.

---

### FR11: Notifications

**Description:** The system shall send email notifications for booking submission, approval, rejection, and cancellation events.

---

### FR12: Google Calendar Integration

**Description:** The system shall automatically create a Google Calendar event upon booking approval and delete the event upon cancellation.

---

### FR13: Audit Log Generation

**Description:** The system shall record all booking-related actions, including approvals, rejections, forwarding actions, and corresponding timestamps.

---

### FR14: Audit Log Access

**Description:**

* App Admin shall be able to view all audit logs.
* Users shall be able to view the approval trail of **their own bookings only**.

---

## 5. Other Non-Functional Requirements

### 5.1 Performance Requirements

* The system shall support up to **400 concurrent users**
* Booking availability checks shall be near real-time
* The system shall prevent double-booking of the same slot

### 5.2 Security Requirements

* Google Authentication shall be mandatory
* Role-based access control shall be enforced
* Users shall not access other users’ booking data

### 5.3 Reliability Requirements

* System availability shall be at least **98%** during operational hours

### 5.4 Usability Requirements

* User interface shall be simple and self-explanatory
* Calendar-based booking interaction shall be supported

### 5.5 Maintainability Requirements

* The system shall be modular to support future extension to other departments

---

## 6. Other Requirements

* Audit logs shall be retained for **one semester**
* Penalty rules shall be clearly instructed beforehand
* Rule violations shall be handled by **Faculty In-charges**
* Session timeout policy: **To Be Determined**

---

## Appendix A: Glossary

Refer to Section 1.4.

## Appendix B: Analysis Models

* Use-case diagrams
* Sequence diagrams
* Approval workflow diagrams

## Appendix C: To Be Determined List

1. Session timeout duration
2. Automated penalty enforcement rules

---