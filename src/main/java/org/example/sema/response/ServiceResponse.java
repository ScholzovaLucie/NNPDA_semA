package org.example.sema.response;

public class ServiceResponse<T> {
    private String message;
    private T data;

    public ServiceResponse(T data, String message) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
