package com.dominik.tasktracker;

public enum TaskStatus {
    ADD,
    UPDATE,
    DELETE,
    MARK_IN_PROGRESS,
    MARK_DONE,
    LIST;

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", " ");
    }
}
