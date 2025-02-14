package com.travelhub.travelhub_api.data.dto.image;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class BestImageListDTO {
    Long ctIdx;
    Double ctScore;
    String igPath;
}
