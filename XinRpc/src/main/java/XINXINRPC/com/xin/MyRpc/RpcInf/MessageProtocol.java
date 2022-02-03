package XINXINRPC.com.xin.MyRpc.RpcInf;

import lombok.Data;

@Data
public class MessageProtocol {
    private final int magicNumber = 764261545;

    /**
     * 1 content为RpcRequest
     * 2 content为RpcResponse
     */
    private int status;

    //序列化算法
    private int serializer_code;

    //数据长度
    private int len;

    //数据
    private byte[] content;

    public MessageProtocol(int status, int serializer_code, int len, byte[] content) {
        this.status = status;
        this.serializer_code = serializer_code;
        this.len = len;
        this.content = content;
    }
}
