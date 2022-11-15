package com.globalvox.clinicmanagementsystem.entity.enums;

public enum Rating {

    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5");

    private final String label;

    Rating(String label) { this.label = label; }

    @Override
    public String toString() { return label; }

}
