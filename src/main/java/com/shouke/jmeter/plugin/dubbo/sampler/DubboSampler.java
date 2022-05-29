package com.shouke.jmeter.plugin.dubbo.sampler;


import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.IntegerProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.log4j.Logger;

import com.alibaba.dubbo.common.utils.StringUtils;

import com.shouke.jmeter.plugin.dubbo.util.ClassUtil;

/**
 *  JMeter creates an instance of a sampler class for every occurrence of the
 * element in every thread. [some additional copies may be created before the
 * test run starts]
 *
 * Thus each sampler is guaranteed to be called by a single thread - there is no
 * need to synchronize access to instance variables.
 *
 * However, access to class fields must be synchronized.
 *
 */
public class DubboSampler extends AbstractSampler {
    public static String REGISTRY_TYPE = "REGISTRY_TYPE";             // 注册中心类型
    public static String REGISTRY_ADDRESS = "REGISTRY_ADDRESS";       // 注册中心地址
    public static String RPC_PROTOCOL_TYPE = "RPC_PROTOCOL_TYPE";     // rpc协议类型
    public static String RPC_ADDRESS = "RPC_ADDRESS";                 // rpc直连地址
    public static String PACKAGE = "PACKAGE";                         // JAR包名称
    public static String INTERFACE = "INTERFACE";                     // 接口名称
    public static String METHOD = "METHOD";                           // 方法名称
    public static String INTERFACEINPUT = "INTERFACEINPUT";           // 接口名称(手工输入的接口名称)
    public static String METHODINPUT = "METHODINPUT";                 // 方法名称（手工输入的方法名称）
    public static String METHOD_ARGS_SIZE = "METHOD_ARGS_SIZE";       // 方法参数个数
    public static String VERSION = "VERSION";                         // 接口服务版本
    public static String GROUP = "GROUP";                             // 接口服务分组
    public static String LOADBALANCE = "LOADBALANCE";                 // 负载均衡策略
    public static String SYNC = "SYNC";                               // 是否同步访问
    public static String COLLECTIONS = "COLLECTIONS";                 // 消费者连接数
    public static String TIMEOUT = "TIMEOUT";                         // 请求接口超时
    public static String CLUSTER = "CLUSTER";                         // 集群容错模式
    public static String RETRIES = "RETRIES";                         // 失败重试次数

    private static final long serialVersionUID = 240L;

    private GenericService genericService;                            // 存放泛型服务
    private static ApplicationConfig application =  new ApplicationConfig();  // 所有采样共享一份消费者应用，以节约内存
    static  {
        application.setName("dubboSampler");  // 设置消费者应用名称
    }
    private ReferenceConfig<GenericService> ref;                          // 存放引用
    private boolean reSample;                                             // 用于标记是否重新执行压测
    private String method;                                                // 存放接口方法
    private StringBuilder stringBuilder;                                  // 用于记录请求基础数据（方法参数类型及参数值外）
    List<Object> sampleResult;                                            // 存放请求结果

    private static Logger logger = Logger.getLogger(DubboSampler.class);  // 获取日志打印器

    public DubboSampler() {
        logger.debug("初始化DubboSampler");
        reSample = true;
        ref = null;
        genericService = null;
        method = "";
        stringBuilder = null;
        sampleResult = null;
    }

    // 获取注册中心类型
    public String getRegistryType() {
        return this.getPropertyAsString(REGISTRY_TYPE);
    }

    // 设置注册中心类型
    public void setRegistryType(String registryType) {
        this.setProperty(new StringProperty(REGISTRY_TYPE, org.springframework.util.StringUtils.trimAllWhitespace(registryType)));
    }

    // 获取注册中心地址
    public String getRegistryAddress() {
        String address = this.getPropertyAsString(REGISTRY_ADDRESS);
        String registryAddress = address;
        int index = address.indexOf("://");
        if (index > -1) {
            index = index + 3;
            registryAddress = address.substring(index);
        }
        logger.debug("调用 getRegistryAddress 方法，返回注册中心地址,：" + registryAddress);
        return registryAddress;
//        return this.getPropertyAsString(REGISTRY_ADDRESS);
    }

