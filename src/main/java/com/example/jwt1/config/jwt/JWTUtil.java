package com.example.jwt1.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt1.auth.PrincipalDetails;

import java.time.Instant;

public class JWTUtil {

//    private static final Alogorithm ALOGORITHM
    private static final long AUTH_TIME = 20*60;
    private static final long REFRESH_TIME = 60*60*24*7;

    public static String makeAuthToken(PrincipalDetails principalDetails) {
        return JWT.create()
                .withSubject(principalDetails.getUsername())
                .withClaim("exp", Instant.now().getEpochSecond() + AUTH_TIME)
                .sign(Algorithm.HMAC512("dsg"));
    }

    public static String makeRefreshToken(PrincipalDetails principalDetails) {
        return JWT.create()
                .withSubject(principalDetails.getUsername())
                .withClaim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
                .sign(Algorithm.HMAC512("dsg"));
    }

    public static VerifyResult verify(String token) {
        try {
            var verify = JWT.require(Algorithm.HMAC512("dsg")).build().verify(token);
            return VerifyResult.builder().success(true).username(verify.getSubject()).build();
        } catch (Exception e) {
            return VerifyResult.builder().success(false)
                    .username("dsg-false").build();
        }
    }
}
