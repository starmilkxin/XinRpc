package XINXINRPC.com.xin.MyRpc.RpcInf;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = -2348978412016982000L;

    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(200);
        response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(String message) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(300);
        response.setMessage(message);
        return response;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

