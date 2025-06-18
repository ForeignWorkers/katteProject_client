package me.soldesk.katte_project_client.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiManagers {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE_URL = "http://localhost:9000/"; // ✅ 수정: http: → http://

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

    // ✅ POST QuERY
    public static <T> ResponseEntity<T> postQuery(String path, Map<String, String> queryParams, TypeReference<T> typeRef) {
        try {
            // URL + Query 파라미터 조립
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
            if (queryParams != null) {
                queryParams.forEach(urlBuilder::addQueryParameter);
            }

            HttpUrl urlWithParams = urlBuilder.build();

            // POST지만 바디 없이 보낼 수 있음 (폼 전송이 아닌 URL에 붙여서 전송)
            Request request = new Request.Builder()
                    .url(urlWithParams)
                    .post(RequestBody.create("", null)) // POST인데 바디는 없음 (빈 바디)
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

    //파일 등록 전용 post
    public static ResponseEntity<String> postFile(String path, Map<String, Object> parts) {
        try {
            // Multipart/data 형식으로 http body를 구성할 벌더를 생성
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            // 입력된 파라미터 반복
            for (Map.Entry<String, Object> entry : parts.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof MultipartFile file) {
                    // multipartfile인 경우 파일 본문으로 추가
                    builder.addFormDataPart(
                            entry.getKey(),//파라미터 이름 ex)file
                            file.getOriginalFilename(), //업로드할 파일 이름
                            RequestBody.create(file.getBytes(), MediaType.parse(file.getContentType()))
                    );
                } else {
                    //일반 문자열 파라미터는 그대로 추가
                    builder.addFormDataPart(entry.getKey(), String.valueOf(value));
                }
            }

            //최종 요청 객체 생성 post , body 포함
            Request request = new Request.Builder()
                    .url(BASE_URL + path) // url 세팅 localhost:9000/
                    .post(builder.build()) // post + body
                    .build();

            //실제 요청 수행 및 결과 반환
            return execute(request, new TypeReference<String>() {});
        } catch (Exception e) {
            e.printStackTrace();
            //예외 발생시 500
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static <T> ResponseEntity<T> execute(Request request, TypeReference<T> typeRef) {
        try (Response response = client.newCall(request).execute()) {
            HttpStatus status = HttpStatus.valueOf(response.code());

            if (!response.isSuccessful() || response.body() == null) {
                return new ResponseEntity<>(null, status);
            }

            String responseBody = response.body().string();

            // ✅ 비어 있으면 null 반환
            if (responseBody == null || responseBody.isBlank()) {
                return new ResponseEntity<>(null, status);
            }

            String typeName = typeRef.getType().getTypeName();

            // ✅ 기본 타입 분기 처리
            switch (typeName) {
                case "java.lang.String" -> {
                    @SuppressWarnings("unchecked")
                    T casted = (T) responseBody;
                    return new ResponseEntity<>(casted, status);
                }
                case "java.lang.Boolean", "boolean" -> {
                    @SuppressWarnings("unchecked")
                    T casted = (T) Boolean.valueOf(responseBody);
                    return new ResponseEntity<>(casted, status);
                }
                case "java.lang.Integer", "int" -> {
                    @SuppressWarnings("unchecked")
                    T casted = (T) Integer.valueOf(responseBody);
                    return new ResponseEntity<>(casted, status);
                }
                case "java.lang.Long", "long" -> {
                    @SuppressWarnings("unchecked")
                    T casted = (T) Long.valueOf(responseBody);
                    return new ResponseEntity<>(casted, status);
                }
            }

            // ✅ 그 외엔 JSON 파싱
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