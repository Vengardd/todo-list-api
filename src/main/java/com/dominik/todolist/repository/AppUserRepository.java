package com.dominik.todolist.repository;

import com.dominik.todolist.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Finds a user by their email address.
     * Used for login and checking if email exists during registration.
     * @param email The email to search for.
     * @return An Optional containing the User if found, otherwise empty.
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     * Useful for quick check during registration without fetching the whole user object.
     * @param email The email to check.
     * @return true if a user with this email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
