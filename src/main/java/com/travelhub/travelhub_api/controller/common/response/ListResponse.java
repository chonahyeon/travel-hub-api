package com.travelhub.travelhub_api.controller.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> list;
}