    // 设置注册中心地址
    public void setRegistryAddress(String registryAddress) {
        registryAddress = org.springframework.util.StringUtils.trimAllWhitespace(registryAddress);
        String address = registryAddress;
        int index = registryAddress.indexOf("://");
        if (index > -1) {
            index = index + 3;
            address = registryAddress.substring(index);
        }
        logger.debug("调用 setRegistryAddress 方法，设置注册中心地址为：" + address);

        this.setProperty(new StringProperty(REGISTRY_ADDRESS, address));
//        this.setProperty(new StringProperty(REGISTRY_ADDRESS, org.springframework.util.StringUtils.trimAllWhitespace(registryAddress)));
    }


    // 获取RPC协议类型
    public String getRpcProtocolType() {
        return this.getPropertyAsString(RPC_PROTOCOL_TYPE);
    }

    // 设置RPC协议类型
    public void setRpcProtocolType(String rpcProtocolType) {
        this.setProperty(new StringProperty(RPC_PROTOCOL_TYPE, org.springframework.util.StringUtils.trimAllWhitespace(rpcProtocolType)));
    }

    // 获取RPC直连地址
    public String getRpcAddress() {
        String address = this.getPropertyAsString(RPC_ADDRESS);
        String rpcAddress = address;
        int index = address.indexOf("://");
        if (index > -1) {
            index = index + 3;
            rpcAddress = address.substring(index);
        }
        logger.debug("调用 getRpcAddress 方法，获取rpc直连地址为：" + rpcAddress);
        return rpcAddress;
//        return this.getPropertyAsString(RPC_ADDRESS);

    }

    // 设置RPC直连地址
    public void setRpcAddress(String rpcAdress) {
        rpcAdress = org.springframework.util.StringUtils.trimAllWhitespace(rpcAdress);
        String address = rpcAdress;
        int index = rpcAdress.indexOf("://");
        if (index > -1) {
            index = index + 3;
            address = rpcAdress.substring(index);
        }
        logger.debug("调用 setRpcAddress 方法，设置rpc直连地址为：" + address);
        this.setProperty(new StringProperty(RPC_ADDRESS, address));
//        this.setProperty(new StringProperty(RPC_ADDRESS, org.springframework.util.StringUtils.trimAllWhitespace(rpcAdress)));
    }


    // 获取JAR包名称
    public String getPackage() {
        return this.getPropertyAsString(PACKAGE);
    }

    // 设置JAR包名称
    public void setPackage(String packageName) {
        this.setProperty(new StringProperty(PACKAGE, org.springframework.util.StringUtils.trimAllWhitespace(packageName)));
    }

    // 获取接口名称
    public String getInterface() {
        if (StringUtils.isBlank(org.springframework.util.StringUtils.trimAllWhitespace(this.getPropertyAsString(INTERFACEINPUT)))){ // 手工输入的接口名称为空
            return this.getPropertyAsString(INTERFACE);
        } else {
            return this.getPropertyAsString(INTERFACEINPUT);
        }
    }

//    // 设置接口名称
//    public void setInterface(String dubboInterface) {
//        this.setProperty(new StringProperty(INTERFACE, org.springframework.util.StringUtils.trimAllWhitespace(dubboInterface)));
//    }

    // 获取接口名称(下拉选取)
    public String getInterface1() {
        return this.getPropertyAsString(INTERFACE);
    }

    // 设置接口名称（下拉选取）
    public void setInterface1(String dubboInterface) {
        this.setProperty(new StringProperty(INTERFACE, org.springframework.util.StringUtils.trimAllWhitespace(dubboInterface)));
    }

    // 获取接口名称(手工输入)
    public String getInterface2() {
        return this.getPropertyAsString(INTERFACEINPUT);
    }

    // 设置接口名称(手工输入)
    public void setInterface2(String dubboInterface) {
        this.setProperty(new StringProperty(INTERFACEINPUT, org.springframework.util.StringUtils.trimAllWhitespace(dubboInterface)));
    }

