package com.nc.portal.service;

import com.nc.portal.model.UserDTO;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Base64;

@Service
public class AccountService {

    private final String URL = "http://localhost:8082/auth/role";
    private final String URL_CREATE = "http://localhost:8082/auth";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Создает заголовок для передачи с кодировкой (Base64)
     *
     * @param user
     * @param password
     * @return
     */
    private HttpHeaders createHttpHeaders(String user, String password) {
        String notEncoded = user + ":" + password;
        String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes(Charset.forName("US-ASCII")));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", encodedAuth);
        return headers;
    }

    /**
     * Запрос на получение роли пользователя
     *
     * @param userDTO
     */
    public void getRole(UserDTO userDTO) {
        try {
            HttpHeaders headers = createHttpHeaders(userDTO.getUsername(), userDTO.getPassword());//new HttpHeaders();//createHttpHeaders(userDTO.getUsername(), userDTO.getPassword());
            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, request, String.class);
            System.out.println("Result - status " + response.getBody());
            userDTO.setRole(response.getBody());
        } catch (Exception e) {
            System.out.println("** Exception: " + e.getMessage());
        }
    }

    /**
     * Запрос на регистрацию
     * Здесь надо поймать код от сервера(406 уже есть такой объект, 201 он создан)
     *
     * @param userDTO
     */
    public void createUser(UserDTO userDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // create request body
            JSONObject request = new JSONObject();
            request.put("username", userDTO.getUsername());
            request.put("password", userDTO.getPassword());
            request.put("role", "CUSTOMER");

            HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> loginResponse = restTemplate
                    .exchange(URL_CREATE, HttpMethod.POST, entity, String.class);

        } catch (Exception e) {
            System.out.println("** Exception: " + e.getMessage());
        }
    }
}
