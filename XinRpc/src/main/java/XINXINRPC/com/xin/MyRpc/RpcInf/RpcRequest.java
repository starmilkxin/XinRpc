package XINXINRPC.com.xin.MyRpc.RpcInf;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

@Data
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 5006564624626218163L;

    /**
     * 待调用接口名称
     */
    private String interfaceName;
    /**
     * 待调用方法名称
     */
    private String methodName;
    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

    public RpcRequest() {

    }

    public RpcRequest(String interfaceName, String methodName, Object[] parameters, Class<?>[] paramTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramTypes = paramTypes;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                '}';
    }
}
