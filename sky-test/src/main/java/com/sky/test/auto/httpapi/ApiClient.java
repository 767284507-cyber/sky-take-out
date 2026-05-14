package com.sky.test.auto.httpapi;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private String baseUrl;
    private Map<String, String> headers = new HashMap<>();

    public ApiClient() {
    }

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }

    public void clearHeaders() {
        headers.clear();
    }

    public <T> ApiResponse<T> get(String path, Class<T> responseType) {
        return execute("GET", path, null, responseType);
    }

    public <T> ApiResponse<T> post(String path, Object requestBody, Class<T> responseType) {
        return execute("POST", path, requestBody, responseType);
    }

    public <T> ApiResponse<T> put(String path, Object requestBody, Class<T> responseType) {
        return execute("PUT", path, requestBody, responseType);
    }

    public <T> ApiResponse<T> delete(String path, Class<T> responseType) {
        return execute("DELETE", path, null, responseType);
    }

    public <T> ApiResponse<T> execute(String method, String path, Object requestBody, Class<T> responseType) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("Base URL is not set");
        }

        String url = baseUrl + path;
        Method httpMethod;
        try {
            httpMethod = Method.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method, e);
        }

        HttpRequest httpRequest = HttpRequest.of(url).method(httpMethod);
        // 设置请求头
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpRequest.header(entry.getKey(), entry.getValue());
        }

        // 设置请求体
        if (requestBody != null) {
            httpRequest.body(JSONUtil.toJsonStr(requestBody), "application/json");
        }

        // 执行请求
        HttpResponse httpResponse = httpRequest.execute();
        String responseBody = httpResponse.body();
        int statusCode = httpResponse.getStatus();

        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(statusCode);

        if (statusCode == 200 && responseBody != null) {
            T data = JSONUtil.toBean(responseBody, responseType);
            response.setData(data);
        } else {
            response.setMsg(responseBody);
        }

        return response;
    }

    /**
     * 关闭资源
     * 由于使用的是Hutool的HttpRequest，它会自动关闭连接，此方法主要用于保持接口一致性
     */
    public void close() {
        // 清理headers
        clearHeaders();
    }
}
