
## Functional Requirements (FRs)


### **FR-1: User Authentication**

* The system shall allow users to log in using official NITC email IDs.
* The system shall support Google-based authentication.
* The system shall provide a password recovery option.

---

### **FR-2: Role-Based Access Control**

* The system shall classify users into:

  * Student
  * Faculty
  * Lab In-Charge
  * Administrator
* The system shall restrict functionalities based on assigned roles.

---

### **FR-3: View Lab/Hall Schedule**

* The system shall display a calendar-based schedule view.
* The schedule shall show:

  * Date
  * Time slot
  * Booking purpose
  * Booking status
* Authorized users shall be able to view the name of the person who booked the slot.

---

### **FR-4: Real-Time Availability Display**

* The system shall show real-time availability of labs and halls.
* Availability shall be displayed for the next **14 days**.
* Instructional hours and blocked days shall not be bookable.

---

### **FR-5: Booking Request Submission**

* The system shall allow users to submit booking requests.
* Booking requests shall include:

  * Space (lab/hall)
  * Date and time
  * Purpose of booking

---

### **FR-6: Conflict Handling**

* The system shall prevent double booking for the same time slot.
* If multiple requests are submitted for the same slot:

  * Only one request shall be approved.
  * Remaining requests shall be rejected with a reason message.

---

### **FR-7: Booking Approval & Messaging**

* The system shall allow Lab In-Charge to:

  * Accept or reject booking requests
  * Provide rejection remarks or suggestions for alternative slots
* Rejected users shall be allowed to submit a new request.

---

### **FR-8: Automatic Supervisor Assignment**

* Upon approval, the system shall automatically assign:

  * Student Admins for student-led events
  * Faculty Supervisors for academic activities
* Assignment shall consider availability schedules.

---

### **FR-9: Notification System**

* The system shall notify:

  * Users when a request is submitted, approved, or rejected
  * Lab In-Charge when a new booking request is pending

---

### **FR-10: Administrative Control**

* The Administrator shall be able to:

  * Add or remove labs and halls
  * Configure instructional hours
  * View all booking activities

---

## Non-Functional Requirements (NFRs)

### **NFR-1: Performance**

* The system shall update booking status in real time.
* Calendar and booking data shall load within **3 seconds** under normal network conditions.

---

### **NFR-2: Scalability**

* The system shall support simultaneous access by multiple users.
* The system shall allow addition of new labs, halls, and users without architectural changes.

---

### **NFR-3: Usability**

* The system shall provide a simple and intuitive mobile UI.
* Users shall be able to perform core actions within minimal steps.

---

### **NFR-4: Reliability**

* The system shall ensure data consistency during concurrent booking attempts.
* The system shall prevent loss of booking data during failures.

---

### **NFR-5: Availability**

* The system shall be available 24×7 except during scheduled maintenance.
* System downtime shall be minimal.

---

### **NFR-6: Security**

* The system shall authenticate users securely using Firebase Authentication.
* Unauthorized access to booking and approval features shall be restricted.
* User data shall be securely stored and transmitted.

---

### **NFR-7: Maintainability**

* The system shall follow modular design principles.
* The system shall allow easy updates and bug fixes.

---

### **NFR-8: Compatibility**

* The system shall be compatible with Android devices.
* The backend shall be cloud-based and accessible over the internet.

---


