package XINXINRPC.com.xin.MyRpc.handler;

import XINXINRPC.com.xin.MyRpc.NacosUtils.NacosServiceRegistry;
import XINXINRPC.com.xin.MyRpc.NacosUtils.ServiceProvider;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcRequest;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

@Component
@ChannelHandler.Sharable
public class ServerHandler extends  ChannelInboundHandlerAdapter{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Autowired
    //服务提供中心
    private ServiceProvider serviceProvider;

    @Autowired
    //服务注册中心
    private NacosServiceRegistry serviceRegistry;

    public void register0(Object service, InetSocketAddress inetSocketAddress) {
        serviceProvider.setService(service);
        serviceRegistry.register(service.getClass().getInterfaces()[0].getSimpleName(), inetSocketAddress);
        logger.info("成功注册服务[" + service.getClass().getInterfaces()[0].getSimpleName() + "] 地址[" + inetSocketAddress + "]");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) msg;
            String interfaceName = request.getInterfaceName();
            String methodName = request.getMethodName();
            Object[] parameters = request.getParameters();
            Class<?>[] paramTypes = request.getParamTypes();
            try {
                //通过服务注册中心获取接口的唯一实现类对象
                Object o = serviceProvider.getService(interfaceName);
                //获取方法
                Method declaredMethod = o.getClass().getDeclaredMethod(methodName, paramTypes);
                //执行方法，得到结果
                Object invoke = declaredMethod.invoke(o, parameters);
                logger.info("服务端方法执行成功，结果为: {}", invoke);
                //将结果封装成RpcResponse后发送，发送后再取消连接
                ctx.writeAndFlush(RpcResponse.success(invoke)).addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
                ctx.writeAndFlush(RpcResponse.fail(e.getMessage()));
                logger.info("服务端方法执行失败，原因为: {}", e.getMessage());
                e.printStackTrace();
            }
        }else {
            throw new IllegalArgumentException("错误的接受对象");
        }
    }
}
