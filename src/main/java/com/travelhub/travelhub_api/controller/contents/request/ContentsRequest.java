package com.travelhub.travelhub_api.controller.contents.request;

import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.data.dto.contents.ContentsPlaceWriterDto;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsEntity;
import com.travelhub.travelhub_api.data.mysql.entity.ContentsTagEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record ContentsRequest(
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        String ctTitle,
        @NotNull(message = "여행 시작 날짜는 필수 입력 값입니다.")
        LocalDate ctStartTime,
        @NotNull(message = "여행 종료 날짜는 필수 입력 값입니다.")
        LocalDate ctEndTime,
        List<Long> tags,
        List<ContentsPlaceWriterDto> places
) {
        public ContentsEntity ofContentsEntity() {
                return ContentsEntity.builder()
                        .ctTitle(this.ctTitle)
                        .ctStartTime(this.ctStartTime)
                        .ctEndTime(this.ctEndTime)
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
