package XINXINRPC.com.xin.MyRpc.LoadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class RoundLoadBalancer implements CommonLoadBalancer {
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances, String serviceName) {
        if (instances == null || instances.size() == 0) {
            return null;
        }
        if(index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
