package org.nus.cloud.tokenVerificationService.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.tokenVerificationService.exception.ServiceException;
import org.nus.cloud.tokenVerificationService.utils.RedisUtil;
import org.nus.cloud.tokenVerificationService.utils.Result;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class TokenVerificationController {
    @Resource
    private RedisUtil redisUtil;

    @PostMapping("/**")
    public Object tokenVerification(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        String token = request.getHeader("token");
        if ((token == null) || (token.isBlank())) {
            token = request.getParameter("token");
        }
        if ((token == null) || (token.isBlank())) {
            return Result.error(response, "401", "Invalid token");
        }
        String fullUserId = request.getHeader("fullUserId");
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            fullUserId = request.getParameter("fullUserId");
        }
        if ((fullUserId == null) || (fullUserId.isBlank())) {
            return Result.error(response, "401", "Invalid fullUserId");
        }
        String dbFullUserId = redisUtil.verifyToken(token, true, false);
        if (dbFullUserId == null || !dbFullUserId.equals(fullUserId)) {
            return Result.error(response, "401", "Verification failed");
        }
        else {
            if (request.getRequestURI().equals("/tokenVerification")) {
                return Result.success(response);
            } else {
                return forwardRequest(request);
            }
        }
    }

    public ResponseEntity<byte[]> forwardRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            path += "?" + query;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.add(headerName, request.getHeader(headerName));
        }
        HttpEntity<byte[]> httpEntity;
        try {
            httpEntity = new HttpEntity<>(request.getInputStream().readAllBytes(), headers);
        } catch (IOException e) {
            throw new ServiceException("500", e.getMessage());
        }

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        RestTemplate restTemplate = getRestTemplateWithoutRetry();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(@NotNull ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(@NotNull ClientHttpResponse response) {
            }
        });
        restTemplate.setUriTemplateHandler(factory);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(httpEntity, byte[].class);
        ResponseExtractor<ResponseEntity<byte[]>> responseExtractor = restTemplate.responseEntityExtractor(byte[].class);
        ResponseEntity<byte[]> responseEntity = restTemplate.execute("http://cluster-ip-service-event.nus-cloud-project.svc.cluster.local:25661" + path, method, requestCallback, responseExtractor);

        HttpHeaders responseHeaders = new HttpHeaders();
        Objects.requireNonNull(responseEntity).getHeaders().forEach((key, value) -> {
            if (key.equalsIgnoreCase("Transfer-Encoding")) {
                value = value.stream().filter(s -> !s.equalsIgnoreCase("chunked")).collect(Collectors.toList());
            }
            if ((!value.isEmpty()) && (!key.equalsIgnoreCase("Access-Control-Allow-Origin"))) {
                responseHeaders.put(key, value);
            }
        });
        responseEntity = ResponseEntity.status(responseEntity.getStatusCode()).headers(responseHeaders).body(responseEntity.getBody());
        return responseEntity;
    }

    public RestTemplate getRestTemplateWithoutRetry() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.disableAutomaticRetries();
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}