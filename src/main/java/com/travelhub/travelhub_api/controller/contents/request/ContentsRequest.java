package com.travelhub.travelhub_api.controller.contents.request;

import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.dto.contents.ContentsPlaceWriterDto;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsTagEntity;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.stream.Collectors;

public record ContentsRequest(
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        String title,
        List<Long> tags,
        List<ContentsPlaceWriterDto> places
) {
        public ContentsEntity ofContentsEntity() {
                return ContentsEntity.builder()
                        .ctTitle(this.title)
                        .usId(LoginUserDTO.get())
                        .build();
        }

        public List<ContentsTagEntity> ofContentsTagEntity(Long ctIdx) {
                return this.tags.stream()
                        .map(tag -> ContentsTagEntity.builder()
                                .ctIdx(ctIdx)
                                .tgIdx(tag)
                                .build())
                        .collect(Collectors.toList());
        }

}
