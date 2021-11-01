package com.patrick.delivery.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author patrick on 4/14/20
 * @project
 */
@Service
public class HttpCall<T> {

    public ResponseEntity<T> sendAPIRequest(String switchUrl, String json, HttpHeaders httpHeaders, Class<T> clazz){
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> httpEntity = new HttpEntity<>(json, httpHeaders);

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(switchUrl)
                .build()
                .encode();

        return restTemplate.exchange(uriComponents.toString(), HttpMethod.PUT, httpEntity, clazz);
    }

    /**
     * get Request
     * */
    public ResponseEntity<T> sendAPIGetRequest(String url, MultiValueMap<String, String> params, HttpHeaders httpHeaders, Class<T> clazz){
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        /*UriComponents uriComponents = UriComponentsBuilder.fromUriString(url)
                .buildAndExpand(params)
                .encode();*/

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParams(params).build();

        return restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, httpEntity, clazz);
    }
}