    // 获取接口方法
    public String getMethod() {
        if (StringUtils.isBlank(org.springframework.util.StringUtils.trimAllWhitespace(this.getPropertyAsString(METHODINPUT)))){ // 手工输入的接口名称为空
            return this.getPropertyAsString(METHOD);
        } else {
            return this.getPropertyAsString(METHODINPUT);
        }
    }

//    // 设置接口方法
//    public void setMethod(String method) {
//        this.setProperty(new StringProperty(METHOD, org.springframework.util.StringUtils.trimAllWhitespace(method)));
//    }

    // 设置接口方法(下拉选取)
    public void setMethod1(String method) {
        this.setProperty(new StringProperty(METHOD, org.springframework.util.StringUtils.trimAllWhitespace(method)));
    }
    // 获取接口方法(下拉选取)
    public String getMethod1() {
        return this.getPropertyAsString(METHOD);
    }

    // 设置接口方法(手工输入)
    public void setMethod2(String method) {
        this.setProperty(new StringProperty(METHODINPUT, org.springframework.util.StringUtils.trimAllWhitespace(method)));
    }

    // 获取接口方法(手工输入)
    public String getMethod2() {
        return this.getPropertyAsString(METHODINPUT);
    }



    // 获取接口版本
    public String getVersion() {
        return this.getPropertyAsString(VERSION);
    }

    // 设置接口版本
    public void setVersion(String version) {
        this.setProperty(new StringProperty(VERSION, org.springframework.util.StringUtils.trimAllWhitespace(version)));
    }


    // 获取接口服务分组
    public String getGroup() {
        return this.getPropertyAsString(GROUP);
    }

    // 设置接口服务分组
    public void setGroup(String group) {
        this.setProperty(new StringProperty(GROUP, org.springframework.util.StringUtils.trimAllWhitespace(group)));
    }

    // 获取负载均衡策略
    public String getLoadbalance() {
        return this.getPropertyAsString(LOADBALANCE);
    }

    // 设置负载均衡策略
    public void setLoadbalance(String loadbalance) {
        this.setProperty(new StringProperty(LOADBALANCE, org.springframework.util.StringUtils.trimAllWhitespace(loadbalance)));
    }

    // 获取是否同步请求
    public String getSync() {
        return this.getPropertyAsString(SYNC);
    }

    // 设置是否同步请求
    public void setSync(String if_sync) {
        this.setProperty(new StringProperty(SYNC, org.springframework.util.StringUtils.trimAllWhitespace(if_sync)));
    }

    // 获取消费者连接数
    public String getConnections() {
        return this.getPropertyAsString(COLLECTIONS);
    }

    // 设置消费者连接数
    public void setConnections(String collections) {
        this.setProperty(new StringProperty(COLLECTIONS, org.springframework.util.StringUtils.trimAllWhitespace(collections)));
    }

    // 获取请求接口超时
    public String getTimeout() {
        return this.getPropertyAsString(TIMEOUT);
    }

    // 设置请求接口超时
    public void setTimeout(String timeout) {
        this.setProperty(new StringProperty(TIMEOUT, org.springframework.util.StringUtils.trimAllWhitespace(timeout)));
    }

    // 获取集群容错模式
    public String getCluster() {
        return this.getPropertyAsString(CLUSTER);
    }

    // 设置集群容错模式
    public void setCluster(String cluster) {
        this.setProperty(new StringProperty(CLUSTER, org.springframework.util.StringUtils.trimAllWhitespace(cluster)));
    }


    // 获取失败重试次数
    public String getRetries() {
        return this.getPropertyAsString(RETRIES);
    }

    // 设置失败重试次数
    public void setRetries(String retries) {
        this.setProperty(new StringProperty(RETRIES, org.springframework.util.StringUtils.trimAllWhitespace(retries)));
    }

//    // 获取方法参数
//    public List<MethodArgument> getMethodArguments() {
//        int methodArgCnt = this.getPropertyAsInt(METHOD_ARGS_SIZE, 0); // 如果不存在METHOD_ARGS_SIZE 属性则返回默认值0，否则以返回int型的METHOD_ARGS_SIZE的值
//        List<MethodArgument> list = new ArrayList<MethodArgument>(); //用于存储方法参数,形如 [[argType1, value1],[argType2, value2], ...]
//        for (int i=1; i<= methodArgCnt; i++) {
//            String argumentType = this.getPropertyAsString("ARGUMENT_TYPE" + i);
//            String argumentValue = this.getPropertyAsString("ARGUMENT_AVLUE" + i);
//            MethodArgument methodArgument = new MethodArgument(argumentType, argumentValue);
//            list.add(methodArgument);
//        }
//
//        return list;
//    }

