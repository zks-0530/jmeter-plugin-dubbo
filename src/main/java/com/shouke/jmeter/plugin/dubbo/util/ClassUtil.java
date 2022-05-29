package com.shouke.jmeter.plugin.dubbo.util;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Iterator;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.common.reflect.TypeToken;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

import java.lang.reflect.Method;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
//import java.util.Set;
//import java.util.HashSet;

import com.shouke.jmeter.plugin.dubbo.sampler.MethodArgument;
import org.apache.log4j.Logger;

public class ClassUtil {
    private static Logger logger = Logger.getLogger(ClassUtil.class);  // 获取日志打印器


    public static boolean isBlank(String argumentValue) {
        if (StringUtils.isBlank(argumentValue) || "null".equals(argumentValue.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static void parseParameter(List<String> argumentTypeList, List<Object> argumentValueList, MethodArgument arg) {
        try {
            String argumentType = arg.getArgumentType();
            String tmepArgumentType = argumentType.toLowerCase();
            if ("int".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? 0 : Integer.parseInt(arg.getArgumentValue()));
            } else if ("int[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<int[]>() {}.getType()));
            } else if ("double".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? 0.0d : Double.parseDouble(arg.getArgumentValue()));
            } else if ("double[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<double[]>() {}.getType()));
            } else if ("short".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? 0 : Short.parseShort(arg.getArgumentValue()));
            } else if ("short[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<short[]>() {}.getType()));
            } else if ("float".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? 0.0f : Float.parseFloat(arg.getArgumentValue()));
            } else if ("float[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<float[]>() {}.getType()));
            } else if ("long".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? 0l : Long.parseLong(arg.getArgumentValue()));
            } else if ("long[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<long[]>() {}.getType()));
            } else if ("byte".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? 0 : Byte.parseByte(arg.getArgumentValue()));
            } else if ("byte[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<byte[]>() {}.getType()));
            } else if ("boolean".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? false : Boolean.parseBoolean(arg.getArgumentValue()));
            } else if ("boolean[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<boolean[]>() {}.getType()));
            } else if ("char".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? "\u0000" : arg.getArgumentValue().charAt(0));
            } else if ("char[]".equals(tmepArgumentType)) {
                argumentTypeList.add(argumentType);
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<char[]>() {}.getType()));
            } else if ("java.lang.string".equals(tmepArgumentType) || "string".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.String");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : String.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.string[]".equals(tmepArgumentType) || "string[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.String[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<String[]>() {}.getType()));
            } else if ("java.lang.integer".equals(tmepArgumentType) || "integer".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Integer");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Integer.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.integer[]".equals(tmepArgumentType) || "integer[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Integer[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Integer[]>() {}.getType()));
            } else if ("java.lang.double".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Double");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Double.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.double[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Double[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Double[]>() {}.getType()));
            } else if ("java.lang.short".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Short");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Short.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.short[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Short[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Short[]>() {}.getType()));
            } else if ("java.lang.Long".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Long");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Long.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.long[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Long[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Long[]>() {}.getType()));
            } else if ("java.lang.float".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Float");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Float.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.float[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Float[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Float[]>() {}.getType()));
            } else if ("java.lang.byte".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Byte");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Byte.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.byte[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Byte[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Byte[]>() {}.getType()));
            } else if ("java.lang.boolean".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Boolean");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : Boolean.valueOf(arg.getArgumentValue()));
            } else if ("java.lang.boolean[]".equals(tmepArgumentType)) {
                argumentTypeList.add("java.lang.Boolean[]");
                argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<Boolean[]>() {}.getType()));
            } else {
                if (tmepArgumentType.endsWith("[]")) {
                    List<?> list = null;
                    if (!isBlank(arg.getArgumentValue())) {
                        list = JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<List<?>>() {}.getType());
                    }
                    argumentTypeList.add(arg.getArgumentType());
                    argumentValueList.add(list == null ? null : list.toArray());
                } else {
                    try { // jdk、lib下的类
                        Class<?> clazz = Class.forName(argumentType); // 通过类的全名称（携带包名）获取类的Class对象
                        argumentTypeList.add(arg.getArgumentType());
                        argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), clazz));
                    } catch (ClassNotFoundException e) {
                        //不是jdk或者lib下的类，使用通用map格式反序列化值
                        argumentTypeList.add(arg.getArgumentType());
                        argumentValueList.add(isBlank(arg.getArgumentValue()) ? null : JsonUtil.formJson(arg.getArgumentValue(), new TypeToken<HashMap<String, Object>>() {}.getType()));
                    }
                }
            }
        } catch(Exception e){
            throw new IllegalArgumentException("Invalid parameter => [ParamType=" + arg.getArgumentType() + ",ParamValue=" + arg.getArgumentValue() + "]", e);
        }
    }




    /**
     * 递归遍历指定目录下的文件
     */
    public static void traverseFolder(String dirPath, String filter, List<String> resultList, List<String> reultList2) {
        File file = new File(dirPath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                logger.warn("未获取到文件");
                return;
            } else {
                for (File file2 : files) {
                    String file2AbsolutePath = file2.getAbsolutePath();
                    if (file2.isDirectory()) {
                        logger.debug("正在遍历目录(" + file2AbsolutePath +")下的文件(夹)");
                        traverseFolder(file2AbsolutePath, filter, resultList, reultList2);
                    } else {
                        if (file2AbsolutePath.contains(filter)) { // 文件名包含目标字符串则添加到结果列表
                            logger.debug("获取到文件:" + file2AbsolutePath);
                            resultList.add(file2.getAbsolutePath());
                            reultList2.add(file2.getName());
                        }
                    }
                }
            }
        } else {
            logger.warn("目录：" + dirPath + " 不存在!");
        }
    }

    /**
     * 获取指定目录下的jar包文件
     **/
    public static List<Object> getJarPkgs(String jarDirPath) {
        List<String> resultList = new ArrayList<>(); // 存放文件名（包含绝对路径
        List<String> resultList2 = new ArrayList<>();// 存放文件名（不包含绝对路径
        List<Object> finalResultList = new ArrayList<>();// 存放文件名（不包含绝对路径
        try {
            traverseFolder(jarDirPath, ".jar", resultList, resultList2);
            finalResultList.add(resultList);
            finalResultList.add(resultList2);
        } catch (Exception e) {
            System.out.println(e.toString());
        }finally {
            return finalResultList;
        }

    }

    /**
     * 读取存放jar包里面的类名、方法名过滤器的配置文件
     */
    public static HashMap<String,String[]> getFilters(String configFilePath) {
        File file = new File(configFilePath);
        HashMap<String, String[]> filterMap = new HashMap<>();
        if (file.isFile() && file.exists()) {
            Properties prop = new Properties();
            try {
                //读取属性文件a.properties
                InputStream in = new BufferedInputStream(new FileInputStream(configFilePath));

                prop.load(in);//加载属性列表
                Iterator<String> it = prop.stringPropertyNames().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = org.springframework.util.StringUtils.trimAllWhitespace(prop.getProperty(key).replace("，", ","));
                    logger.debug(key + ":" + value);
                    filterMap.put(key, value.split(","));
                }
                in.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            } finally {
                return filterMap;
            }
        } else {
            logger.warn("配置文件" + configFilePath + "不存在");
            return filterMap;
        }
    }

    /**
     * 转换字符串表达式为Boolean表达式
     */
    public static Boolean getBoolExpresstion(HashMap<String,String[]> filterMap, String keyInclude, String keyExclude, String srcString){
        String resultExpression = "";  // 存放最后结果表达式
        String includeExpression = ""; // 需要满足包含的逻辑字符串表达式
        String excludeExpression = ""; // 需要满足排除的逻辑字符串表达式
        try {
            if(filterMap.containsKey(keyInclude)){ //keyInclude 存在
                String[] arrayValue = filterMap.get(keyInclude);
                for(String s:arrayValue){
                    if (s.length() != 0 ) {
                        includeExpression = String.format(includeExpression + " || \"%s\".contains(\"" + s + "\")", srcString);
                    }
                }
                if (includeExpression.length() != 0) {
                    includeExpression = includeExpression.substring(includeExpression.indexOf("||") + 3);
                } else {
                    includeExpression = "true";
                }
                logger.debug("includeExpression表达式为：" + includeExpression);
            } else {
                includeExpression = "true";
            }

            if(filterMap.containsKey(keyExclude)){ //keyExclude 存在
                String[] arrayValue = filterMap.get(keyExclude);
                for(String s:arrayValue){
                    if (s.length() != 0 ) {
                        excludeExpression = String.format(excludeExpression + " && !\"%s\".contains(\"" + s + "\")", srcString);
                    }
                }
                if (excludeExpression.length() != 0) {
                    excludeExpression = excludeExpression.substring(excludeExpression.indexOf("&&") + 3) ;
                } else {
                    excludeExpression = "true";
                }
                logger.debug("excludeExpression表达式为：" + excludeExpression);
            } else {
                excludeExpression = "true";
            }

            resultExpression = "(" + includeExpression + ")  && "  + "(" + excludeExpression + ")";
            logger.debug("组合后逻辑表达式为：" + resultExpression);
        } catch (Exception e) {
            resultExpression = "true";
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Boolean result = true; // 默认为真
        try {
            result =(Boolean)engine.eval(resultExpression);
        } catch (ScriptException e) {
            logger.error("转换字符串表达式为Boolean表达式出错：" + e.toString());
        } finally {
            return result;
        }
    }

    /**
     * 获取 jar 包中的类名，每个类对应的方法名，及对应方法的参数类型
     */
    public static List<Object> getNamesInJarPkg(String jarPkgPath, String pkgName, HashMap<String,String[]> filterMap) throws Exception {
        List<Object> resultList = new ArrayList<>();
        HashMap<String, String[]> interfaceMap = new HashMap<>();  // 用于存放包名和全类名的映射关系
        List<String> interfaceNameList = new ArrayList<>();        // 用于存放每个包的全类名
        HashMap<String, String[]> methodMap = new HashMap<>();     // 用于存放全类名和方法名的映射关系
        HashMap<String, String[]> argumentMap = new HashMap<>();   // 用于存放方法名和方法参数类型的映射关系
        try{
//            String pkgName = "";   // 存放包名
//            Integer index = jarPkgPath.lastIndexOf("\\");
//            if (index > -1) {
//                pkgName = jarPkgPath.substring(index+1);
//            } else {
//                return resultList; // 返回结果
//            }

            logger.debug("截取的包名为：" + pkgName);

            //通过将给定路径名字符串转换为抽象路径名来创建一个新File实例
            File f = new File(jarPkgPath);
            URL url1 = f.toURI().toURL();
            URLClassLoader myClassLoader = new URLClassLoader(new URL[]{url1},Thread.currentThread().getContextClassLoader());

            //通过jarFile和JarEntry得到所有的类
            JarFile jar = new JarFile(jarPkgPath);
            //返回zip文件条目的枚举
            Enumeration<JarEntry> enumFiles = jar.entries();

            JarEntry entry;
            String classFullName;
            //测试此枚举是否包含更多的元素
            while(enumFiles.hasMoreElements()){
                entry = enumFiles.nextElement();
                classFullName = entry.getName();
                if(getBoolExpresstion(filterMap, "classInclude", "classExclude", classFullName)){
                    if(classFullName.endsWith(".class")){
                        // 去掉后缀.class，并替换 / 为 .
                        String className = classFullName.substring(0,classFullName.length()-6).replace("/", ".");
                        logger.debug("*****************************");
                        logger.debug("全类名:" + className);

                        // 存储类名
                        interfaceNameList.add(className);

                        Class<?> myclass = myClassLoader.loadClass(className);
                        List<String> methodNameList = new ArrayList<>();        // 用于存放每个类的方法名

                        //得到类中包含的方法名称及方法参数类型
                        Method[] methods;
                        try {
                            methods = myclass.getMethods();
//                        Method[] methods = myclass.getDeclaredMethods();
                        } catch (NoClassDefFoundError e) {
                            logger.debug(e.toString());
//                            logger.error(e.toString());
//                            logger.warn(e.toString());
                            methodMap.put(pkgName+className, new String[]{});
                            continue;
                        }

                        for (Method method : methods) {
                            String methodName = method.getName();
                            logger.debug("方法名称:" + methodName);

                            if(getBoolExpresstion(filterMap, "methodInclude", "methodExclude", methodName)){
                                logger.debug("逻辑表达式为真，添加方法名称:" + methodName + "到methodNameList");

                                if(!methodNameList.contains(methodName)){
                                    methodNameList.add(methodName); // 存储方法名称
                                }
//                                List<String> argumentTypeList = new ArrayList<>();        // 用于存放每个方法的参数类型
                                Class<?>[] parameterTypes = method.getParameterTypes();
                                for (Class<?> clas : parameterTypes) {
                                    // String parameterName = clas.getName();
                                    String parameterName = clas.getSimpleName();
                                    logger.debug("参数类型:" + parameterName);
//                                    argumentTypeList.add(parameterName); // 存储参数类型
                                }
                                logger.debug("==========================");

//                                if (!argumentTypeList.isEmpty()) {
//                                    argumentMap.put(pkgName+className+methodName, argumentTypeList.toArray(new String[argumentTypeList.size()]));
//                                } else {
//                                    argumentMap.put(pkgName+className+methodName, new String[]{});
//                                }
                            }
                        }

                        if (!methodNameList.isEmpty()) {
                            methodMap.put(pkgName+className, methodNameList.toArray(new String[methodNameList.size()]));
                        } else {
                            methodMap.put(pkgName+className, new String[]{});
                        }
                    }
                }else{
                    //
                }
            }

            if (!interfaceNameList.isEmpty()) {
                interfaceMap.put(pkgName, interfaceNameList.toArray(new String[interfaceNameList.size()]));
            } else {
                interfaceMap.put(pkgName, new String[]{});
            }

            resultList.add(interfaceMap);
            resultList.add(methodMap);
            resultList.add(argumentMap);
        } catch(IOException e){
            e.printStackTrace();
            resultList.add(interfaceMap);
            resultList.add(methodMap);
            resultList.add(argumentMap);
        } finally {
            return resultList;
        }
    }

}