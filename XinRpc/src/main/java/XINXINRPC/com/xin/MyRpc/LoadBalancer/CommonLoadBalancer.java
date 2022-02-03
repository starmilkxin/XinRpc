package XINXINRPC.com.xin.MyRpc.LoadBalancer;

import XINXINRPC.com.xin.MyRpc.serializer.CommonSerializer;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface CommonLoadBalancer {
    Logger logger = LoggerFactory.getLogger(CommonLoadBalancer.class);

    Instance select(List<Instance> instances, String serviceName);

    static CommonLoadBalancer getLoadBalancer(Integer code) {
        switch(code) {
            case 1:
                logger.info("负载均衡: 采用轮询算法");
                return new RoundLoadBalancer();
            case 2:
                logger.info("负载均衡: 采用加权随机算法");
                return new RandomLoadBalancer();
            case 3:
                logger.info("负载均衡: 采用一致性hash算法");
                return new ConsistencyHashingLoadBalancer();
            default:
                logger.info("负载均衡: 默认采用加权随机算法");
                return new RandomLoadBalancer();
        }
    }
}
