package com.devwonder.common.util;

import org.slf4j.Logger;

public final class LoggingUtils {
    
    private LoggingUtils() {
        // Utility class
    }

    /**
     * Standard logging for entity creation
     */
    public static void logEntityCreation(Logger log, String entityName, Object id) {
        log.info("{} created successfully with ID: {}", entityName, id);
    }

    /**
     * Standard logging for entity creation with account context
     */
    public static void logEntityCreation(Logger log, String entityName, Object id, String accountId) {
        log.info("{} created successfully with ID: {} for account: {}", entityName, id, accountId);
    }

    /**
     * Standard logging for entity update
     */
    public static void logEntityUpdate(Logger log, String entityName, Object id) {
        log.info("{} updated successfully with ID: {}", entityName, id);
    }

    /**
     * Standard logging for entity deletion
     */
    public static void logEntityDeletion(Logger log, String entityName, Object id) {
        log.info("{} deleted successfully with ID: {}", entityName, id);
    }

    /**
     * Standard logging for entity fetch
     */
    public static void logEntityFetch(Logger log, String entityName, Object id) {
        log.info("Fetching {} with ID: {}", entityName, id);
    }

    /**
     * Standard logging for entity list fetch
     */
    public static void logEntityListFetch(Logger log, String entityName, int page, int size) {
        log.info("Fetching {} list - page: {}, size: {}", entityName, page, size);
    }

    /**
     * Standard logging for operation start
     */
    public static void logOperationStart(Logger log, String operation, Object... params) {
        if (params.length == 0) {
            log.info("Starting {}", operation);
        } else {
            log.info("Starting {} with params: {}", operation, params);
        }
    }

    /**
     * Standard logging for operation completion
     */
    public static void logOperationComplete(Logger log, String operation) {
        log.info("Completed {}", operation);
    }

    /**
     * Standard logging for validation
     */
    public static void logValidation(Logger log, String entityName, Object id, String validationType) {
        log.debug("Validating {} with ID: {} - {}", entityName, id, validationType);
    }

    /**
     * Standard logging for business operation
     */
    public static void logBusinessOperation(Logger log, String operation, String entityName, Object id) {
        log.info("Executing {} for {} with ID: {}", operation, entityName, id);
    }

    /**
     * Standard logging for search operations
     */
    public static void logSearch(Logger log, String entityName, String searchCriteria) {
        log.info("Searching {} with criteria: {}", entityName, searchCriteria);
    }

    /**
     * Standard logging for count operations
     */
    public static void logCount(Logger log, String entityName, long count) {
        log.info("Found {} {} records", count, entityName);
    }

    /**
     * Standard logging for cache operations
     */
    public static void logCacheOperation(Logger log, String operation, String cacheName, Object key) {
        log.debug("Cache {} - cache: {}, key: {}", operation, cacheName, key);
    }
}