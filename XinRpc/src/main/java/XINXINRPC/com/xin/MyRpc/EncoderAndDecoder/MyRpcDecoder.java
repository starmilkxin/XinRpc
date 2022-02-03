package XINXINRPC.com.xin.MyRpc.EncoderAndDecoder;

import XINXINRPC.com.xin.MyRpc.RpcInf.MessageProtocol;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcRequest;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcResponse;
import XINXINRPC.com.xin.MyRpc.core.NettyServer;
import XINXINRPC.com.xin.MyRpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class MyRpcDecoder extends ByteToMessageDecoder {
    private final Logger logger = LoggerFactory.getLogger(MyRpcDecoder.class);

    private final int magicNumber = 764261545;

    private int curMagicNumber;

    /**
     * 1 content为RpcRequest
     * 2 content为RpcResponse
     */
    private int status;

    //序列化算法
    private int serializer_code;

    //协议栈中的数据的长度
    private int len;

    public MyRpcDecoder() {

    }

    public MyRpcDecoder(Integer serializerCode) {
        this.serializer_code = serializerCode;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //读取魔数
        if (curMagicNumber != magicNumber) {
            if (in.readableBytes() >= 4) {
                curMagicNumber = in.readInt();
                if (curMagicNumber != magicNumber) {
                    logger.error("无法识别的协议包: {}", curMagicNumber);
                    throw new RuntimeException("无法识别的协议包");
                }
            }
        }
        //读取状态，判断是请求还是回答
        if (curMagicNumber == magicNumber && status == 0) {
            if (in.readableBytes() >= 4) {
                status = in.readInt();
            }
        }
        //判断序列化算法
        if (status > 0 && serializer_code == 0) {
            if (in.readableBytes() >= 4) {
                serializer_code = in.readInt();
            }
        }
        //读取数据长度
        if (serializer_code > 0 && len == 0) {
            if (in.readableBytes() >= 4) {
                len = in.readInt();
            }
        }
        //读取数据
        if (len > 0) {
            if (in.readableBytes() >= len) {
                byte[] content = new byte[len];
                in.readBytes(content);
                MessageProtocol message = new MessageProtocol(status, serializer_code, len, content);
                //获取序列化算法
                CommonSerializer serializer = CommonSerializer.getSerializer(serializer_code);
                //反序列化content,根据MessageProtocol得到RpcRequest或RpcResponse
                Object obj = serializer.deserialize(content, status == 1 ? RpcRequest.class : RpcResponse.class);
                out.add(obj);
                logger.info("接收到" + obj.getClass().getSimpleName() + ": " + obj);
                //初始化status, len
                status = 0;
                len = 0;
            }
        }
    }
}
