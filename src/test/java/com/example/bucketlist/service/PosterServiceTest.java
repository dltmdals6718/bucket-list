package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterTag;
import com.example.bucketlist.domain.Tag;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterRepository;
import com.example.bucketlist.repository.PosterTagRepository;
import com.example.bucketlist.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@Transactional
class PosterServiceTest {

    private final PosterService posterService;
    private final PosterRepository posterRepository;
    private final TagRepository tagRepository;
    private final PosterTagRepository posterTagRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;
    private Member member;
    private Poster poster;
    private Tag tag1;
    private Tag tag2;

    @Autowired
    public PosterServiceTest(PosterService posterService, PosterRepository posterRepository, TagRepository tagRepository, PosterTagRepository posterTagRepository, MemberRepository memberRepository, EntityManager em) {
        this.posterService = posterService;
        this.posterRepository = posterRepository;
        this.tagRepository = tagRepository;
        this.posterTagRepository = posterTagRepository;
        this.memberRepository = memberRepository;
        this.em = em;
    }

    @BeforeEach
    public void setup() {
        member = new Member();
        member.setNickname("test member");
        memberRepository.save(member);

        poster = new Poster();
        poster.setTitle("title");
        poster.setContent("content");
        poster.setIsPrivate(false);
        poster.setMember(member);
        posterRepository.save(poster);

        tag1 = new Tag();
        tag1.setName("여행");
        tagRepository.save(tag1);

        tag2 = new Tag();
        tag2.setName("음식");
        tagRepository.save(tag2);

        PosterTag posterTag1 = new PosterTag();
        posterTag1.setPoster(poster);
        posterTag1.setTag(tag1);
        posterTagRepository.save(posterTag1);

        PosterTag posterTag2 = new PosterTag();
        posterTag2.setPoster(poster);
        posterTag2.setTag(tag2);
        posterTagRepository.save(posterTag2);

        em.flush();
        em.clear();

    }

    @Test
    @DisplayName("게시글 태그 생성")
    void posterTagCreate() {

        // given
        PosterWriteRequest posterWriteRequest = new PosterWriteRequest();
        posterWriteRequest.setTitle("title");
        posterWriteRequest.setContent("content");
        posterWriteRequest.setIsPrivate(false);

        Set<String> tags = new HashSet<>();
        tags.add("여행");
        tags.add("음식");
        posterWriteRequest.setTags(tags);

        // when
        Long posterId = posterService.createPoster(member.getId(), posterWriteRequest);

        // then
        em.flush();
        em.clear();

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        Assertions
                .assertThat(poster.getPosterTags().size())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("게시글 태그 이스케이프 확인")
    void posterTagEscape() {

        // given
        PosterWriteRequest posterWriteRequest = new PosterWriteRequest();
        posterWriteRequest.setTitle("title");
        posterWriteRequest.setContent("content");
        posterWriteRequest.setIsPrivate(false);

        Set<String> tags = new HashSet<>();
        tags.add("<script>");
        posterWriteRequest.setTags(tags);

        // when
        Long posterId = posterService.createPoster(member.getId(), posterWriteRequest);

        // then
        em.flush();
        em.clear();

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        String escapedTag = poster.getPosterTags().get(0).getTag().getName();
        Assertions
                .assertThat(escapedTag)
                .isEqualTo("&lt;script&gt;");
    }

    @Test
    @DisplayName("게시글 기존 태그 삭제")
    void existPosterTagDelete() {

        // given
        PosterWriteRequest posterWriteRequest = new PosterWriteRequest();
        posterWriteRequest.setTitle("title");
        posterWriteRequest.setContent("content");
        posterWriteRequest.setIsPrivate(false);

        Set<String> tags = new HashSet<>();
        tags.add(tag1.getName());
        posterWriteRequest.setTags(tags);

        // when
        posterService.updatePoster(member.getId(), poster.getId(), posterWriteRequest);

        // then
        em.flush();
        em.clear();

        Poster findPoster = posterRepository.findById(poster.getId())
                .orElseThrow(() -> new IllegalArgumentException());

        Assertions
                .assertThat(findPoster.getPosterTags().size())
                .isEqualTo(1);

        Assertions
                .assertThat(findPoster.getPosterTags().get(0).getTag().getId())
                .isEqualTo(tag1.getId());


    }

    @Test
    @DisplayName("게시글에 새로운 태그 추가")
    void newPosterTagAdd() {

        // given
        PosterWriteRequest posterWriteRequest = new PosterWriteRequest();
        posterWriteRequest.setTitle("title");
        posterWriteRequest.setContent("content");
        posterWriteRequest.setIsPrivate(false);

        String newTagName = "새로운 태그명";
        Set<String> tags = new HashSet<>();
        tags.add(tag1.getName());
        tags.add(tag2.getName());
        tags.add(newTagName);
        posterWriteRequest.setTags(tags);

        // when
        posterService.updatePoster(member.getId(), poster.getId(), posterWriteRequest);

        // then
        em.flush();
        em.clear();

        Poster findPoster = posterRepository.findById(poster.getId())
                .orElseThrow(() -> new IllegalArgumentException());

        Assertions
                .assertThat(findPoster.getPosterTags().size())
                .isEqualTo(3);

        Assertions
                .assertThat(
                        findPoster.getPosterTags().stream()
                                .anyMatch(posterTag -> posterTag.getTag().getName().equals(newTagName))
                )
                .isTrue();

    }

    @Test
    @DisplayName("게시글 정보 조회")
    void getPosterDetails() {

        // given
        Long posterId = poster.getId();

        // when
        PosterDetailsResponse posterDetailsResponse = posterRepository.findPosterDetailsById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        // then
        Assertions
                .assertThat(posterDetailsResponse.getPosterId())
                .isEqualTo(posterId);

        Assertions
                .assertThat(posterDetailsResponse.getContent())
                .isEqualTo(poster.getContent());

        Assertions
                .assertThat(posterDetailsResponse.getTitle())
                .isEqualTo(poster.getTitle());

        Assertions
                .assertThat(posterDetailsResponse.getMemberId())
                .isEqualTo(member.getId());

        Assertions
                .assertThat(posterDetailsResponse.getTags())
                .contains(tag1.getName(), tag2.getName());

    }

}