//package sws.NoticeBoard.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Slf4j
//public class SecurityUtil {
//    @Value("${jwt.secret}")
//    private static String secretkey;
//    // 1시간 단위
//    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60;
//
//    public static String getCurrentUsername() {
//        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication.getName() == null) {
//            throw new RuntimeException("No authentication information.");
//        }
//        return authentication.getName();
//    }
//
//    private static Claims extractClaims(String token, String key) {
//        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
//    }
//    //토큰 만료확인 메서드
//    public static boolean isExpired(String token) {
//        // expire timestamp를 return함
//        Date expiredDate = extractClaims(token, secretkey).getExpiration();
//        return expiredDate.before(new Date());
//    }
//
//    public String getUsernameFromToken(String token) {
//        return getClaimFromToken(token, Claims::getId);
//    }
//    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = getAllClaimsFromToken(token);
//        return claimsResolver.apply(claims);
//    }
//    // 모든 token에 대한 사용자 속성정보 조회
//    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
//    }
//
//    // 토큰 만료일자 조회
//    public Date getExpirationDateFromToken(String token) {
//        return getClaimFromToken(token, Claims::getExpiration);
//    }
//
//    // id를 입력받아 accessToken 생성
//    public String generateAccessToken(String id) {
//        return generateAccessToken(id, new HashMap<>());
//    }
//
//    // id, 속성정보를 이용해 accessToken 생성
//    public String generateAccessToken(String id, Map<String, Object> claims) {
//        return doGenerateAccessToken(id, claims);
//    }
//
//    // JWT accessToken 생성
//    private String doGenerateAccessToken(String id, Map<String, Object> claims) {
//        String accessToken = Jwts.builder()
//                .setClaims(claims)
//                .setId(id)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1))// 1시간
//                .signWith(SignatureAlgorithm.HS512, secretkey)
//                .compact();
//
//        return accessToken;
//    }
//
//    // id를 입력받아 accessToken 생성
//    public String generateRefreshToken(String id) {
//        return doGenerateRefreshToken(id);
//    }
//
//    // JWT accessToken 생성
//    private String doGenerateRefreshToken(String id) {
//        String refreshToken = Jwts.builder()
//                .setId(id)
//                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5)) // 5시간
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .signWith(SignatureAlgorithm.HS512, secretkey)
//                .compact();
//
//        return refreshToken;
//    }
//
//    // id를 입력받아 accessToken, refreshToken 생성
//    public Map<String, String> generateTokenSet(String id) {
//        return generateTokenSet(id, new HashMap<>());
//    }
//
//    // id, 속성정보를 이용해 accessToken, refreshToken 생성
//    public Map<String, String> generateTokenSet(String id, Map<String, Object> claims) {
//        return doGenerateTokenSet(id, claims);
//    }
//
//    // JWT accessToken, refreshToken 생성
//    private Map<String, String> doGenerateTokenSet(String id, Map<String, Object> claims) {
//        Map<String, String> tokens = new HashMap<String, String>();
//
//        String accessToken = Jwts.builder()
//                .setClaims(claims)
//                .setId(id)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1))// 1시간
//                .signWith(SignatureAlgorithm.HS512, secretkey)
//                .compact();
//
//        String refreshToken = Jwts.builder()
//                .setId(id)
//                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5)) // 5시간
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .signWith(SignatureAlgorithm.HS512, secretkey)
//                .compact();
//
//        tokens.put("accessToken", accessToken);
//        tokens.put("refreshToken", refreshToken);
//        return tokens;
//    }
//
//    // JWT refreshToken 만료체크 후 재발급
//    public Boolean reGenerateRefreshToken(String id) throws Exception {
//        log.info("[reGenerateRefreshToken] refreshToken 재발급 요청");
//        // 관리자 정보 조회
//        AdminDTO aDTO = new AdminDTO();
//        // DB에서 정보 조회
//        // ...
//
//        // DB에서 refreshToken 정보 조회
//        RefreshTokenDTO rDTO = new RefreshTokenDTO();
//        // ... DB 조회 부분
//
//        // refreshToken 정보가 존재하지 않는 경우
//        if(rDTO == null) {
//            log.info("[reGenerateRefreshToken] refreshToken 정보가 존재하지 않습니다.");
//            return false;
//        }
//
//        // refreshToken 만료 여부 체크
//        try {
//            String refreshToken = rDTO.getRefreshToken().substring(7);
//            Jwts.parser().setSigningKey(secret).parseClaimsJws(refreshToken);
//            log.info("[reGenerateRefreshToken] refreshToken이 만료되지 않았습니다.");
//            return true;
//        }
//        // refreshToken이 만료된 경우 재발급
//        catch(ExpiredJwtException e) {
//            rDTO.setRefreshToken("Bearer " + generateRefreshToken(id));
//            // ... DB에서 refreshToken 정보 수정
//            log.info("[reGenerateRefreshToken] refreshToken 재발급 완료 : {}", "Bearer " + generateRefreshToken(id));
//            return true;
//        }
//        // 그 외 예외처리
//        catch(Exception e) {
//            log.error("[reGenerateRefreshToken] refreshToken 재발급 중 문제 발생 : {}", e.getMessage());
//            return false;
//        }
//    }
//
//    // 토근 검증
//
//}