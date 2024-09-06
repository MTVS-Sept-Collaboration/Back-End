package com.homefit.backend.login.oauth.entity.user;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class UserPrincipal implements OAuth2User, UserDetails, OidcUser {
    private Long id;
    private String kakaoId;
    private String nickName;
    private String profileImage;
    private RoleType role;
    private Collection<GrantedAuthority> authorities;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    @Setter
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return kakaoId;
    }

    @Override
    public String getUsername() {
        return kakaoId;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.idToken;
    }

    public static UserPrincipal create(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .nickName(user.getNickName())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(RoleType.USER.getCode())))
                .build();
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public static UserPrincipal create(
            User user,
            Map<String, Object> attributes,
            OidcIdToken idToken,
            OidcUserInfo userInfo
    ) {
        UserPrincipal userPrincipal = create(user, attributes);
        userPrincipal.idToken = idToken;
        userPrincipal.userInfo = userInfo;

        return userPrincipal;
    }
}
