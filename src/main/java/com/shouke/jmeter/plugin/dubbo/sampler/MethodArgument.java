package com.shouke.jmeter.plugin.dubbo.sampler;

import org.springframework.util.StringUtils;
import java.io.Serializable;

// dubbo接口参数实体类，需要实现序列化，否则可能报错
public class MethodArgument implements Serializable {
    private String argumentType;
    private String argumentValue;

    public MethodArgument(String argumentType, String argumentValue) {
        this.setArgumentType(argumentType);
        this.setArgumentValue(argumentValue);
    }

    public void setArgumentType(String argumentType) {
        // 如果参数类型为null，则为设置为null,否则设置为argumentType去掉有空白(空格，tab等)字符后的值
        this.argumentType = (argumentType == null ? null : StringUtils.trimAllWhitespace(argumentType));
    }

    public String getArgumentType() {
        return this.argumentType;
    }

    public String getArgumentValue() {
        return this.argumentValue;
    }


    public void setArgumentValue(String argumentValue) {
//        this.argumentValue = (argumentValue == null ? null : StringUtils.trimAllWhitespace(argumentValue));
        this.argumentValue = (argumentValue == null ? null : argumentValue);
    }
}
