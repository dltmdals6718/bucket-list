package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.PosterComment;
import com.example.bucketlist.domain.PosterCommentLike;
import com.example.bucketlist.exception.DuplicatePosterCommentLikeException;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterCommentLikeRepository;
import com.example.bucketlist.repository.PosterCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PosterCommentLikeService {

    private final PosterCommentLikeRepository posterCommentLikeRepository;
    private final PosterCommentRepository posterCommentRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PosterCommentLikeService(PosterCommentLikeRepository posterCommentLikeRepository, PosterCommentRepository posterCommentRepository, MemberRepository memberRepository) {
        this.posterCommentLikeRepository = posterCommentLikeRepository;
        this.posterCommentRepository = posterCommentRepository;
        this.memberRepository = memberRepository;
    }

    public void addPosterCommentLike(Long commentId, Long memberId) {

        PosterComment posterComment = posterCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        Optional<PosterCommentLike> byPosterCommentAndMember = posterCommentLikeRepository.findByPosterCommentAndMember(posterComment, member);
        if (byPosterCommentAndMember.isPresent()) {
            throw new DuplicatePosterCommentLikeException(ErrorCode.DUPLICATE_POSTER_COMMENT_LIKE);
        }

        PosterCommentLike posterCommentLike = new PosterCommentLike();
        posterCommentLike.setPosterComment(posterComment);
        posterCommentLike.setMember(member);
        posterCommentLikeRepository.save(posterCommentLike);

    }


}
