package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterLike;
import com.example.bucketlist.exception.DuplicatePosterLikeException;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterLikeRepository;
import com.example.bucketlist.repository.PosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PosterLikeService {

    private final PosterLikeRepository posterLikeRepository;
    private final PosterRepository posterRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PosterLikeService(PosterLikeRepository posterLikeRepository, PosterRepository posterRepository, MemberRepository memberRepository) {
        this.posterLikeRepository = posterLikeRepository;
        this.posterRepository = posterRepository;
        this.memberRepository = memberRepository;
    }

    public Long addPosterLike(Long posterId, Long memberId) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        Optional<PosterLike> posterLikeOptional = posterLikeRepository.findByPosterAndMember(poster, member);
        if (posterLikeOptional.isPresent())
            throw new DuplicatePosterLikeException(ErrorCode.DUPLICATE_POSTER_LIKE);

        PosterLike posterLike = new PosterLike();
        posterLike.setPoster(poster);
        posterLike.setMember(member);
        PosterLike save = posterLikeRepository.save(posterLike);
        return save.getId();
    }

}
