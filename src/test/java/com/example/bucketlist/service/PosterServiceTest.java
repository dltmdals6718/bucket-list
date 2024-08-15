package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private final MemberRepository memberRepository;
    private final EntityManager em;
    private Member member;

    @Autowired
    public PosterServiceTest(PosterService posterService, PosterRepository posterRepository, MemberRepository memberRepository, EntityManager em) {
        this.posterService = posterService;
        this.posterRepository = posterRepository;
        this.memberRepository = memberRepository;
        this.em = em;
    }

    @BeforeEach
    public void setup() {
        member = new Member();
        member.setNickname("test member");
        memberRepository.save(member);
    }

    @Test
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

}