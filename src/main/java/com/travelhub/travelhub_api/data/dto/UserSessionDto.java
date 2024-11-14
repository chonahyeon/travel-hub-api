package com.travelhub.travelhub_api.data.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.travelhub.travelhub_api.data.enums.Role;
import com.travelhub.travelhub_api.data.mysql.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserSessionDto implements OAuth2User {

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

	public User convert() {
		boolean emailVerified = Boolean.parseBoolean(attribute.get("email_verified").toString());
		return User.builder()
				.usId(attribute.get(this.name).toString())
				.usName(attribute.get("name").toString())
				.usEmail(emailVerified ? attribute.get("email").toString() : null)
				.usRole(this.role)
				.usProfile(attribute.get("email").toString())
				.build();
	}
}
