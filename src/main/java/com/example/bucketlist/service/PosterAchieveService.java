package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterAchieve;
import com.example.bucketlist.dto.request.PosterAchieveRequest;
import com.example.bucketlist.repository.PosterAchieveImageRepository;
import com.example.bucketlist.repository.PosterAchieveRepository;
import com.example.bucketlist.repository.PosterRepository;
import com.example.bucketlist.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PosterAchieveService {

    private final PosterRepository posterRepository;
    private final PosterAchieveRepository posterAchieveRepository;
    private final PosterAchieveImageRepository posterAchieveImageRepository;

    @Autowired
    public PosterAchieveService(PosterRepository posterRepository, PosterAchieveRepository posterAchieveRepository, PosterAchieveImageRepository posterAchieveImageRepository) {
        this.posterRepository = posterRepository;
        this.posterAchieveRepository = posterAchieveRepository;
        this.posterAchieveImageRepository = posterAchieveImageRepository;
    }

    @Transactional
    public Long createPosterAchieve(Long memberId, Long posterId, PosterAchieveRequest posterAchieveRequest) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        Member member = poster.getMember();
        if (!member.getId().equals(memberId))
            throw new AccessDeniedException("작성자 미일치");

        PosterAchieve posterAchieve = new PosterAchieve();
        posterAchieve.setContent(posterAchieveRequest.getContent());
        posterAchieve.setPoster(poster);
        posterAchieve.setMember(member);
        posterAchieveRepository.save(posterAchieve);

        poster.setIsAchieve(true);

        List<String> imageUUIDs = UploadFileUtil.imageUUIDExtractor(posterAchieveRequest.getContent());
        for (String imageUUID : imageUUIDs) {
            posterAchieveImageRepository.findByStoreFileName(imageUUID)
                    .ifPresent(posterAchieveImage -> {
                        posterAchieveImage.setPosterAchieve(posterAchieve);
                    });
        }

        return posterAchieve.getId();
    }



}
