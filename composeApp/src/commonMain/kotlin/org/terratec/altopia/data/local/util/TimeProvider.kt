package org.terratec.altopia.data.local.util

/**
 * Returns the current time in milliseconds since epoch.
 * Platform-specific implementations provide the actual time.
 */
expect fun getCurrentTimeMillis(): Long
