package com.gouda.edyou.entity;

import com.gouda.edyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Formatter;
import java.util.Map;

public class OAuth2UserImpl implements OAuth2User {
    private OAuth2User oAuth2User;
    private UserService userService;

    @Autowired
    public OAuth2UserImpl(OAuth2User oAuth2User, UserService userService) {
        this.oAuth2User = oAuth2User;
        this.userService = userService;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
//        User user = userService.findByEmail(getEmail());
        return getEmail();
    }

    public String getFullName() {
        return oAuth2User.getAttribute("name");
    }
    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public static String encrypt(String msg)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(msg.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }
    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
