package com.travelhub.travelhub_api.data.dto.image;

import com.travelhub.travelhub_api.data.enums.ImageType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MainPlaceListDTO {
    /*
     * 매핑 정보
     */
    Long idx;

    /*
     * 이미지 정보
     */
    Long igIdx;
    ImageType igType;
    String igPath;

    /*
     * 스토리지 정보
     */
    Long stIdx;
    String stName;
    String stNameSpace;

    /*
     * 도시 정보
     */
    Long citIdx;
    String citName;
    String citDescription;

    /*
     * 국가 정보
     */
    Long cntIdx;
    String cntName;
    String cntCode;
}