    // 获取方法参数
    public Vector<MethodArgument> getMethodArguments() {
        int methodArgCnt = this.getPropertyAsInt(METHOD_ARGS_SIZE, 0); // 如果不存在METHOD_ARGS_SIZE 属性则返回默认值0，否则以返回int型的METHOD_ARGS_SIZE的值
        Vector<MethodArgument> vector = new Vector<>(); //用于存储方法参数,形如 [[argType1, value1],[argType2, value2], ...]
        for (int i=1; i<= methodArgCnt; i++) {
            String argumentType = this.getPropertyAsString("ARGUMENT_TYPE" + i);
            String argumentValue = this.getPropertyAsString("ARGUMENT_AVLUE" + i);
            MethodArgument methodArgument = new MethodArgument(argumentType, argumentValue);
            vector.add(methodArgument);
        }
        return vector;
    }

    // 设置方法参数
    public void setMethodArguments(Vector<MethodArgument> methodArguments) {
        int methodArgCnt = (methodArguments == null ? 0: methodArguments.size()); // 获取参数个数
        this.setProperty(new IntegerProperty(METHOD_ARGS_SIZE, methodArgCnt));

        if (methodArgCnt > 0) {
            for (int i=1; i<=methodArgCnt; i++) {
                this.setProperty(new StringProperty("ARGUMENT_TYPE" + i, methodArguments.get(i-1).getArgumentType()));
                this.setProperty(new StringProperty("ARGUMENT_AVLUE" + i, methodArguments.get(i-1).getArgumentValue()));
            }
        }

    }


