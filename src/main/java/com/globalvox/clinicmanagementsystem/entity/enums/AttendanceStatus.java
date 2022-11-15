package com.globalvox.clinicmanagementsystem.entity.enums;

public enum AttendanceStatus {

    ABSENT("A"),
    PRESENT("P"),
    LEAVE("L"),
    HALF_LEAVE("HL"),
    NOT_JOINED_YET("NON");

    private final String label;

    AttendanceStatus(String label) { this.label = label; }

    @Override
    public String toString() { return label; }
}
