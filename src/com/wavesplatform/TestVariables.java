package com.wavesplatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestVariables {

    private static Map<Object, Object> variables = new HashMap<>();
    private static final String CONTENT_TYPE = "application/json";

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File("./" + System.getProperty("env") + ".properties")));

        } catch (IOException e) {
            throw new IllegalStateException("There is no such properties file" +
                    System.getProperty("env") + ".properties", e);
        }
        Enumeration em = properties.keys();
        while (em.hasMoreElements()) {
            try {
                String property = (String) em.nextElement();
                properties.put(property, System.getProperty(property));
            } catch (NullPointerException ignore) {
            }
        }
        variables.putAll(properties);
    }


    public static Map<Object, Object> getVariables() {
        return variables;
    }

    public static Object setVariable(String name, Object value) {
        variables.put(name, value);
        return getVariable(name);
    }

    public static Object getVariable(String name) {
        if (variables.get(name) == null)
            throw new Error(String.format("Variable '%s' not found", name));

        return variables.get(name);
    }

    public static String getTestSeed() {
        return (String) getVariable("test-seed");
    }

    public static String getRichSeed() {
        return (String) getVariable("secondSeed");
    }

    public static String getThirdSeed() {
        return (String) getVariable("thirdSeed");
    }

    public static String getSecondAddress() {
        return (String) getVariable("secondAddress");
    }

    public static byte getChainId() {
        String schema = getVariable("schema").toString();
        if (schema.length() != 1 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(schema.charAt(0)) < 0) {
            throw new Error("Value of 'schema' must be a single letter");
        }
        return (byte) schema.charAt(0);
    }

    public static String getDefaultContentType() {
        try {
            return (String) getVariable("defaultContentType");
        } catch(Error e) {
            return (String) setVariable("defaultContentType", CONTENT_TYPE);
        }
    }

    public static String getAssetId() {
        return (String) getVariable("assetId");
    }

    public static String getHost() {
        return (String) getVariable("host");
    }

    public static String getProtocol() {
        return (String) getVariable("protocol");
    }

    public static String getApiKey() {
        return (String) getVariable("api-key");
    }

    public static String getMatcherUrl() {
        return (String) getVariable("matcher");
    }

    public static String getBtcAssetId() {
        return (String) getVariable("btc");
    }

    public static String getLoadHosts() {
        return (String) getVariable("loadhosts");
    }
}
