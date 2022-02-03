package XINXINRPC.com.xin.MyRpc.LoadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistencyHashingLoadBalancer implements CommonLoadBalancer {

    // 虚拟节点的个数
    private static final int VIRTUAL_NUM = 5;

    // 虚拟节点分配，key是hash值，value是虚拟节点服务器名称
    private static SortedMap<Integer, Instance> shards = new TreeMap<Integer, Instance>();

    // 真实节点列表
    private static List<Instance> realNodes = new LinkedList<Instance>();

    @Override
    public Instance select(List<Instance> instances, String serviceName) {
        init(instances);
        return getServer(serviceName);
    }

    //初始化circle中的结点和虚拟结点
    public void init(List<Instance> instances) {
        for (Instance instance : instances) {
            addNode(instance);
        }
    }


    public static Instance getServer(String serviceName) {
        int hash = getHash(serviceName);
        Integer key = null;
        SortedMap<Integer, Instance> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()) {
            key = shards.lastKey();
        } else {
            key = subMap.firstKey();
        }
        return shards.get(key);
    }

    public static void addNode(Instance node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node.getIp() + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, node);
            }
        }
    }

    public static void delNode(Instance node) {
        if (realNodes.contains(node)) {
            realNodes.remove(node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node.getIp() + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
            }
        }
    }

    /**
     * FNV1_32_HASH算法
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
}