    // 调用 Dubbo
    private List<Object> invokeDubbo() {
        logger.debug("调用 invokeDubbo 方法");
        if (sampleResult != null) {
            sampleResult.clear();
        }
        try {

            if (reSample) {  // 每次重新执行压测，针对每个线程，都只运行一次，以获取配置相关数据（方法参数类型，参数值除外）
                Boolean directInvoke = false; // 标记是否采用Dubbo直连
                reSample = false;
                stringBuilder = new StringBuilder();
                sampleResult = new ArrayList();

                RegistryConfig registry = new RegistryConfig();
                String registryType = getRegistryType();
                String registryAddress = getRegistryAddress();
                String rpcAddress = getRpcAddress();
                String rpcProtocolType = getRpcProtocolType();
                if (!registryType.equals("none")) { // 不采用dubbo直连
                    stringBuilder.append("注册中心类型：").append(registryType).append("\n");
                    stringBuilder.append("注册中心地址：").append(registryAddress).append("\n");
                    if (StringUtils.isBlank(registryAddress)) {
                        sampleResult.add(false);
                        sampleResult.add("请求失败，注册中心地址不能为空");
                        sampleResult.add(null); // 参数类型
                        sampleResult.add(null); // 参数值
                        return sampleResult;
                    }
                    registry.setAddress(registryType + registryAddress);
                }else{
                    directInvoke = true;
                    stringBuilder.append("RPC协议类型：").append(rpcProtocolType).append("\n");
                    stringBuilder.append("RPC直连地址：").append(rpcAddress).append("\n");
                    if (StringUtils.isBlank(rpcAddress)) {
                        sampleResult.add(false);
                        sampleResult.add("请求失败，\"RPC直连地址\"不能为空");
                        sampleResult.add(null); // 参数类型
                        sampleResult.add(null); // 参数值
                        return sampleResult;
                    }
                }

                String interfaceName = getInterface();
                stringBuilder.append("接口名称：").append(interfaceName).append("\n");
                if (StringUtils.isBlank(interfaceName)) {
                    sampleResult.add(false);
                    sampleResult.add("请求失败，\"接口名称\"不能为空");
                    sampleResult.add(null); // 参数类型
                    sampleResult.add(null); // 参数值
                    return sampleResult;
                }

                method = getMethod();
                if (StringUtils.isBlank(method)) {
                    stringBuilder.append("方法名称：").append(method).append("\n");
                    sampleResult.add(false);
                    sampleResult.add("请求失败，\"方法名称\"不能为空");
                    sampleResult.add(null); // 参数类型
                    sampleResult.add(null); // 参数值
                    return sampleResult;
                }
                stringBuilder.append("方法名称：").append(method).append("\n");

                String version = getVersion();
                stringBuilder.append("接口服务版本：").append(version).append("\n");
                String group = getGroup();
                stringBuilder.append("接口服务分组：").append(group).append("\n");
                String loadBalance = getLoadbalance();
                stringBuilder.append("负载均衡策略：").append(loadBalance).append("\n");
                String syncSetting = getSync();
                stringBuilder.append("是否同步访问：").append(syncSetting).append("\n");

                Integer connections = 0;
                String temp = getConnections();
                try {
                    if (!StringUtils.isBlank(temp)) {
                        connections = Integer.valueOf(temp);
                    }
                } catch (NumberFormatException e) {
                    stringBuilder.append("消费者连接数：").append(temp).append("\n");
                    sampleResult.add(false);
                    sampleResult.add("请求失败，\"消费者连接数\"只能为整数");
                    sampleResult.add(null); // 参数类型
                    sampleResult.add(null); // 参数值
                    return sampleResult;
                }
                stringBuilder.append("消费者连接数：").append("" + connections).append("\n");


                Integer timeout = 0;
                temp = getTimeout();
                try {
                    if (!StringUtils.isBlank(temp)) {
                        timeout = Integer.valueOf(temp);
                    }
                } catch (NumberFormatException e) {
                    stringBuilder.append("请求接口超时：").append(temp).append("\n");
                    sampleResult.add(false);
                    sampleResult.add("请求失败，\"请求接口超时\"只能为整数");
                    sampleResult.add(null); // 参数类型
                    sampleResult.add(null); // 参数值
                    return sampleResult;
                }
                stringBuilder.append("请求接口超时：").append("" + timeout).append("\n");

                String cluster = getCluster();
                stringBuilder.append("集群容错模式：").append(cluster).append("\n");

                Integer retries = 0;
                temp = getRetries();
                try {
                    if (!StringUtils.isBlank(temp)) {
                        retries = Integer.valueOf(temp);
                    }
                } catch (NumberFormatException e) {
                    stringBuilder.append("失败重试次数：").append(temp).append("\n");
                    sampleResult.add(false);
                    sampleResult.add("请求失败，\"失败重试次数\"只能为整数");
                    sampleResult.add(null); // 参数类型
                    sampleResult.add(null); // 参数值
                    return sampleResult;
                }
                stringBuilder.append("失败重试次数：").append("" + retries);



                ref  = new ReferenceConfig<>();
                ref.setApplication(application);
                if (!directInvoke) {
                    logger.debug("通过注册中心访问");
                    ref.setRegistry(registry);
                } else {
                    logger.debug("Dubbo直连访问");
                    // ref.setProtocol(rpcProtocolType);
                    ref.setUrl(rpcProtocolType + rpcAddress);
                }

                ref.setInterface(interfaceName);

                if (!StringUtils.isBlank(version)) {
                    ref.setVersion(version);
                }

                if (!StringUtils.isBlank(group)) {
                    ref.setGroup(group);
                }

                if (!StringUtils.isBlank(loadBalance)) {
                    ref.setLoadbalance(loadBalance);
                }

                if (!StringUtils.isBlank(syncSetting)) {
                    ref.setAsync(syncSetting.equals("True") ? false : true);
                }
                ref.setConnections(connections);

                ref.setTimeout(timeout);

                if (!StringUtils.isBlank(cluster)) {
                    ref.setCluster(getCluster());
                }

                ref.setRetries(retries);

                ref.setGeneric(true); // 声明为泛化接口

                genericService = ref.get();

            }

            List<MethodArgument> args = getMethodArguments();

            List<String> argumentTypeList =  new ArrayList<>();
            List<Object> argumentValueList = new ArrayList<>();
            for(MethodArgument arg : args) { // 循环遍历参数对象，获取参数类型和参数值
                ClassUtil.parseParameter(argumentTypeList, argumentValueList, arg);
            }

            String[] argumentTypes = null;  // 存放参数类型，供 $invoke方法调用使用
            Object[] argumentValues = null; // 存放参数值，供 $invoke方法调用使用

            // 泛型调用传参，参数类型和参数值为数组形式，所以需要转换list为数组
            argumentTypes = argumentTypeList.toArray(new String[argumentTypeList.size()]);
            argumentValues = argumentValueList.toArray(new Object[argumentValueList.size()]);

            Object res = null;
            try {
                if (genericService != null) {
                    res = genericService.$invoke(method, argumentTypes, argumentValues);
                    sampleResult.add(true);
                    sampleResult.add(res);
                    sampleResult.add(argumentTypes);
                    sampleResult.add(argumentValues);
                } else {
                    throw new Exception("java.lang.NullPointerException caused by genericService, make sure the zookeeper address is right");
                }
//                logger.debug(Arrays.toString(argumentTypes));
//                logger.debug(Arrays.toString(argumentValues));
                return sampleResult;
            } catch (Exception e) {
                logger.error("RpcException：", e);
                sampleResult.add(false);
                sampleResult.add("Rpc调用异常：" + e.toString());
                sampleResult.add(argumentTypes);
                sampleResult.add(argumentValues);
                return sampleResult;
            }
        } catch (Exception e ) {
            logger.error("UnknownException：", e);
            sampleResult.add(false);
            sampleResult.add(e.toString());
            sampleResult.add(null);
            sampleResult.add(null);
            return sampleResult;
        }
    }


