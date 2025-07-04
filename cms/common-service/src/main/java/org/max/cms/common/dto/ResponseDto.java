package org.max.cms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    
    private int code;
    private String message;
    private T data;
    private boolean success;
    
    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .code(200)
                .message("Success")
                .data(data)
                .success(true)
                .build();
    }
    
    public static <T> ResponseDto<T> success(String message, T data) {
        return ResponseDto.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .success(true)
                .build();
    }
    
    public static <T> ResponseDto<T> error(String message) {
        return ResponseDto.<T>builder()
                .code(500)
                .message(message)
                .success(false)
                .build();
    }
    
    public static <T> ResponseDto<T> error(int code, String message) {
        return ResponseDto.<T>builder()
                .code(code)
                .message(message)
                .success(false)
                .build();
    }
}