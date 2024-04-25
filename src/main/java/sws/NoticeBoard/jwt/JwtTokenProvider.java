package sws.NoticeBoard.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sws.NoticeBoard.controller.form.AdminDTO;
import sws.NoticeBoard.controller.form.RefreshTokenDTO;
import sws.NoticeBoard.domain.Member;
import sws.NoticeBoard.domain.RefreshToken;
import sws.NoticeBoard.repository.MemberRepository;
import sws.NoticeBoard.service.MemberService;
import sws.NoticeBoard.service.RefreshTokenService;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@PropertySources({
        @PropertySource("classpath:/application.properties")
})
@Transactional(readOnly = true)
public class JwtTokenProvider {
    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60;
    @Value("${jwt.secret}")
    String secretkey;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    // 모든 token에 대한 사용자 속성정보 조회
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
    }

    // 토큰 만료일자 조회
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // id를 입력받아 accessToken 생성
    public String generateAccessToken(String id) {
        return generateAccessToken(id, new HashMap<>());
    }

    // id, 속성정보를 이용해 accessToken 생성
    public String generateAccessToken(String id, Map<String, Object> claims) {
        return doGenerateAccessToken(id, claims);
    }

    // JWT accessToken 생성
    private String doGenerateAccessToken(String id, Map<String, Object> claims) {
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1))// 1시간
                .signWith(SignatureAlgorithm.HS256, secretkey)
                .compact();

        return accessToken;
    }

    // id를 입력받아 accessToken 생성
    public String generateRefreshToken(String id) {
        return doGenerateRefreshToken(id);
    }

    // JWT accessToken 생성
    private String doGenerateRefreshToken(String id) {
        String refreshToken = Jwts.builder()
                .setId(id)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5)) // 5시간
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, secretkey)
                .compact();

        return refreshToken;
    }

    // id를 입력받아 accessToken, refreshToken 생성
    public Map<String, String> generateTokenSet(String id) {
        return generateTokenSet(id, new HashMap<>());
    }

    // id, 속성정보를 이용해 accessToken, refreshToken 생성
    public Map<String, String> generateTokenSet(String id, Map<String, Object> claims) {
        return doGenerateTokenSet(id, claims);
    }

    // JWT accessToken, refreshToken 생성
    private Map<String, String> doGenerateTokenSet(String id, Map<String, Object> claims) {
        Map<String, String> tokens = new HashMap<String, String>();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1))// 1시간
                .signWith(SignatureAlgorithm.HS256, secretkey)
                .compact();

        String refreshToken = Jwts.builder()
                .setId(id)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 5)) // 5시간
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, secretkey)
                .compact();

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // JWT refreshToken 만료체크 후 재발급
    public Boolean reGenerateRefreshToken(String memberId) throws Exception {
        log.info("[reGenerateRefreshToken] refreshToken 재발급 요청");
        // 관리자 정보 조회
        Member member = memberRepository.findByLoginId(memberId);

        // refreshToken 체크
        RefreshToken rf = refreshTokenService.refreshTokenLoad(member.getId()).orElse(null);


        // refreshToken 정보가 존재하지 않는 경우
        if(rf == null) {
            log.info("[reGenerateRefreshToken] refreshToken 정보가 존재하지 않습니다.");
            return false;
        }

        // refreshToken 만료 여부 체크
        try {
            String refreshToken = rf.getToken().substring(7);
            Jwts.parser().setSigningKey(secretkey).parseClaimsJws(refreshToken);
            log.info("[reGenerateRefreshToken] refreshToken이 만료되지 않았습니다.");
            return true;
        }
        // refreshToken이 만료된 경우 재발급
        catch(ExpiredJwtException e) {
            rf.setToken("Bearer " + generateRefreshToken(member.getLoginId()));
            Date expirationDateFromToken = getExpirationDateFromToken(rf.getToken());
            rf.setExpiredAt(expirationDateFromToken);
            refreshTokenService.save(rf);
            log.info("[reGenerateRefreshToken] refreshToken 재발급 완료 : {}", "Bearer " + generateRefreshToken(member.getLoginId()));
            return true;
        }
        // 그 외 예외처리
        catch(Exception e) {
            log.error("[reGenerateRefreshToken] refreshToken 재발급 중 문제 발생 : {}", e.getMessage());
            return false;
        }
    }

    // 토근 검증
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
