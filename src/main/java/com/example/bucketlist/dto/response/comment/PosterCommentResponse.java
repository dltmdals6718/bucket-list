package com.example.bucketlist.dto.response.comment;

import lombok.Data;


@Data
public class PosterCommentResponse {

    private Long posterId;
    private Long memberId;
    private String nickname;
    private String profileImg;
    private Long commentId;
    private String createdDate;
    private String content;
    private Boolean isOwner;
    private Long likeCnt;

}
