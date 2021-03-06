package com.nc.portal.utils;

import com.nc.portal.model.OrdersDTO;
import com.nc.portal.model.Role;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.util.WebUtils;

import javax.crypto.Cipher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.Arrays;

public class CookieUtil {

    private static final String COOKIE_AUTH = "COOKIA";
    private static final String COOKIE_ORDER = "COOKIO";
    //шифрование Cookie
    private static Aes256 aes256 = new Aes256();
    private static int cookSize = 0;

    public static void create(HttpServletResponse response,
                              String record1, //auth / pointFrom
                              String record2, //role / pointTo
                              boolean flag //true - auth, false - order
            /*String domain*/) {
        try {

            //Переводим в байты
            String cooks = record1 + ":" + record2;
            byte[] cookByte = cooks.getBytes();
            //Кодируем
            byte[] encrypt = aes256.makeAes(cookByte, Cipher.ENCRYPT_MODE);
            cookSize = encrypt.length;
            //Переводим декодированное в строку
            String value = new BigInteger(1, encrypt).toString(16);

            String cookieName = flag ? COOKIE_AUTH : COOKIE_ORDER;

            Cookie cookie = new Cookie(cookieName, value);

            cookie.setPath("/");
            cookie.setSecure(false);
            cookie.setHttpOnly(true);

            //if (maxAge > 0) {
            //-1 значит куки действуют до закрытия браузера
            cookie.setMaxAge(-1);
            // }

            response.addCookie(cookie);
        } catch (Exception e) {
        }
    }


    public static void clear(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookie = request.getCookies();
        for (Cookie c : cookie) {
            c.setMaxAge(0);
            response.addCookie(c);
        }
    }

    private static String getValueByName(HttpServletRequest request, int flag) {     //flag==1 auth
        String cookieName = flag == 3 ? COOKIE_ORDER : COOKIE_AUTH;                 //flag==2 role
        Cookie cookie = WebUtils.getCookie(request, cookieName);                   //flag==3 order
        if (cookie == null) {
            return null;
        } else {
            if (cookSize == 0) {
                return null;
            }
            try {
                String cook = cookie.getValue();
                byte[] cookByte = new BigInteger(cook, 16).toByteArray();
                //не спрашивайте(костыль из-за нулевого байта)
                if (!(cookByte.length == cookSize)) {
                    if (cookByte.length == cookSize + 1) {
                        byte[] shifr = new byte[cookByte.length - 1];
                        System.arraycopy(cookByte, 1, shifr, 0, shifr.length);
                        cookByte = shifr;
                    } else {
                        if (cookByte.length == cookSize - 1) {
                            byte[] shifr = new byte[cookByte.length + 1];
                            System.arraycopy(cookByte, 0, shifr, 1, shifr.length);
                            shifr[0] = 0;
                            cookByte = shifr;
                        }
                    }
                }

                byte[] decrypt = aes256.makeAes(cookByte, Cipher.DECRYPT_MODE);
                //String value = new BigInteger(1, decrypt).toString(16);
                String value = new String(decrypt);

                if (flag == 3) {
                    return value;
                }

                int delim = value.indexOf(":");
                if (delim == -1) {
                    //какая-то обработка ошибки
                    return null;
                }
                if (flag == 1) {
                    return value.substring(0, delim);
                }
                if (flag == 2) {
                    return value.substring(delim + 1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

    public static String getAuth(HttpServletRequest request) {
        String auth = getValueByName(request, 1);
        return auth;
    }

    public static OrdersDTO getOrder(HttpServletRequest request) {
        String order = getValueByName(request, 3);
        if (order == null) {
            return null;
        }
        int delim = order.indexOf(":");

        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setPointFrom(order.substring(0, delim));
        ordersDTO.setPointTo(order.substring(delim + 1));

        return ordersDTO;
    }

    public static Role getRole(HttpServletRequest request) {
        Role role = Role.valueOf(getValueByName(request, 2));
        if (role == null) {
            return Role.UNAUTHORIZED;
        }
        return role;
    }
}

