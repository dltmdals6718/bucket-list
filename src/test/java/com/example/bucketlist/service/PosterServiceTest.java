package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterTag;
import com.example.bucketlist.domain.Tag;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterRepository;
import com.example.bucketlist.repository.PosterTagRepository;
import com.example.bucketlist.repository.TagRepository;
import com.example.bucketlist.utils.EscapeUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        member.setEmail("test@test.com");
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
    @DisplayName("게시글 생성시 이스케이프 변환")
    void posterEscapeCreate() {

        // given
        String title = "<h1>제목</h1> <script>alert('hello')</script>";
        String content = "태그 화이트리스트 추가 필요";
        PosterWriteRequest posterWriteRequest = new PosterWriteRequest();
        posterWriteRequest.setTitle(title);
        posterWriteRequest.setContent(content);

        // when
        Long posterId = posterService.createPoster(member.getId(), posterWriteRequest);

        // then
        Poster findPoster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        Assertions
                .assertThat(findPoster.getTitle())
                .isEqualTo(EscapeUtils.escapeHtml(title));

        Assertions
                .assertThat(findPoster.getContent())
                .isEqualTo(content);

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
        String tag = "<script>";
        tags.add(tag);
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
                .isEqualTo(EscapeUtils.escapeHtml(tag));
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

    @Test
    @DisplayName("전체 게시글 조회")
    void findAllPosters() {

        // given
        String jpql = "SELECT COUNT(p) FROM Poster p WHERE isPrivate = FALSE";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        Long totalPosterCount = query.getSingleResult();

        // when
        Page<PosterOverviewResponse> response = posterRepository.findPosterOverview(1, 10, null, null);

        List<PosterOverviewResponse> content = response.getContent();
        PosterOverviewResponse posterOverviewResponse = content.get(0); // 기본값 최신순이기에 get(0)

        //then
        Assertions
                .assertThat(posterOverviewResponse.getPosterId())
                .isEqualTo(poster.getId());

        Assertions
                .assertThat(posterOverviewResponse.getTags())
                .contains(tag1.getName(), tag2.getName());

        Assertions
                .assertThat(response.getTotalElements())
                .isEqualTo(totalPosterCount);

    }

    @Test
    @DisplayName("태그로 게시글 조회")
    void findPosterByTags() {

        // given
        ArrayList<String> searchTags = new ArrayList<>();
        String tag = tag1.getName();
        searchTags.add(tag);

        // when
        Page<PosterOverviewResponse> response = posterRepository.findPosterOverview(1, 10, searchTags, null);
        List<PosterOverviewResponse> content = response.getContent();

        //then
        boolean allHaveTag = content.stream()
                .allMatch(posterOverviewResponse -> posterOverviewResponse.getTags().contains(tag));

        Assertions
                .assertThat(allHaveTag)
                .isTrue();

        boolean containsSpecificPoster = content.stream()
                .anyMatch(posterOverviewResponse -> posterOverviewResponse.getPosterId().equals(poster.getId()));

        Assertions
                .assertThat(containsSpecificPoster)
                .isTrue();

    }

    @Test
    @DisplayName("비공개 글은 페이징에서 제외")
    void notIncludePrivatePoster() {

        // given
        Poster privatePoster = new Poster();
        privatePoster.setIsPrivate(true);
        privatePoster.setMember(member);
        posterRepository.save(privatePoster);

        // when
        String jpql = "SELECT COUNT(*) FROM Poster p";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        Long totalPosterCount = query.getSingleResult();

        Page<PosterOverviewResponse> response = posterRepository.findPosterOverview(1, 10, null, null);

        //then
        long totalElements = response.getTotalElements();
        Assertions
                .assertThat(totalElements)
                .isNotEqualTo(totalPosterCount);

    }

    @Test
    @DisplayName("키워드 검색")
    void keywordSearchPoster() {

        // given
        String keyword = "검색 키워드";

        Poster newPoster = new Poster();
        newPoster.setTitle("제목");
        newPoster.setContent("내용");
        newPoster.setMember(member);
        newPoster.setIsPrivate(false);
        posterRepository.save(newPoster);

        Poster titleInclueKeywordPoster = new Poster();
        titleInclueKeywordPoster.setTitle(keyword);
        titleInclueKeywordPoster.setContent("내용");
        titleInclueKeywordPoster.setMember(member);
        titleInclueKeywordPoster.setIsPrivate(false);
        posterRepository.save(titleInclueKeywordPoster);

        Poster contentIncludeKeywordPoster = new Poster();
        contentIncludeKeywordPoster.setTitle("제목");
        contentIncludeKeywordPoster.setContent(keyword);
        contentIncludeKeywordPoster.setMember(member);
        contentIncludeKeywordPoster.setIsPrivate(false);
        posterRepository.save(contentIncludeKeywordPoster);

        // when
        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(1, 5, null, keyword);

        // then
        List<PosterOverviewResponse> content = posterOverview.getContent();
        Assertions
                .assertThat(content)
                .allMatch(posterOverviewResponse -> {

                    Poster findPoster = posterRepository.findById(posterOverviewResponse.getPosterId())
                            .orElseThrow(() -> new IllegalArgumentException());

                    if (findPoster.getTitle().contains(keyword) || findPoster.getPureContent().contains(keyword))
                        return true;
                    return false;
                });

    }



}