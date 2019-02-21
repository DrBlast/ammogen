package com.wavesplatform.steps;

import com.google.common.net.HttpHeaders;
import com.wavesplatform.helpers.NodeDefaults;
import com.wavesplatform.TestVariables;

import java.util.*;
import java.util.stream.Collectors;

public class AmmoSteps extends NodeDefaults {

    public Map<String, String> collectDefaultHeaders(int bodyLength) {
        Map<String, String> headers = new LinkedHashMap<>();
        List<String> hosts = Arrays.asList(TestVariables.getLoadHosts().split(";"));
        Random r = new Random();
        headers.put(HttpHeaders.HOST, hosts.get(r.nextInt(hosts.size())));
        headers.put(HttpHeaders.ACCEPT, "application/json");
        headers.put(HttpHeaders.CONNECTION, "close");

        if (bodyLength != 0) {
            headers.put(HttpHeaders.CONTENT_LENGTH, Integer.toString(bodyLength));
            headers.put(HttpHeaders.CONTENT_TYPE, "application/json");
        }
        return headers;
    }

    public String printPostWithDefaultHeaders(String body, String path, String tag) {
        Map<String, String> headers = collectDefaultHeaders(body.length());
        return printPostOrderRequest(body, path, headers, tag);
    }

    public String printPostOrderRequest(String body, String path, Map<String, String> headers, String tag) {

        String headersString = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));
        String req = String.format("POST %s HTTP/1.1\r\n%s\r\n\r\n%s", path, headersString, body);

        if (tag.equals(""))
            return String.format("%d\n%s\r\n", req.length(), req);
        else
            return String.format("%d %s\n%s\r\n", req.length(), tag.toUpperCase(), req);
    }

    public String printGetWithTimeStampAndSignaure(String path, long timestamp, String signature) {
        Map<String, String> headers = collectDefaultHeaders(0);
        headers.put("Signature", signature);
        headers.put("Timestamp", String.valueOf(timestamp));
        headers.put(HttpHeaders.USER_AGENT, "YaTank");
        String headersString = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));
        String req = String.format("GET %s HTTP/1.1\r\n%s\r\n\r\n", path, headersString);
        return String.format("%d\n%s", req.length(), req);
    }

    public String printGet(String path, String tag) {
        Map<String, String> headers = collectDefaultHeaders(0);
        String headersString = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));
        String req = String.format("GET %s HTTP/1.1\r\n%s\r\n\r\n", path, headersString);
        return String.format("%d %s\n%s", req.length(), tag, req);
    }
}
