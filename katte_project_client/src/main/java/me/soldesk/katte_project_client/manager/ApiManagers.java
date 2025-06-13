package me.soldesk.katte_project_client.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiManagers {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "https://api-katte.jp.ngrok.io/"; // ✅ 수정: http: → http://

    static {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    // ✅ GET
    public static <T> ResponseEntity<T> get(String path, Map<String, String> queryParams, TypeReference<T> typeRef) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
            if (queryParams != null) {
                queryParams.forEach(urlBuilder::addQueryParameter);
            }

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();

            return execute(request, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ POST
    public static <T> ResponseEntity<T> post(String path, Object requestBody, TypeReference<T> typeRef) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .post(body)
                    .build();

            return execute(request, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ PATCH (Query 방식)
    public static <T> ResponseEntity<T> patchQuery(String path, Map<String, String> queryParams, TypeReference<T> typeRef) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
            if (queryParams != null) {
                queryParams.forEach(urlBuilder::addQueryParameter);
            }

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .patch(RequestBody.create(new byte[0])) // 빈 Body 전달
                    .build();

            return execute(request, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ PATCH (Body 방식)
    public static <T> ResponseEntity<T> patchBody(String path, Object requestBody, TypeReference<T> typeRef) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            System.out.println("➡️ PATCH BODY JSON: " + json);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .patch(body)
                    .build();

            return execute(request, typeRef);
        } catch (Exception e) {
            System.err.println("❌ PATCH 요청 중 오류: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // ✅ DELETE (Query 방식)
    public static <T> ResponseEntity<T> deleteQuery(String path, Map<String, String> queryParams, TypeReference<T> typeRef) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
            if (queryParams != null) {
                queryParams.forEach(urlBuilder::addQueryParameter);
            }

            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .delete()
                    .build();

            return execute(request, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ DELETE (Body 방식)
    public static <T> ResponseEntity<T> deleteBody(String path, Object requestBody, TypeReference<T> typeRef) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .delete(body)
                    .build();

            return execute(request, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ 공통 실행 메서드 (TypeReference 기반)
    public static <T> ResponseEntity<T> execute(Request request, TypeReference<T> typeRef) {
        try (Response response = client.newCall(request).execute()) {
            HttpStatus status = HttpStatus.valueOf(response.code());

            if (!response.isSuccessful() || response.body() == null) {
                return new ResponseEntity<>(null, status);
            }

            String responseBody = response.body().string();
            T body = objectMapper.readValue(responseBody, typeRef);
            return new ResponseEntity<>(body, status);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static boolean isSuccessful(ResponseEntity<?> response) {
        return response != null && response.getStatusCode().is2xxSuccessful();
    }
}