    public SampleResult sample(Entry e) {
        logger.debug("调用 sample");
        SampleResult res = new SampleResult();
        boolean isOK = false;//测试结果标记位
        res.setSampleLabel(getTitle()); // 设置采样标签
        res.sampleStart(); // 开始统计响应时间

        /*
         * 执行采样
         */
        try {

            /*
             * Set up the sample sampleResult details
             */

            List<Object> responseData = invokeDubbo();
            isOK = (Boolean)responseData.get(0);
            Object data = responseData.get(1);
            String[] argumentTypes = (String[])responseData.get(2);  // 存放参数类型
            Object[] argumentValues = (Object[])responseData.get(3); // 存放参数值

            res.setResponseData(data.toString(), "utf-8");
            res.setDataType(SampleResult.TEXT);
            if (isOK) {
                res.setResponseCodeOK();
                res.setResponseMessage("OK");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(stringBuilder.toString());

            sb.append("\n参数类型：\n");
            if (argumentTypes != null) {
                for (Object item: argumentTypes) {
                    sb.append(item.toString() + "\n");
                }
            }

            sb.append("\n参数值：\n");
                if (argumentValues != null) {
                for (Object item: argumentValues) {
                    sb.append(item.toString()).append("\n");
                }
            }
            res.setSamplerData(sb.toString()); // 设置请求数据
        } catch (Exception ex) {
            String responseMessage = ex.toString();
            logger.debug(responseMessage);
            res.setResponseMessage(responseMessage);
            res.setResponseCode("500");// $NON-NLS-1$
        } finally {
            res.sampleEnd(); // End timimg
            res.setSuccessful(isOK);
        }

        return res;
    }

    /**
     * @return a string for the sampleResult Title
     */
    private String getTitle() {
        return this.getName();
    }

    /*
    * 释放引用
    * */
    @Override
    protected void finalize() throws java.lang.Throwable{
        try {
            if (ref != null) {
                ref.destroy();
            }
        } finally {
            super.finalize();
        }
    }


//    /*
//     * Helper method
//     */
//    private void trace(String s) {
//        String tl = getTitle();
//        String tn = Thread.currentThread().getName();
//        String th = this.toString();
//        logger.debug(tn + " (" + classCount.get() + ") " + tl + " " + s + " " + th);
//    }
}