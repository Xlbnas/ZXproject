package com.campus.textbook.common;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result {
    private int code;       // 状态码
    private String msg;     // 提示信息
    private Object data;    // 返回数据

    private Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 操作成功，无数据 */
    public static Result success(String msg) {
        return new Result(200, msg, null);
    }

    /** 操作成功，有数据 */
    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    /** 操作成功，有消息和数据 */
    public static Result success(String msg, Object data) {
        return new Result(200, msg, data);
    }

    /** 操作失败 */
    public static Result error(String msg) {
        return new Result(500, msg, null);
    }

    /** 未登录/Token失效 */
    public static Result unauthorized(String msg) {
        return new Result(401, msg, null);
    }

    /** 无权限 */
    public static Result forbidden(String msg) {
        return new Result(403, msg, null);
    }
}
