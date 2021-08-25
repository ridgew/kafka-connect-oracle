package com.ecer.kafka.connect.oracle.errorHandler;
import java.util.regex.Pattern;

public abstract class RegexDataHandler implements DataHandler {

    protected final Pattern hexRegex;

    protected RegexDataHandler(final String hexRegex) {
        this.hexRegex = Pattern.compile(hexRegex, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 获取当前的匹配模型
     * @return
     */
    public Pattern getPattern() {
        return hexRegex;
    }
    
}
