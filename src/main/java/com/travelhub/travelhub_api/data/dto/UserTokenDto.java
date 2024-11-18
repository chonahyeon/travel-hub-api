package com.travelhub.travelhub_api.data.dto;

import com.travelhub.travelhub_api.data.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserTokenDto {
	private String usId;
	private Role role;
}
