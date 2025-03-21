package com.dominik.tasktracker.model;

public enum TaskStatus {
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", " ");
    }
}