package com.example.bucketlist.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PosterDetailsResponse {

    private Long posterId;
    private Long memberId;
    private String email;
    private String provider;
    private String providerId;
    private String nickname;
    private String profileImg;
    private String createdDate;
    private String title;
    private String content;
    private List<String> tags;
    private Boolean isAchieve;
    private String posterAchieveContent;
    private Long likeCnt;

    @QueryProjection
    public PosterDetailsResponse(Long posterId, Long memberId, String email, String provider, String providerId, String nickname, String profileImg, String createdDate, String title, String content, List<String> tags, Boolean isAchieve, String posterAchieveContent, Long likeCnt) {
        this.posterId = posterId;
        this.memberId = memberId;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.createdDate = createdDate;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.isAchieve = isAchieve;
        this.posterAchieveContent = posterAchieveContent;
        this.likeCnt = likeCnt;
    }
}
