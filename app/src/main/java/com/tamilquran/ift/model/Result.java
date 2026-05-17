package com.tamilquran.ift.model;

public final class Result<T> {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY
    }

    private final Status status;
    private final T data;
    private final String message;
    private final Throwable error;

    private Result(Status status, T data, String message, Throwable error) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.error = error;
    }

    public static <T> Result<T> loading() {
        return new Result<>(Status.LOADING, null, null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(Status.SUCCESS, data, null, null);
    }

    public static <T> Result<T> empty() {
        return new Result<>(Status.EMPTY, null, null, null);
    }

    public static <T> Result<T> error(String message, Throwable error) {
        return new Result<>(Status.ERROR, null, message, error);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
