package com.vertexcache.module.restapi.model;

import com.google.gson.annotations.SerializedName;
import com.vertexcache.core.util.message.ResultCode;

public class ApiResponse<T> {

    @SerializedName("success")
    private final boolean success;

    @SerializedName("message")
    private final String message;

    @SerializedName("data")
    private final T data;

    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private int status = 200;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // --- Generic Success Variants ---
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    // --- Generic Error Variant ---
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // --- ResultCode Variants ---
    public static <T> ApiResponse<T> success(ResultCode code, T data) {
        ApiResponse<T> response = new ApiResponse<>(true, code.getMessage(), data);
        response.code = code.getCode();
        return response;
    }

    public static <T> ApiResponse<T> error(ResultCode code) {
        ApiResponse<T> response = new ApiResponse<>(false, code.getMessage(), null);
        response.code = code.getCode();
        return response;
    }

    // --- Getters & Modifiers ---
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public ApiResponse<T> withStatus(int status) {
        this.status = status;
        return this;
    }
}