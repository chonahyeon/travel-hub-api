package com.travelhub.travelhub_api.data.dto.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class TagListDTO {
    Long tgIdx;
    String tgName;
    Long counts;
}
