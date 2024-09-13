package com.example.bucketlist.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class PosterOverviewResponse {

    private Long posterId;
    private Long memberId;
    private String nickname;
    private String profileImg;
    private String createdDate;
    private String title;
    private String content;
    private List<String> tags;
    private Boolean isAchieve;

}
