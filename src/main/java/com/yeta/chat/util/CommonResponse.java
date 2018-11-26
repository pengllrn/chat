package com.yeta.chat.util;

/**
 * 统一返回对象
 * @author YETA
 * @date 2018/11/23/15:44
 */
public class CommonResponse {

    /**
     * 状态码
     * 1000：成功
     * 1001：失败
     */
    private Integer code;

    /**
     * 返回类型
     * 1：群聊消息
     * 2：私聊消息
     * 3：浏览器自己请求资源
     * 4：服务器主动推送
     */
    private Integer type;

    /**
     * 消息
     */
    private Object message;

    public CommonResponse(Integer code, Integer type, Object message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "code=" + code +
                ", type=" + type +
                ", message=" + message +
                '}';
    }
}
