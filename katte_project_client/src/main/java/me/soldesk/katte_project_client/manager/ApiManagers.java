package me.soldesk.katte_project_client.manager;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiManagers {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "http://localhost:9000/";

    public static <T> ResponseEntity<T> get(String path, Map<String, String> queryParams, Class<T> responseType) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
            if (queryParams != null) {
                queryParams.forEach(urlBuilder::addQueryParameter);
            }
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .build();

            return execute(request, responseType);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> ResponseEntity<T> post(String path, Object requestBody, Class<T> responseType) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .post(body)
                    .build();

            return execute(request, responseType);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> ResponseEntity<T> patchQuery(String path, Map<String, String> queryParams, Class<T> responseType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
        if (queryParams != null) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .patch(RequestBody.create(new byte[0])) // PATCH는 body가 반드시 있어야 하므로 빈 body 전달
                .build();

        return execute(request, responseType);
    }


    public static <T> ResponseEntity<T> patchBody(String path, Object requestBody, Class<T> responseType) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            System.out.println("➡️ PATCH BODY JSON: " + json);

            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .patch(body)
                    .build();

            return execute(request, responseType);

        } catch (Exception e) {
            System.err.println("❌ PATCH 요청 중 오류: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 혹은 커스텀 처리
        }
    }

    public static <T> ResponseEntity<T> deleteQuery(String path, Map<String, String> queryParams, Class<T> responseType) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
            if (queryParams != null) {
                queryParams.forEach(urlBuilder::addQueryParameter);
            }
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .delete()
                    .build();

            return execute(request, responseType);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> ResponseEntity<T> deleteBody(String path, Object requestBody, Class<T> responseType) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(BASE_URL + path)
                    .delete(body)
                    .build();

            return execute(request, responseType);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static <T> ResponseEntity<T> execute(Request request, Class<T> responseType) {
        try (Response response = client.newCall(request).execute()) {
            HttpStatus status = HttpStatus.valueOf(response.code());

            if (!response.isSuccessful() || response.body() == null) {
                return new ResponseEntity<>(null, status);
            }

            String responseBody = response.body().string();

            if (responseType == String.class) {
                @SuppressWarnings("unchecked")
                T casted = (T) responseBody;
                return new ResponseEntity<>(casted, status);
            }

            T bodyObject = objectMapper.readValue(responseBody, responseType);
            return new ResponseEntity<>(bodyObject, status);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static boolean isSuccessful(ResponseEntity<?> response) {
        return response != null && response.getStatusCode().is2xxSuccessful();
    }
}