package com.yeta.chat.util;

/**
 * 统一请求对象
 * @author YETA
 * @date 2018/11/23/15:32
 */
public class CommonRequest {

    /**
     * 请求类型
     * 1：群聊消息
     * 2：私聊消息
     * 3：浏览器自己请求资源
     */
    private Integer type;

    /**
     * 私聊消息发送的目标
     */
    private String target;

    /**
     * 消息
     */
    private String message;

    public CommonRequest() {
    }

    public CommonRequest(Integer type, String target, String message) {
        this.type = type;
        this.target = target;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CommonRequest{" +
                "type=" + type +
                ", target='" + target + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
