# Steps of Requirements Elicitation

---

## 1. Identify Stakeholders

### Primary Stakeholders
**Students and Faculty**
- View available spaces (labs, halls, classrooms)
- Submit booking requests
- Track booking status (pending / approved / rejected)
- View schedules and availability

---

### Secondary Stakeholders

#### Lab In-Charge (Faculty)
- Approve or reject booking requests
- Handle booking conflicts
- Manage cancellations and user blocking

#### App Administrator
- Manage users and roles
- Add or remove labs, halls, and classrooms
- Configure instructional hours
- Oversee overall system usage

---

## Hierarchy
- **Category 1:** Students and Faculty  
- **Category 2:** Lab Admin (Student Admin or Faculty)  
- **Category 3:** Lab In-Charge (Faculty)  
- **Category 4:** App Administrator  

---

## 2. Gather Requirements

### 2.1 Functional Requirements

#### FR-1: User Authentication
- The system shall allow users to log in using official NITC email IDs.
- The system shall support Google-based authentication.
- The system shall provide a password recovery option.

---

#### FR-2: Role-Based Access Control
- The system shall classify users into:
  - Student
  - Faculty
  - Lab In-Charge
  - Administrator
- The system shall restrict functionalities based on assigned roles.

---

#### FR-3: View Lab/Hall Schedule
- The system shall display a calendar-based schedule view.
- The schedule shall show:
  - Date
  - Time slot
  - Booking purpose
  - Booking status
- Authorized users shall be able to view the name of the person who booked the slot.

---

#### FR-4: Real-Time Availability Display
- The system shall show real-time availability of labs and halls.
- Availability shall be displayed for the next **14 days**.
- Instructional hours and blocked days shall not be bookable.

---

#### FR-5: Booking Request Submission
- The system shall allow users to submit booking requests.
- Booking requests shall include:
  - Space (lab / hall / classroom)
  - Date and time
  - Purpose of booking

---

#### FR-6: Conflict Handling
- The system shall prevent double booking for the same time slot.
- If multiple requests are submitted for the same slot:
  - Only one request shall be approved.
  - Remaining requests may be rejected by the Lab In-Charge with a reason message.

---

#### FR-7: Booking Approval & Messaging
- The system shall allow the Lab In-Charge to:
  - Accept or reject booking requests
  - Provide rejection remarks or suggest alternative slots
- Rejected users shall be allowed to submit a new booking request.

---

#### FR-8: Faculty/Student Allotment
- Upon approval of a booking request, the system shall initiate faculty/student allotment.
- Allotment shall consider availability schedules and booking purpose.

---

#### FR-9: Notification System
- The system shall notify:
  - Users when a request is submitted, approved, or rejected
  - Lab In-Charge when a new booking request is pending
  - Assigned faculty or student when they are allotted to a space

---

#### FR-10: Administrative Control
- The Administrator shall be able to:
  - Add or remove labs, halls, and classrooms
  - Configure instructional hours
  - View all booking activities

---

#### FR-11: FCFS-Based Faculty/Student Allotment
- Upon approval of a booking request by the Lab In-Charge, the system shall notify all eligible assigned faculty members and student admins.
- Faculty/student allotment shall be done on a **First-Come-First-Serve (FCFS)** basis.
- Faculty members or student admins may accept or reject the allotment.
- The lab or hall booking shall be confirmed only after a faculty/student accepts the allotment.
- Allotment shall consider availability schedules.

---

#### FR-12: Booking Cancellation Handling
- The system shall allow users to request cancellation of a confirmed booking.
- The cancellation request shall be forwarded to the Lab In-Charge.
- If the Lab In-Charge accepts the cancellation:
  - The booking shall be cancelled.
  - The lab or hall shall be marked as available.
- If the Lab In-Charge rejects the cancellation:
  - The user shall be blocked from making further booking requests.
