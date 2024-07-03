package org.cs304.backend.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {

    public static final String CODE_SUCCESS = "200";
    public static final String CODE_SYS_ERROR = "500";

    private String code;

    private String msg;

    private Object data;

    public static Result success(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new Result(CODE_SUCCESS, "Request Success", null);
    }

    public static Result success(HttpServletResponse response, Object data) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new Result(CODE_SUCCESS, "Request Success", data);
    }

    public static Result error(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new Result(CODE_SYS_ERROR, "System Error", null);
    }

    public static Result error(HttpServletResponse response, String msg) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new Result(CODE_SYS_ERROR, msg, null);
    }

    public static Result error(HttpServletResponse response, String code, String msg) {
        response.setStatus(Integer.parseInt(code));
        return new Result(code, msg, null);
    }

    public static Result error(HttpServletResponse response, String code, String msg, Object data) {
        response.setStatus(Integer.parseInt(code));
        return new Result(code, msg, data);
    }

}
