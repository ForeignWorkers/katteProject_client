package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.auction.AuctionDataBean;
import common.bean.content.ContentStyleBean;
import common.bean.content.ContentStyleComment;
import common.bean.product.ProductInfoBean;
import common.bean.user.UserPaymentBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class StyleService {

    private final RestTemplate rt = new RestTemplate();

    /**
     * 스타일 메타 정보 저장 후 생성된 ID 리턴
     */
    public int save(
            String style_title,
            int imgCount,
            MultipartFile[] images,
            String caption,
            List<String> productTags,
            List<String> hashtags,
            int user_id
    ) throws IOException {
        // 1) 스타일 메타 정보 POST → Integer ID 반환
        ContentStyleBean style = new ContentStyleBean();
        style.setStyle_title(style_title);
        style.setCaption(caption);
        style.setUser_id(user_id);
        style.setImg_count(imgCount);
        style.setHashtags(hashtags);
        style.setProductTag(productTags);

        ResponseEntity<Integer> result = ApiManagers.post(
                "content/style",
                style,
                new TypeReference<Integer>() {}
        );

        if (!ApiManagers.isSuccessful(result) || result.getBody() == null) {
            throw new IOException("스타일 등록 실패: " + result.getStatusCode());
        }
        int styleId = result.getBody();

        // 2) 이미지 일괄 업로드
        uploadMediaFiles(styleId, images);
        System.out.printf("Successfully uploaded style %d with %d images%n", styleId, imgCount);

        //프로덕트 아이디 업로드
        Map<String, String> reqQuery = new HashMap<>();

        reqQuery.put("style_id", Integer.toString(styleId));
        reqQuery.put("product_id", productTags.get(0));

        TypeReference<Boolean> ref = new TypeReference<>(){};

        Boolean productResult = ApiManagers.postQuery("content/style/add_product_id",
                reqQuery,
                ref).getBody();
        if (Boolean.FALSE.equals(productResult))
            System.out.println("프로덕트 아이디 등록 실패");

        return styleId;
    }

    /**
     * 페이지 단위로 스타일 리스트 + imageUrls 가져오기
     */
    public List<ContentStyleBean> getPage(int page, int size) throws IOException {
        // 1) query 파라미터 준비
        Map<String,String> params = Map.of(
                "page",  String.valueOf(page),
                "size",  String.valueOf(size)
        );

        // 2) GET /api/styles?page={page}&size={size}
        ResponseEntity<List<ContentStyleBean>> resp = ApiManagers.get(
                "/api/styles",                        // <-- **슬래시(/)** 붙였습니다.
                params,
                new TypeReference<List<ContentStyleBean>>(){} // <-- 명시적으로 List<ContentStyleBean> 으로
        );

//        // 3) 에러 체크
//        if (!ApiManagers.isSuccessful(resp) || resp.getBody() == null) {
//            throw new IOException("스타일 목록 불러오기 실패: " + resp.getStatusCode());
//        }

        // 4) 결과 반환
        return resp.getBody();
    }
    /**
     * Multipart/form-data 로 한 번에 여러 file 파트를 보내는 메서드
     */
    private void uploadMediaFiles(int styleId, MultipartFile[] files) throws IOException {

        for (int i = 0; i < files.length; i++) {
            Map<String, Object> parts = new HashMap<>();
            parts.put("post_id", styleId+"_"+(i+1));
            parts.put("isStyle", true);
            parts.put("file", files[i]);

            ResponseEntity<String> response = ApiManagers.postFile("media/upload", parts);
            if (!ApiManagers.isSuccessful(response)) {
                System.out.println(response.getBody());
            }
        }
    }

    public boolean toggleLike(int user_id, int style_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));
        params.put("style_id", Integer.toString(style_id));

        TypeReference<Boolean> ref = new TypeReference<>() {};
        ResponseEntity<Boolean> response = ApiManagers.postQuery("content/style/like" ,params ,ref);
        return Boolean.TRUE.equals(response.getBody());
    }

    public ContentStyleBean getStyleById(int style_id) {
        Map<String, String> params = new HashMap<>();
        params.put("style_id", Integer.toString(style_id));
        TypeReference<ContentStyleBean> ref = new TypeReference<>() {};

        ResponseEntity<ContentStyleBean> res = ApiManagers.get("content/style",params,ref);
        return res.getBody();
    }

    public List<Integer> isLikeStyle(int user_id, int size, int offset) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));
        params.put("size", Integer.toString(size));
        params.put("offset", Integer.toString(offset));

        TypeReference<List<ContentStyleBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ContentStyleBean>> res = ApiManagers.get("content/style/like/user",params,ref);

        List<Integer> list = new ArrayList<>();
        for (ContentStyleBean style : res.getBody()) {
            list.add(style.getId());
        }

        return list;
    }

    public List<Integer> isLikeStyleAll(int user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));

        TypeReference<List<ContentStyleBean>> ref = new TypeReference<>() {};
        ResponseEntity<List<ContentStyleBean>> res = ApiManagers.get("content/style/like/userAll", params, ref);

        List<Integer> list = new ArrayList<>();

        List<ContentStyleBean> body = res.getBody();
        if (body != null) {
            for (ContentStyleBean style : body) {
                list.add(style.getId());
            }
        }

        System.out.println("✅ 좋아요한 스타일 ID 목록: " + list);
        return list;
    }

    public List<Integer> getStyleProductId(int style_id) {
        Map<String, String> params = new HashMap<>();
        params.put("style_id", Integer.toString(style_id));

        TypeReference<List<Integer>> ref = new TypeReference<>() {};
        ResponseEntity<List<Integer>> res = ApiManagers.get("content/style/getTagProduct", params, ref);

        System.out.println("✅ 좋아요한 스타일 ID 목록: " + res);
        return res.getBody();
    }

    public ProductInfoBean getProductById(int product_id) {
        Map<String, String> params = new HashMap<>();
        params.put("product_id", Integer.toString(product_id));

        TypeReference<ProductInfoBean> ref = new TypeReference<>() {};
        ResponseEntity<ProductInfoBean> res = ApiManagers.get("product", params, ref);

        return res.getBody();
    }

    public List<ContentStyleComment> getCommentsByStyleId(int style_id) {
        Map<String, String> params = new HashMap<>();
        params.put("style_id", Integer.toString(style_id));
        TypeReference<List<ContentStyleComment>> ref = new TypeReference<>() {};
        ResponseEntity<List<ContentStyleComment>> res = ApiManagers.get("content/style/comment", params, ref);

        return res.getBody();
    }

    public boolean addComment(int user_id, int style_id, String content) {
        ContentStyleComment comment = new ContentStyleComment();
        comment.setUser_id(user_id);
        comment.setStyle_id(style_id);
        comment.setContent(content);
        comment.setCreate_at(new Date());

        TypeReference<String> ref = new TypeReference<>() {};
        ResponseEntity<String> res = ApiManagers.post("content/style/comment", comment, ref);
        return res.getStatusCode().is2xxSuccessful();
    }

    public boolean isExitLikeByUserId(int style_id, int user_id) {
        Map<String, String> params = new HashMap<>();
        params.put("style_id", Integer.toString(style_id));
        params.put("user_id", Integer.toString(user_id));

        TypeReference<Boolean> ref = new TypeReference<>() {};
        ResponseEntity<Boolean> res = ApiManagers.get("content/isStyleLikeExist", params, ref);
        System.out.println(res.getBody());
        System.out.println(res.getStatusCode().is2xxSuccessful());

        return Boolean.TRUE.equals(res.getBody());
    }
}
