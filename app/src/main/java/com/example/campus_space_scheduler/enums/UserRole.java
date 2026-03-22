package com.example.campus_space_scheduler.enums;

public enum UserRole {
    STUDENT("Student"),
    FACULTY("Faculty"),
    HALL_INCHARGE("Hall Incharge"),
    STAFF_INCHARGE("Staff Incharge"),
    CSED_STAFF("CSED Staff"),
    HOD("HoD"),
    FACULTY_INCHARGE("Faculty Incharge"),
    LAB_ADMIN("Lab admin"),
    APP_ADMIN("App admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromDisplayName(String text) {
        for (UserRole role : values()) {
            if (role.displayName.equalsIgnoreCase(text)) {
                return role;
            }
        }
        return null;
    }
}
