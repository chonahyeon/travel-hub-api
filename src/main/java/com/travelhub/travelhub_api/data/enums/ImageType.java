package com.travelhub.travelhub_api.data.enums;

import lombok.Getter;

@Getter
public enum ImageType {
    CT("content/"),
    RV("review/"),
    MA("main/");

    private final String uploadPath;

    ImageType(String uploadPath) {
        this.uploadPath = uploadPath;
    }
}
