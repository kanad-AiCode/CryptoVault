
package com.kanad.cryptovault.response;


public class ValidationErrorResponse<T> {

    private String message;
    private T errors;

    public ValidationErrorResponse(String message, T errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getErrors() {
        return errors;
    }

    public void setErrors(T errors) {
        this.errors = errors;
    }
}
