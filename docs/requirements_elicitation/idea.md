# Steps of Requirements Elicitation
## 1. Identify Stakeholders
### Primary Stakeholders
- Students
    View available spaces
    Make booking requests
    Track booking status
- Faculty
    Book classrooms/labs/halls
    View schedules
    Manage their own bookings
### Secondary Stakeholders
- Department / Lab In-Charge
    Approve or manage bookings
    Handle conflicts
    Update room details
- Administrator
    Manage users and roles
    Configure spaces (labs, halls, classrooms)
    Oversee system usage
## 2. Gather Requirements
### 2.1 Functional Requirements
The system shall allow users to view real-time availability of campus spaces
The system shall allow authorized users to book classrooms, laboratories, and halls
The system shall display schedule details including date, time, and purpose
The system shall automatically assign the appropriate in-charge based on the selected space
The system shall allow administrators to edit space and schedule details
The system shall provide booking status updates (approved / pending / rejected) (optional)
### 2.2 Non-Functional Requirements
The system shall provide a quick response time for viewing availability and booking.
The system shall have an easy-to-use interface suitable for mobile devices.
The system shall ensure reliable data storage and retrieval.
The system shall support simultaneous access by multiple users.
### 2.3 Optional / Enhancement Requirements
The system may display the location of spaces on a campus map.
The system may provide notifications for booking updates.
The system may support future expansion to web or iOS platforms.
## 3. Prioritize Requirements
### 3.1 Must Have (Core requirements)
View real-time availability of classrooms, labs, and halls
View schedules (date, time, space, purpose)
Book campus spaces (faculty/admin)
Prevent booking conflicts (no double booking)
Automatic allotment of lab/hall in-charge
Role-based access (student / faculty / admin)
Store and retrieve booking data reliably (Firebase)
## 4. Categorize Feasibility
