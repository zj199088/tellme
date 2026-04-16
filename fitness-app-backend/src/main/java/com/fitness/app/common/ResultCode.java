package com.fitness.app.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ALREADY_EXISTS(1003, "用户已存在"),
    
    PLAN_NOT_FOUND(2001, "计划不存在"),
    PLAN_NO_PERMISSION(2002, "无权限操作该计划"),
    
    TEMPLATE_NOT_FOUND(3001, "模板不存在"),
    
    FILE_UPLOAD_ERROR(4001, "文件上传失败");
    
    private final int code;
    private final String message;
}
