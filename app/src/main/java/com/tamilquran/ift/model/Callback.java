package com.tamilquran.ift.model;

/**
 * Delivers the result of a background operation back to the caller.
 * Implementations are invoked on the main thread.
 */
public interface Callback<T> {
    void onResult(T result);
}
