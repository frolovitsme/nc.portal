package com.nc.portal.service;

import com.nc.portal.model.PriceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PriceService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map getPrice(String pointFrom, String pointTo) {
        try {
            PriceDto priceDto = new PriceDto(pointFrom, pointTo);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PriceDto> entity = new HttpEntity<>(priceDto, headers);
            ResponseEntity<Map> response = restTemplate.exchange(GlobalConstants.URL_PRICE, HttpMethod.POST, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.debug("** Exception: " + e.getMessage());
        }
        return new HashMap();
    }
}
