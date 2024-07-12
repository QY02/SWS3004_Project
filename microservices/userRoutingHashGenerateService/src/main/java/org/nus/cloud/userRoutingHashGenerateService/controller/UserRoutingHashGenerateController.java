package org.nus.cloud.userRoutingHashGenerateService.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nus.cloud.userRoutingHashGenerateService.config.RoutingRulesConfig;
import org.nus.cloud.userRoutingHashGenerateService.entity.Rule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class UserRoutingHashGenerateController {

    @Resource
    private RoutingRulesConfig routingRulesConfig;

    @RequestMapping(value = "/**")
    public ResponseEntity<byte[]> generateHash(HttpServletRequest request) throws IOException {
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
            if (headerName.equalsIgnoreCase("email")) {
                String email = request.getHeader(headerName);
                int emailHash = Math.abs(email.hashCode()) % 100000000;
                headers.add("routingHash", String.format("%08d", emailHash));
                List<Rule> routingRuleList = routingRulesConfig.getRuleList();
                for (Rule rule : routingRuleList) {
                    if ((emailHash >= rule.getStartHash()) && (emailHash < rule.getEndHash())) {
                        headers.add("routingIndex", String.valueOf(rule.getIndex()));
                        break;
                    }
                }
            }
        }
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(request.getInputStream().readAllBytes(), headers);

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        RestTemplate restTemplate = new RestTemplate();
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
        ResponseEntity<byte[]> responseEntity = restTemplate.execute("http://cluster-ip-service-user.nus-cloud-project.svc.cluster.local:25559" + path, method, requestCallback, responseExtractor);

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