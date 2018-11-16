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

    public static void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public static Object getVariable(String name) {
        if (variables.get(name) != null)
            return variables.get(name);
        else
            throw new Error(String.format("Variable %s not found, name"));
    }

    public static String getTestSeed() {
        if (variables.get("test-seed") != null)
            return (String) TestVariables.getVariable("test-seed");
        else
            throw new Error("Variable 'test-defaultTestSeed' not found");
    }

    public static String getRichSeed() {
        if (variables.get("secondSeed") != null)
            return (String) TestVariables.getVariable("secondSeed");
        else
            throw new Error("Variable 'secondSeed' not found");
    }

    public static String getThirdSeed() {
        if (variables.get("thirdSeed") != null)
            return (String) TestVariables.getVariable("thirdSeed");
        else
            return "";
    }

    public static String getSecondAddress() {
        if (variables.get("secondAddress") != null)
            return (String) TestVariables.getVariable("secondAddress");
        else
            throw new Error("Variable 'secondAddress' not found");
    }

    public static Byte getSchemaByte() {
        if (variables.get("schema") != null)
            if (TestVariables.getVariable("schema").equals("D"))
                return 68;
            else if (TestVariables.getVariable("schema").equals("W"))
                return 87;
            else if (TestVariables.getVariable("schema").equals("W"))
                return 84;

        throw new Error("Variable 'secondAddress' not found");
    }

    public static byte getChainId() {
        if (variables.get("schema") != null)
            return (byte) ((String) TestVariables.getVariable("schema")).charAt(0);
        else
            throw new Error("Variable 'secondAddress' not found");
    }

    public static String getDefaultContentType() {
        if (variables.get("defaultContentType") != null)
            return (String) TestVariables.getVariable("defaultContentType");
        else {
            TestVariables.setVariable("defaultContentType", CONTENT_TYPE);
            return CONTENT_TYPE;
        }

    }

    public static String getAssetId() {
        if (variables.get("assetId") != null)
            return (String) TestVariables.getVariable("assetId");
        else
            throw new Error("Variable 'assetId' not found");
    }

    public static String getHost() {
        if (variables.get("host") != null)
            return (String) TestVariables.getVariable("host");
        else
            throw new Error("Variable 'host' not found");
    }

    public static String getProtocol() {
        if (variables.get("protocol") != null)
            return (String) TestVariables.getVariable("protocol");
        else
            throw new Error("Variable 'protocol' not found");
    }


    public static String getApiKey() {
        if (variables.get("api-key") != null)
            return (String) TestVariables.getVariable("api-key");
        else
            throw new Error("Variable 'api-key' not found");
    }

    public static String getMatcherUrl() {
        if (variables.get("matcher") != null)
            return (String) TestVariables.getVariable("matcher");
        else
            throw new Error("Variable 'matcher' not found");

    }

    public static String getBtcAssetId() {
        if (variables.get("btc") != null)
            return (String) TestVariables.getVariable("btc");
        else
            throw new Error("Variable 'btc' not found");
    }


    public static String getLoadHosts() {
        if (variables.get("loadhosts") != null)
            return (String) TestVariables.getVariable("loadhosts");
        else
            throw new Error("Variable 'loadhosts' not found");
    }
}
