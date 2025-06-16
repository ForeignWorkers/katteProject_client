package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.auction.AuctionDataBean;
import common.bean.content.ContentShortformBean;
import common.bean.product.ProductCheckResultBean;
import common.bean.product.ProductPerSaleBean;
import common.bean.product.ProductSizeBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductRegisterService {

    public ProductRegisterResult registerProductFlow(
            ContentShortformBean shortformBean,
            ProductSizeBean sizeBean,
            AuctionDataBean auctionBean,
            ProductPerSaleBean perSaleBean,
            ProductCheckResultBean checkResultBean
    ) {
        //1.숏폼 등록
        ResponseEntity<String> shortformRes = ApiManagers.post("content/short", shortformBean, new TypeReference<String>() {});
        if (!ApiManagers.isSuccessful(shortformRes)) {
            return new ProductRegisterResult("숏폼 등록 실패: " + shortformRes.getBody(), -1);
        }

        //2.최신 숏폼 ID 조회 → perSaleBean에 설정
        ResponseEntity<Integer> shortIdRes = ApiManagers.get("content/short/latest", null, new TypeReference<Integer>() {});
        System.out.println(shortIdRes.getBody());
        if (!ApiManagers.isSuccessful(shortIdRes)) {
            return new ProductRegisterResult("숏폼 ID 조회 실패", -1);
        }
        perSaleBean.setShortform_id(shortIdRes.getBody());

        //3.사이즈 등록 (선택적)
        if (sizeBean != null && sizeBean.getSize_value() != null && !sizeBean.getSize_value().isBlank()) {

            Map<String, String> query = Map.of(
                    "product_id", String.valueOf(sizeBean.getProduct_id()),
                    "size_value", sizeBean.getSize_value()
            );

            // 중복 확인
            ResponseEntity<Boolean> existsRes = ApiManagers.get("product/size/exists", query, new TypeReference<Boolean>() {});
            if (Boolean.TRUE.equals(existsRes.getBody())) {
                System.out.println("이미 등록된 사이즈 (등록 생략)");
            } else {
                ResponseEntity<String> sizeRes = ApiManagers.post("product/size", sizeBean, new TypeReference<String>() {});
                if (!ApiManagers.isSuccessful(sizeRes)) {
                    return new ProductRegisterResult("사이즈 등록 실패", -1);
                }
            }

            //항상 정확한 size_id 조회
            ResponseEntity<Integer> sizeIdRes = ApiManagers.get("product/size/id", query, new TypeReference<Integer>() {});
            if (!ApiManagers.isSuccessful(sizeIdRes) || sizeIdRes.getBody() == null) {
                return new ProductRegisterResult("사이즈 ID 조회 실패", -1);
            }

            auctionBean.setProduct_size_id(sizeIdRes.getBody());
        }

        //4.경매 등록
        ResponseEntity<String> auctionRes = ApiManagers.post("auction", auctionBean, new TypeReference<String>() {});
        if (!ApiManagers.isSuccessful(auctionRes)) {
            return new ProductRegisterResult("경매 등록 실패: " + auctionRes.getBody(), -1);
        }

        //5.최신 경매 ID 조회 → perSaleBean에 설정
        ResponseEntity<Integer> auctionIdRes = ApiManagers.get("auction/latest", null, new TypeReference<Integer>() {});
        if (!ApiManagers.isSuccessful(auctionIdRes)) {
            return new ProductRegisterResult("경매 ID 조회 실패", -1);
        }
        perSaleBean.setAuction_data_id(auctionIdRes.getBody());

        //6.최종 판매 등록
        ResponseEntity<String> saleRes = ApiManagers.post("product/sale", perSaleBean, new TypeReference<String>() {});
        if (!ApiManagers.isSuccessful(saleRes)) {
            return new ProductRegisterResult("판매 등록 실패: " + saleRes.getBody(), -1);
        }

        //7.최신 판매 등록 ID 조회 → checkResultBean에 설정
        ResponseEntity<Integer> perSaleIdRes = ApiManagers.get("product/sale/latest", null, new TypeReference<Integer>() {});
        if (!ApiManagers.isSuccessful(perSaleIdRes)) {
            return new ProductRegisterResult("판매 id 조회 실패",-1);
        }
        int perSaleId = perSaleIdRes.getBody();
        checkResultBean.setPer_sale_id(perSaleId);

        checkResultBean.setPer_sale_id(perSaleIdRes.getBody());
        
        //8.검수 요청 수행
        checkResultBean.setCheck_step(ProductCheckResultBean.CheckStep.IN_PROGRESS);
        checkResultBean.setSale_step(ProductCheckResultBean.SaleStep.INSPECTION);

        ResponseEntity<String> inspectionRes = ApiManagers.post("product/inspection", checkResultBean, new TypeReference<String>() {});
        if (!ApiManagers.isSuccessful(inspectionRes)) {
            return new ProductRegisterResult("검수 요청 실패: " + inspectionRes.getBody(),-1);
        }

        return new ProductRegisterResult("상품 등록이 완료되었습니다!", perSaleId);
    }

}
