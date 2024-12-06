package com.travelhub.travelhub_api.data.dto.auth;

import com.travelhub.travelhub_api.data.enums.common.Role;
import com.travelhub.travelhub_api.data.mysql.entity.common.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OAuthUserDTO implements OAuth2User {

	private String name;
	private Map<String,Object> attribute;
	private Role role;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attribute;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority(role.name()));
	}

	public String getUserId() {
		return attribute.get(name).toString();
	}

	/*
	 * UseSessionDto -> User Entity
	 * 이메일은 유효할때만 저장.
	 */
	public UserEntity convert() {
		return UserEntity.builder()
				.usId(attribute.get(this.name).toString())
				.usName(attribute.get("name").toString())
				.usRole(this.role)
				.usProfile(attribute.get("picture").toString())
				.build();
	}
}
