package com.example.bucketlist.service;

import com.example.bucketlist.domain.*;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.exception.authentication.ForbiddenException;
import com.example.bucketlist.repository.*;
import com.example.bucketlist.utils.EscapeUtils;
import com.example.bucketlist.utils.S3Uploader;
import com.example.bucketlist.utils.UploadFileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PosterService {

    private S3Uploader s3Uploader;
    private PosterRepository posterRepository;
    private PosterImageRepository posterImageRepository;
    private MemberRepository memberRepository;
    private TagRepository tagRepository;
    private PosterTagRepository posterTagRepository;

    @Autowired
    public PosterService(S3Uploader s3Uploader, PosterRepository posterRepository, PosterImageRepository posterImageRepository, MemberRepository memberRepository, TagRepository tagRepository, PosterTagRepository posterTagRepository) {
        this.s3Uploader = s3Uploader;
        this.posterRepository = posterRepository;
        this.posterImageRepository = posterImageRepository;
        this.memberRepository = memberRepository;
        this.tagRepository = tagRepository;
        this.posterTagRepository = posterTagRepository;
    }

    @Transactional
    public Long createPoster(Long memberId, PosterWriteRequest posterWriteRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        Poster poster = new Poster();
        poster.setTitle(EscapeUtils.escapeHtml(posterWriteRequest.getTitle()));
        String content = posterWriteRequest.getContent();
        poster.setContent(content);

        Document document = Jsoup.parse(content);
        String pureContent = document.text();
        poster.setPureContent(EscapeUtils.escapeHtml(pureContent));

        poster.setMember(member);
        poster.setIsPrivate(posterWriteRequest.getIsPrivate());
        poster.setIsAchieve(false);
        posterRepository.save(poster);

        for (String name : posterWriteRequest.getTags()) {

            String escapedName = EscapeUtils.escapeHtml(name);
            if (escapedName.isBlank())
                continue;

            Tag tag = tagRepository.findByName(escapedName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(escapedName);
                        return tagRepository.save(newTag);
                    });

            PosterTag posterTag = new PosterTag();
            posterTag.setPoster(poster);
            posterTag.setTag(tag);
            posterTagRepository.save(posterTag);
        }

        List<String> imageUUIDs = UploadFileUtil.imageUUIDExtractor(posterWriteRequest.getContent());
        for (String imageUUID : imageUUIDs) {
            posterImageRepository.findByStoreFileName(imageUUID)
                    .ifPresent(posterImage -> {
                        posterImage.setPoster(poster);
                    });
        }

        return poster.getId();
    }

    public PosterDetailsResponse getPosterForView(Long posterId) {

        PosterDetailsResponse poster = posterRepository.findPosterDetailsById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        return poster;
    }

    public Poster getPosterForUpdate(Long memberId, Long posterId) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());
        poster.setTitle(EscapeUtils.unescapeHtml(poster.getTitle()));

        if (!memberId.equals(poster.getMember().getId()))
            throw new ForbiddenException(ErrorCode.FORBIDDEN);

        return poster;
    }

    @Transactional
    public Poster updatePoster(Long memberId, Long posterId, PosterWriteRequest posterWriteRequest) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());
        if (!memberId.equals(poster.getMember().getId()))
            throw new ForbiddenException(ErrorCode.FORBIDDEN);

        poster.setTitle(EscapeUtils.escapeHtml(posterWriteRequest.getTitle()));

        String content = posterWriteRequest.getContent();
        poster.setContent(content);
        poster.setIsPrivate(posterWriteRequest.getIsPrivate());

        Document document = Jsoup.parse(content);
        String pureContent = document.text();
        poster.setPureContent(EscapeUtils.escapeHtml(pureContent));


        // 삭제된 태그 처리
        // 게시글의 기존 태그 목록에서, 입력으로 들어온 태그 목록을 지우면 삭제된 태그만 남는다
        List<String> existingTagNames = posterTagRepository.findByPoster(poster).stream()
                .map(posterTag -> posterTag.getTag().getName())
                .collect(Collectors.toList());
        List<String> receivedTagNames = posterWriteRequest.getTags().stream().toList().stream()
                .map(name -> EscapeUtils.escapeHtml(name))
                .collect(Collectors.toList());

        existingTagNames.removeAll(receivedTagNames);
        for (String deletedTagName : existingTagNames) {
            Tag tag = tagRepository.findByName(deletedTagName)
                    .orElseThrow(() -> new IllegalArgumentException());
            posterTagRepository.deleteByPosterAndTag(poster, tag);
        }

        // 추가된 태그 처리
        for (String name : posterWriteRequest.getTags()) {

            String escapedName = EscapeUtils.escapeHtml(name);
            if (escapedName.isBlank())
                continue;

            Tag tag = tagRepository.findByName(escapedName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(escapedName);
                        return tagRepository.save(newTag);
                    });

            Optional<PosterTag> posterTag = posterTagRepository.findByPosterAndTag(poster, tag);
            if (!posterTag.isPresent()) {
                PosterTag newPosterTag = new PosterTag();
                newPosterTag.setPoster(poster);
                newPosterTag.setTag(tag);
                posterTagRepository.save(newPosterTag);
            }
        }

        // 게시글 수정시 삭제된 업로드 이미지 파일 삭제 방법
        // 기존 게시글의 이미지 UUIDs 현재 게시글의 이미지 UUIDs 제거하면 삭제된 UUIDs가 남는다.
        List<String> existingUUIDs = posterImageRepository.findAllByPoster(poster).stream()
                .map(p -> p.getStoreFileName())
                .collect(Collectors.toList());
        List<String> receivedUUIDs = UploadFileUtil.imageUUIDExtractor(posterWriteRequest.getContent());

        existingUUIDs.removeAll(receivedUUIDs);
        for (String deletedUUID : existingUUIDs) {
            posterImageRepository.findByStoreFileName(deletedUUID)
                    .ifPresent(posterImage -> {
                        posterImageRepository.delete(posterImage);
                        s3Uploader.deletePosterImg(posterImage);
                    });
        }

        // 게시글 수정시 추가된 업로드 이미지 파일 등록
        for (String receivedUUID : receivedUUIDs) {
            posterImageRepository.findByStoreFileName(receivedUUID)
                    .filter(posterImage -> posterImage.getPoster() == null)
                    .ifPresent(posterImage -> posterImage.setPoster(poster));
        }

        return poster;
    }

    public PagedModel<PosterOverviewResponse> getPosterOverview(int page, int size, List<String> searchTag, String keyword, String sort, String status) {

        if (size >= 50)
            size = 10;

        Page<PosterOverviewResponse> posterOverview = posterRepository.findPosterOverview(page, size, searchTag, keyword, sort, status);
        return new PagedModel<>(posterOverview);
    }

    public PagedModel<PosterOverviewResponse> findPosterOverviewByMemberId(Long memberId, int page, int size, boolean includePrivate) {

        if (size >= 50)
            size = 10;

        Page<PosterOverviewResponse> posterOverview = posterRepository.findPosterOverviewByMemberId(memberId, page, size, includePrivate);
        return new PagedModel<>(posterOverview);
    }


}
