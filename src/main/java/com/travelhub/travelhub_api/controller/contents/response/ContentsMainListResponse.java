package com.travelhub.travelhub_api.controller.contents.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ContentsMainListResponse(
        Long tgIdx,
        String tgName,
        List<ContentsListResponse> contents
) {}
