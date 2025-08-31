package com.devwonder.common.util;

import com.devwonder.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Supplier;

public final class RepositoryUtils {
    
    private RepositoryUtils() {
        // Utility class
    }

    /**
     * Utility method to reduce orElseThrow boilerplate code
     * @param optional the optional to check
     * @param exceptionSupplier supplier for the exception to throw if empty
     * @return the value if present
     * @throws T the exception if not present
     */
    public static <T, E extends BaseException> T findOrThrow(Optional<T> optional, Supplier<E> exceptionSupplier) {
        return optional.orElseThrow(exceptionSupplier);
    }

    /**
     * Common pattern for finding by ID with not found exception
     * @param optional the optional to check
     * @param entityName name of the entity (for error message)
     * @param id the ID that was searched for
     * @return the entity if found
     * @throws EntityNotFoundException if not found
     */
    public static <T> T findByIdOrThrow(Optional<T> optional, String entityName, Object id) {
        return optional.orElseThrow(() -> new EntityNotFoundException(entityName, id));
    }

    /**
     * Generic entity not found exception
     */
    public static class EntityNotFoundException extends BaseException {
        public EntityNotFoundException(String entityName, Object id) {
            super(String.format("%s not found with ID: %s", entityName, id), 
                  "ENTITY_NOT_FOUND", 
                  HttpStatus.NOT_FOUND);
        }
    }

    /**
     * For entities with specific not found exceptions
     */
    public static <T> T findOrThrow(Optional<T> optional, String message, String errorCode, HttpStatus status) {
        return optional.orElseThrow(() -> new GenericNotFoundException(message, errorCode, status));
    }

    /**
     * Generic not found exception for custom cases
     */
    public static class GenericNotFoundException extends BaseException {
        public GenericNotFoundException(String message, String errorCode, HttpStatus status) {
            super(message, errorCode, status);
        }
    }
}