- A blocked user shall remain restricted until a physical written request is submitted to the Lab In-Charge.
- The Lab In-Charge shall have the authority to revoke the user’s block through the system.

---

### 2.2 Non-Functional Requirements (NFRs)

#### NFR-1: Performance
- The system shall update booking status in real time.
- Calendar and booking data shall load within **3 seconds** under normal network conditions.

---

#### NFR-2: Scalability
- The system shall support simultaneous access by multiple users.
- The system shall allow addition of new labs, halls, and users without architectural changes.

---

#### NFR-3: Usability
- The system shall provide a simple and intuitive mobile UI.
- Users shall be able to perform core actions within minimal steps.

---

#### NFR-4: Reliability
- The system shall ensure data consistency during concurrent booking attempts.
- The system shall prevent loss of booking data during failures.

---

#### NFR-5: Availability
- The system shall be available 24×7 except during scheduled maintenance.
- System downtime shall be minimal.

---

#### NFR-6: Security
- The system shall authenticate users securely using Firebase Authentication.
- Unauthorized access to booking and approval features shall be restricted.
- User data shall be securely stored and transmitted.

---

#### NFR-7: Maintainability
- The system shall follow modular design principles.
- The system shall allow easy updates and bug fixes.

---

#### NFR-8: Compatibility
- The system shall be compatible with Android devices.
- The backend shall be cloud-based and accessible over the internet.

---

### 2.3 Optional / Enhancement Requirements
- Display space locations on a campus map
- Provide advanced search and filtering options
- Generate usage analytics for administrators
- Support future expansion to web or iOS platforms

---

## 3. Prioritize Requirements

### Must Have
- User authentication and role-based access
- Real-time availability and schedule viewing
- Booking request submission and approval
- Conflict prevention and FCFS allotment
- Notifications and data persistence

### Should Have
- Booking status tracking
- Administrative editing of schedules
- Performance optimization
- Validation and error handling

### Could Have
- Campus map view
- Analytics and reports
- Advanced filters

### Won’t Have (Current Version)
- iOS or web deployment
- Integration with external university systems
- Payment handling

---

## 4. Categorize Feasibility

### Technical Feasibility

| Requirement | Feasibility | Remarks |
|------------|------------|---------|
| Real-time availability | Feasible | Firebase real-time updates |
| Booking | Feasible | Firestore CRUD |
| Conflict prevention | Feasible | Time-slot validation |
| Automated allotment | Feasible | Rule-based mapping |
| Booking status | Feasible | Simple state management |
| Campus map view | Partially Feasible | Requires map API integration |

---

### Operational Feasibility

| Aspect | Feasibility | Remarks |
|------|------------|---------|
| Ease of use | Feasible | Mobile-friendly Flutter UI |
| User acceptance | Feasible | Similar to existing practices |
| Admin control | Feasible | Role-based access |

---

### Economic Feasibility

| Factor | Feasibility | Remarks |
|------|------------|---------|
| Development cost | Feasible | Open-source tools |
| Infrastructure cost | Feasible | Firebase free tier |
| Maintenance cost | Feasible | Minimal for academic use |

---

### Schedule Feasibility

| Phase | Feasibility | Remarks |
|------|------------|---------|
| Core features | Feasible | Achievable within semester |
| Optional features | Time-dependent | If time permits |

---

### Legal and Ethical Feasibility

| Aspect | Feasibility | Remarks |
|------|------------|---------|
| Data privacy | Feasible | Firebase Authentication |
| Licensing | Feasible | Flutter & Firebase free |

---

## 5. Constraints and Assumptions
- Only authenticated campus users can access the system.
- The application is developed as an academic project.
- Initial deployment targets Android devices.
- Internet connectivity is required.

---

## 6. Summary
The elicited requirements comprehensively define the functional behavior, constraints, and quality attributes of the Campus Space Scheduler. These requirements form a stable foundation for system design, implementation, testing, and deployment.
