package com.example.bucketlist.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class PosterWriteRequest {

    private String title;
    private String content;
    private Boolean isPrivate;
    private Set<String> tags = new HashSet<>();

}
