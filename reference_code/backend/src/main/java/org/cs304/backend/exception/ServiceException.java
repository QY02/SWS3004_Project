package org.cs304.backend.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

    private final String code;
    private Object causeObject;

    public ServiceException(String message) {
        super(message);
        this.code = "500";
    }

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

}