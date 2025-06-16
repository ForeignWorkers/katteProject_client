package me.soldesk.katte_project_client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import common.bean.auction.AuctionDataBean;
import common.bean.product.ProductPerSaleBean;
import me.soldesk.katte_project_client.manager.ApiManagers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuctionService {

    public AuctionDataBean getAuctionData(int auction_id){
        Map<String, String> params = new HashMap<>();
        params.put("auction_id", Integer.toString(auction_id));
        TypeReference<AuctionDataBean> ref = new TypeReference<>() {};

        ResponseEntity<AuctionDataBean> res = ApiManagers.get("auction",params,ref);
        return res.getBody();
    }
}
