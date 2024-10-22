package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterComment;
import com.example.bucketlist.dto.request.comment.PosterCommentWriteRequest;
import com.example.bucketlist.dto.response.comment.PosterCommentResponse;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterCommentRepository;
import com.example.bucketlist.repository.PosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class PosterCommentService {
    
    private final PosterCommentRepository posterCommentRepository;
    private final MemberRepository memberRepository;
    private final PosterRepository posterRepository;

    @Autowired
    public PosterCommentService(PosterCommentRepository posterCommentRepository, MemberRepository memberRepository, PosterRepository posterRepository) {
        this.posterCommentRepository = posterCommentRepository;
        this.memberRepository = memberRepository;
        this.posterRepository = posterRepository;
    }

    public PagedModel<PosterCommentResponse> getComments(Long posterId, int page, int size) {
        Page<PosterCommentResponse> posterComments = posterCommentRepository.findPosterCommentsByPosterId(posterId, page, size);
        return new PagedModel<>(posterComments);
    }

    public Long createComment(Long posterId, Long memberId, PosterCommentWriteRequest commentWriteRequest) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        PosterComment posterComment = new PosterComment();
        posterComment.setPoster(poster);
        posterComment.setMember(member);
        posterComment.setContent(commentWriteRequest.getContent());
        posterCommentRepository.save(posterComment);

        return posterComment.getId();
    }



}
