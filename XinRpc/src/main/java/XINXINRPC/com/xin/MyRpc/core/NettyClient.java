package XINXINRPC.com.xin.MyRpc.core;

import XINXINRPC.com.xin.MyRpc.EncoderAndDecoder.MyRpcEncoder;
import XINXINRPC.com.xin.MyRpc.Myannotation.XinReference;
import XINXINRPC.com.xin.MyRpc.NacosUtils.NacosServiceRegistry;
import XINXINRPC.com.xin.MyRpc.NacosUtils.ServiceRegistry;
import XINXINRPC.com.xin.MyRpc.RpcInf.RpcRequest;
import XINXINRPC.com.xin.MyRpc.config.MyBeanFactoryPostProcessor;
import XINXINRPC.com.xin.MyRpc.handler.ClientHandler;
import XINXINRPC.com.xin.MyRpc.EncoderAndDecoder.MyRpcDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NettyClient {
    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    @Value("${XinRpc.Serializer}")
    private Integer serializerCode;

    //创建线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private EventLoopGroup group;

    private static Bootstrap bootstrap;

    private static ClientHandler client = new ClientHandler();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private NacosServiceRegistry serviceRegistry;

    //将带有注解XinReference的属性注入为代理对象
    public void injectProxyObject() {
        List<Class<? extends Annotation>> classes = Arrays.asList(Controller.class, Service.class, Component.class, Repository.class);
        for (Class res : classes) {
            Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(res);
            for (Object bean : beansWithAnnotation.values()) {
                Field[] fields = bean.getClass().getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    if (f.isAnnotationPresent(XinReference.class)) {
                        Class<?> type = f.getType();
                        //获得代理对象
                        Object bean1 = getBean(type);
                        //注入代理对象
                        try {
                            f.set(bean, bean1);
                            logger.info("成功为 {} 的 属性 {} 注入代理对象", bean.getClass().getSimpleName(), f.getType().getSimpleName());
                        } catch (IllegalAccessException e) {
                            logger.info("注入服务失败");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    //编写方法使用代理模式，获取一个代理对象
    public Object getBean(final Class<?> serivceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serivceClass}, (proxy, method, args) -> {
                    //客户端每调用一次, 就会进入到该代码
                    if (bootstrap == null) {
                        initClient();
                        logger.info("初始化客户端成功");
                    }

                    //在服务注册中心中根据负载均衡算法发现服务的某一台服务器的地址和端口
                    InetSocketAddress inetSocketAddress = serviceRegistry.getServiceAdd(serivceClass.getSimpleName());
                    ChannelFuture future = bootstrap.connect(inetSocketAddress.getHostName(), inetSocketAddress.getPort()).sync();
                    logger.info("成功连接服务器: {}", inetSocketAddress.getHostName());

                    //设置要发给服务器端的信息
                    client.setRequest(RpcRequest.builder()
                            .interfaceName(serivceClass.getSimpleName())
                            .methodName(method.getName())
                            .parameters(args)
                            .paramTypes(method.getParameterTypes())
                            .build());

                    //异步执行client中的call()方法，并且获取返回值
                    Object result = executor.submit(client).get();

                    //关闭channel
                    future.channel().close().sync();
                    logger.info("客户端关闭");
                    return result;
                });
    }

    //初始化客户端
    private void initClient() {
        //创建EventLoopGroup
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new MyRpcDecoder());
                                pipeline.addLast(new MyRpcEncoder(serializerCode));
                                pipeline.addLast(client);
                            }
                        }
                );
    }
}


