package me.soldesk.katte_project_client.manager;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class ApiManagers {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://localhost:9000/";

    // ✅ GET 요청 - 쿼리 파라미터와 제네릭 응답 처리
    public static <T> ResponseEntity<T> get(String path, Map<String, String> queryParams, Class<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL + path);

        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        String url = uriBuilder.toUriString();

        // 필요 시 헤더 설정 가능

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),  // 필요 시 헤더 설정 가능
                responseType
        );
    }

    // ✅ POST 요청 - 바디 + 제네릭 응답 처리
    public static <T> ResponseEntity<T> post(String path, Object requestBody, Class<T> responseType) {
        String url = BASE_URL + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        System.out.println(entity.getBody());

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                responseType
        );
    }

    // ✅ PATCH 요청 - 바디 + 제네릭 응답 처리
    public static <T> ResponseEntity<T> patchBody(String path, Object requestBody, Class<T> responseType) {
        String url = BASE_URL + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                entity,
                responseType
        );
    }

    // ✅ PATCH 요청 - 쿼리 + 제네릭 응답 처리
    public static <T> ResponseEntity<T> patchQuery(String path, Map<String, String> queryParams, Class<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL + path);

        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        String url = uriBuilder.toUriString();

        // 필요 시 헤더 설정 가능

        return restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(new HttpHeaders()),  // 필요 시 헤더 설정 가능
                responseType
        );
    }

    // ✅ DELETE 요청 - (선택적으로) 바디 포함 가능
    public static <T> ResponseEntity<T> delete(String path, Map<String, String> queryParams, Class<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL + path);

        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        String url = uriBuilder.toUriString();

        // 필요 시 헤더 설정 가능

        return restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(new HttpHeaders()),  // 필요 시 헤더 설정 가능
                responseType
        );
    }

    // ✅ 성공 여부만 체크하는 헬퍼 메서드
    public static boolean isSuccessful(ResponseEntity<?> response) {
        return response != null && response.getStatusCode().is2xxSuccessful();
    }
}