package org.cs304.backend.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AI-generated-content
 * tool: GitHub Copilot
 * version: latest
 * usage: 我问Copilot如何用springboot实现类似Nginx
 * # Web服务API 代理
 * location /_AMapService/ {
 * set $args "$args&jscode=你的安全密钥";
 * proxy_pass https://restapi.amap.com/;
 * }
 * 的功能，我在生成的代码基础上使用@Value从配置文件里读取了安全代码并将安全代码拼接到请求参数中，同时修了很多bug
 */
@Hidden
@RestController
public class AMapProxyController {

    @Value("${amap-security-js-code:}")
    private String jsCode;

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/_AMapService/**")
    @Operation(summary = "高德地图服务代理", description =
    """
    代理高德地图服务API
    ### 参数 ###
    无
    ### 返回值 ###
    {
      "status": 200,
      "message": "OK",
      "data": "高德地图服务API返回的数据"
    }
    ### 实现逻辑 ###
    1. 代理高德地图服务API
    2. 返回高德地图服务API返回的数据
    """)
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) throws IOException {
        String path = request.getRequestURI().substring("/_AMapService".length());
        String query = request.getQueryString();
        if (query != null) {
            path += "?" + query + "&jscode=" + jsCode;
        } else {
            path += "?jscode=" + jsCode;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.equalsIgnoreCase("Host")) {
                headers.add(headerName, "restapi.amap.com");
            } else {
                headers.add(headerName, request.getHeader(headerName));
            }
        }
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(request.getInputStream().readAllBytes(), headers);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(factory);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(httpEntity, byte[].class);
        ResponseExtractor<ResponseEntity<byte[]>> responseExtractor = restTemplate.responseEntityExtractor(byte[].class);
        ResponseEntity<byte[]> responseEntity = restTemplate.execute("https://restapi.amap.com" + path, method, requestCallback, responseExtractor);

        HttpHeaders responseHeaders = new HttpHeaders();
        Objects.requireNonNull(responseEntity).getHeaders().forEach((key, value) -> {
            if (key.equalsIgnoreCase("Transfer-Encoding")) {
                value = value.stream().filter(s -> !s.equalsIgnoreCase("chunked")).collect(Collectors.toList());
            }
            if (!value.isEmpty()) {
                responseHeaders.put(key, value);
            }
        });
        responseEntity = ResponseEntity.status(responseEntity.getStatusCode()).headers(responseHeaders).body(responseEntity.getBody());
        return responseEntity;
    }
}
