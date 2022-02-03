package XINXINRPC.com.xin.MyRpc.NacosUtils;

import XINXINRPC.com.xin.MyRpc.core.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ShutdownHook {
    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static InetSocketAddress inetSocketAddress;

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(InetSocketAddress socketAddress) {
        inetSocketAddress = socketAddress;
        return shutdownHook;
    }

    public void addClearAllHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosServiceRegistry.deregister(inetSocketAddress);
            logger.info("成功注销所有服务");
        }));
    }

}
