package com.example.chainsave.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Result<T> {
    public static final int success = 200;
    public static final int serverError = 500;
    public static final int parameterError = 400;
    public static final int notLoginError = 401;
    private int status = success;
    private String message = "success";
    private T data;

    public Result setServerErrorMsgInfo(String msg){
        this.setStatus(serverError);
        this.setMessage(msg);
        return this;
    }
    public Result setParameterErrorMsgInfo(String msg){
        this.setStatus(parameterError);
        this.setMessage(msg);
        return this;
    }
    public Result setNotLoginErrorMsgInfo(String msg){
        this.setStatus(notLoginError);
        this.setMessage(msg);
        return this;
    }

}
