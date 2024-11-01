package com.example.bucketlist.service;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterComment;
import com.example.bucketlist.dto.request.comment.PosterCommentUpdateRequest;
import com.example.bucketlist.dto.request.comment.PosterCommentWriteRequest;
import com.example.bucketlist.dto.response.comment.PosterCommentResponse;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.exception.UnauthenticationException;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.PosterCommentRepository;
import com.example.bucketlist.repository.PosterRepository;
import com.example.bucketlist.utils.EscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PagedModel<PosterCommentResponse> getComments(CustomUserDetails member, Long posterId, int page, int size) {
        Page<PosterCommentResponse> posterComments = posterCommentRepository.findPosterCommentsByPosterId(member, posterId, page, size);
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

        String escapedContent = EscapeUtils.escapeHtml(commentWriteRequest.getContent());
        posterComment.setContent(escapedContent);

        posterCommentRepository.save(posterComment);

        return posterComment.getId();
    }

    public void deleteComment(Long posterId, Long commentId, Long memberId) {

        PosterComment posterComment = posterCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException());

        if (!posterComment.getPoster().getId().equals(posterId))
            throw new IllegalArgumentException("댓글의 게시글 미일치");

        if (!posterComment.getMember().getId().equals(memberId))
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        posterCommentRepository.delete(posterComment);
    }

    @Transactional
    public void updateComment(Long posterId, Long commentId, Long memberId, PosterCommentUpdateRequest commentUpdateRequest) {

        PosterComment posterComment = posterCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException());

        if (!posterComment.getPoster().getId().equals(posterId))
            throw new IllegalArgumentException("댓글의 게시글 미일치");

        if (!posterComment.getMember().getId().equals(memberId))
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        String content = commentUpdateRequest.getContent();
        posterComment.setContent(content);

    }



}
