package com.example.bucketlist.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PosterWriteRequest {

    private String title;
    private String content;
    private Boolean isPrivate;

}
