package com.example.bucketlist.service;


import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterAchieve;
import com.example.bucketlist.dto.request.PosterAchieveRequest;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterAchieveRepository;
import com.example.bucketlist.repository.PosterRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PosterAchieveServiceTest {

    private final PosterAchieveService posterAchieveService;
    private final MemberRepository memberRepository;
    private final PosterRepository posterRepository;
    private final PosterAchieveRepository posterAchieveRepository;
    private final EntityManager em;
    private Member member;
    private Poster poster;

    @Autowired
    public PosterAchieveServiceTest(PosterAchieveService posterAchieveService, MemberRepository memberRepository, PosterRepository posterRepository, PosterAchieveRepository posterAchieveRepository, EntityManager em) {
        this.posterAchieveService = posterAchieveService;
        this.memberRepository = memberRepository;
        this.posterRepository = posterRepository;
        this.posterAchieveRepository = posterAchieveRepository;
        this.em = em;
    }

    @BeforeEach
    public void setup() {
        member = new Member();
        member.setNickname("test member");
        member.setEmail("test@test.com");
        memberRepository.save(member);

        poster = new Poster();
        poster.setTitle("title");
        poster.setContent("content");
        poster.setIsPrivate(false);
        poster.setMember(member);
        posterRepository.save(poster);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("버킷리스트 완료")
    public void posterAchieve() {

        // given
        PosterAchieveRequest posterAchieveRequest = new PosterAchieveRequest();
        String posterAchieveContent = "버킷리스트 완료했습니다.";
        posterAchieveRequest.setContent(posterAchieveContent);

        //when
        Long posterAchieveId = posterAchieveService.createPosterAchieve(member.getId(), poster.getId(), posterAchieveRequest);

        //then
        em.flush();
        em.clear();

        Poster findPoster = posterRepository.findById(poster.getId())
                .orElseThrow(() -> new IllegalArgumentException());
        PosterAchieve posterAchieve = posterAchieveRepository.findById(posterAchieveId)
                .orElseThrow(() -> new IllegalArgumentException());

        Assertions
                .assertThat(findPoster.getIsAchieve())
                .isTrue();

        Assertions
                .assertThat(posterAchieve.getContent())
                .isEqualTo(posterAchieveContent);
    }



}