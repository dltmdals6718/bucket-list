package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterTag;
import com.example.bucketlist.domain.Tag;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.repository.*;
import com.example.bucketlist.utils.EscapeUtils;
import com.example.bucketlist.utils.S3Uploader;
import com.example.bucketlist.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        poster.setTitle(posterWriteRequest.getTitle());
        poster.setContent(posterWriteRequest.getContent());
        poster.setMember(member);
        poster.setIsPrivate(posterWriteRequest.getIsPrivate());
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

    public Poster getPosterForView(Long posterId) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        return poster;
    }

    public Poster getPosterForUpdate(Long memberId, Long posterId) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        if (memberId != poster.getMember().getId())
            throw new AccessDeniedException("접근 권한 없음.");

        return poster;
    }

    @Transactional
    public Poster updatePoster(Long memberId, Long posterId, PosterWriteRequest posterWriteRequest) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        if (memberId != poster.getMember().getId())
            throw new AccessDeniedException("접근 권한 없음.");

        poster.setTitle(posterWriteRequest.getTitle());
        poster.setContent(posterWriteRequest.getContent());
        poster.setIsPrivate(posterWriteRequest.getIsPrivate());

        List<String> existingUUIDs = posterImageRepository.findAllByPoster(poster).stream()
                .map(p -> p.getStoreFileName())
                .collect(Collectors.toList());
        List<String> receivedUUIDs = UploadFileUtil.imageUUIDExtractor(posterWriteRequest.getContent());

        // 게시글 수정시 삭제된 업로드 이미지 파일 삭제 방법
        // 기존 게시글의 이미지 UUIDs 현재 게시글의 이미지 UUIDs 제거하면 삭제된 UUIDs가 남는다.
        existingUUIDs.removeAll(receivedUUIDs);
        for (String existingUUID : existingUUIDs) {
            posterImageRepository.findByStoreFileName(existingUUID)
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
}
