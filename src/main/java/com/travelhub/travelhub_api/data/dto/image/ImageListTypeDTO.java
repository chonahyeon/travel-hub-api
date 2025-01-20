package com.travelhub.travelhub_api.data.dto.image;

import com.travelhub.travelhub_api.data.enums.ImageType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ImageListTypeDTO {
    /*
     * 매핑 정보
     */
    Long idx;

    /*
     * 이미지 정보
     */
    Long igIdx;
    ImageType igType;
    String igUrl;
    String igPath;

    /*
     * 스토리지 정보
     */
    Long stIdx;
    String stName;
    String stNameSpace;
}
