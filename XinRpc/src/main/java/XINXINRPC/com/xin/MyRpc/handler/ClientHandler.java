package XINXINRPC.com.xin.MyRpc.handler;

import XINXINRPC.com.xin.MyRpc.EncoderAndDecoder.MyRpcDecoder;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcRequest;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

@ChannelHandler.Sharable
@Data
public class ClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private ChannelHandlerContext context;//上下文
    private RpcResponse response;//返回的结果
    private RpcRequest request;//发送的请求

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerAdded");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelRegistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        logger.info("chnannelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("chnannelInactive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelUnregistered");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("handlerRemoved");
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcResponse) {
            response = (RpcResponse) msg;
            if (response.getStatusCode() == 200) {
                logger.info("客户端方法调用成功，结果为: {}", response);
            }else if (response.getStatusCode() == 300) {
                logger.error("客户端方法调用失败，结果为: {}", response);
            }
            notify();
        }else {
            throw new IllegalArgumentException("错误的接受类型");
        }
    }

    //被代理对象调用, 发送数据给服务器，-> wait -> 等待被唤醒(channelRead) -> 返回结果 (3)-》5
    @Override
    public synchronized Object call() throws Exception {
        ChannelFuture future = context.writeAndFlush(request);
        logger.info("客户端发送服务请求并等待");
        //进行wait
        wait(); //等待channelRead 方法获取到服务器的结果后，唤醒
        return response.getData();
    }

}
