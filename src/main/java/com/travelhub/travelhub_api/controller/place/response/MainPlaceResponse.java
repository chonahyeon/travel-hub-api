package com.travelhub.travelhub_api.controller.place.response;

import com.travelhub.travelhub_api.data.dto.image.MainPlaceListDTO;
import com.travelhub.travelhub_api.data.enums.ImageType;
import com.travelhub.travelhub_api.data.mysql.entity.ImageEntity;
import lombok.Builder;

import java.util.List;

@Builder
public record MainPlaceResponse(
        Long igIdx,
        Long idx,
        ImageType igType,
        String igUrl,
        String igPath,
        Long stIdx,
        Long citIdx,
        String citName,
        String citDescription,
        Long cntIdx,
        String cntName,
        String cntCode,
        String name
) {

    public static List<MainPlaceResponse> ofListDTO(List<MainPlaceListDTO> mainPlaceListDTOS, String domain){
        return mainPlaceListDTOS.stream()
                .map(imageListTypeDTO -> of(imageListTypeDTO, domain))
                .toList();
    }

    /**
     * Dto to Dto
     * @param imageTypeDTO 조회 결과
     * @param domain 스토리지 도메인 (사전인증 도메인)
     * @return dto
     */
    public static MainPlaceResponse of(MainPlaceListDTO imageTypeDTO, String domain) {
        String imageUrl = domain + imageTypeDTO.getIgPath();

        return MainPlaceResponse.builder()
                .igIdx(imageTypeDTO.getIgIdx())
                .idx(imageTypeDTO.getIdx())
                .igType(imageTypeDTO.getIgType())
                .igUrl(imageUrl)
                .igPath(imageTypeDTO.getIgPath())
                .stIdx(imageTypeDTO.getStIdx())
                .citIdx(imageTypeDTO.getCitIdx())
                .citName(imageTypeDTO.getCitName())
                .citDescription(imageTypeDTO.getCitDescription())
                .cntIdx(imageTypeDTO.getCntIdx())
                .cntName(imageTypeDTO.getCntName())
                .cntCode(imageTypeDTO.getCntCode())
                .name(String.format("%s %s", imageTypeDTO.getCntName(), imageTypeDTO.getCitName()))  // country + city
                .build();
    }

    /**
     * image entity to dto
     * @param imageEntity 이미지 엔티티
     * @param domain 스토리지 도메인 (사전인증 도메인)
     * @return dto
     */
    public static MainPlaceResponse of(ImageEntity imageEntity, String domain) {
        String imageUrl = domain + imageEntity.getIgPath();

        return MainPlaceResponse.builder()
                .igIdx(imageEntity.getIgIdx())
                .idx(imageEntity.getIdx())
                .igType(imageEntity.getIgType())
                .igUrl(imageUrl)
                .igPath(imageEntity.getIgPath())
                .stIdx(imageEntity.getStIdx())
                .build();
    }
}
