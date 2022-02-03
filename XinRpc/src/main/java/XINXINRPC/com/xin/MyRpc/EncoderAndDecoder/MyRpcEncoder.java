package XINXINRPC.com.xin.MyRpc.EncoderAndDecoder;

import XINXINRPC.com.xin.MyRpc.RpcInf.RpcRequest;
import XINXINRPC.com.xin.MyRpc.core.NettyServer;
import XINXINRPC.com.xin.MyRpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class MyRpcEncoder extends MessageToByteEncoder {
    private final Logger logger = LoggerFactory.getLogger(MyRpcEncoder.class);

    //魔数，用于确定协议
    private final int magicNumber = 764261545;

    //序列化算法,encoder默认是protobuf
    private int serializer_code = 1;

    public MyRpcEncoder() {

    }

    public MyRpcEncoder(Integer serializerCode) {
        this.serializer_code = serializerCode;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //获取序列化算法
        CommonSerializer serializer = CommonSerializer.getSerializer(serializer_code);
        //将msg序列化
        byte[] data = serializer.serialize(msg);
        //发送数据
        Integer status = msg instanceof RpcRequest ? 1 : 2;
        Integer len = data.length;
        //发送魔数
        out.writeInt(magicNumber);
        //发送状态，表示是请求还是回答
        out.writeInt(status);
        //发送序列化算法标识
        out.writeInt(serializer_code);
        //发送数据长度
        out.writeInt(len);
        //发送数据
        out.writeBytes(data);
        logger.info((status == 1 ? "RpcRequest" : "RpcResponse") + "已经被发送：" + msg + " stasus:" + status + " len:" + len + " data:" + data);
    }
}
