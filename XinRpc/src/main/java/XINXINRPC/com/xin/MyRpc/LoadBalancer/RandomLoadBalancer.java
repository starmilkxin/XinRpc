package XINXINRPC.com.xin.MyRpc.LoadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements CommonLoadBalancer {
    //加权随机负载均衡算法
    @Override
    public Instance select(List<Instance> instances, String serviceName) {
        int weightAll = 0;
        boolean sameWeight = true;
        int previousWeight = 0;
        for (int i = 0; i < instances.size(); i++) {
            Instance instance = instances.get(i);
            int curWeight = (int)instance.getWeight();
            if (i > 0 && previousWeight != curWeight) {
                sameWeight = false;
            }
            weightAll += curWeight;
            previousWeight = curWeight;
        }
        if (weightAll > 0 && !sameWeight) {
            int offset = new Random().nextInt(weightAll);
            for (Instance instance : instances) {
                offset -= instance.getWeight();
                if (offset < 0) {
                    return instance;
                }
            }
        }
        return instances.get(new Random().nextInt(instances.size()));
    }
}
