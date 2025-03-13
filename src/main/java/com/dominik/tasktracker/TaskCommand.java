package com.dominik.tasktracker;

public enum TaskCommand {
    ADD,
    UPDATE,
    DELETE,
    LIST,
    CHANGE_STATUS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}