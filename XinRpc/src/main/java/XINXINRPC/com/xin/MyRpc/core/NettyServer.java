package XINXINRPC.com.xin.MyRpc.core;

import XINXINRPC.com.xin.MyRpc.EncoderAndDecoder.MyRpcEncoder;
import XINXINRPC.com.xin.MyRpc.NacosUtils.ShutdownHook;
import XINXINRPC.com.xin.MyRpc.handler.ServerHandler;
import XINXINRPC.com.xin.MyRpc.EncoderAndDecoder.MyRpcDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class NettyServer {
    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static Set<Object> waitRegisterService = new HashSet<>();

    @Autowired
    private ServerHandler serverHandler;

    @Value("${XinRpc.ServerAdd}")
    private String serverAdd;

    @Value("${XinRpc.ServerPort}")
    private Integer serverPort;

    @Value("${XinRpc.Serializer}")
    private Integer serializerCode;

    //注册带有XinService的服务，以及服务端的启动
    public void automaticRegisterAndStartServer() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverAdd, serverPort);
        for (Object o : waitRegisterService) {
            register(o, inetSocketAddress);
        }
        if (!waitRegisterService.isEmpty()) {
            startServer(serverAdd, serverPort);
        }
    }

    public static void addwaitRegisterService(Map<String, Object> map) {
        for (Object o : map.values()) {
            waitRegisterService.add(o);
        }
    }

    //注册服务，sevice为某个接口的实现类
    public synchronized NettyServer register(Object service, InetSocketAddress inetSocketAddress) {
        serverHandler.register0(service, inetSocketAddress);
        return this;
    }

    //编写一个方法，完成对NettyServer的初始化和启动
    public void startServer(String hostname, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                          @Override
                          protected void initChannel(SocketChannel ch) throws Exception {
                              ChannelPipeline pipeline = ch.pipeline();
                              pipeline.addLast(new MyRpcDecoder());
                              pipeline.addLast(new MyRpcEncoder(serializerCode));
                              pipeline.addLast(serverHandler); //业务处理器
                          }
                      }
                    );
            ChannelFuture channelFuture = serverBootstrap.bind(hostname, port).sync();
            //注册钩子函数
            ShutdownHook.getShutdownHook(new InetSocketAddress(serverAdd, serverPort)).addClearAllHook();
            logger.info("服务提供方开始提供服务~~");
            //阻塞等待关闭事件
            channelFuture.channel().closeFuture().sync();
            logger.info("服务端关闭");
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("服务端优雅关闭");
        }
    }
}

