package com.example.bucketlist.repository.querydsl.impl;

import com.example.bucketlist.domain.QMember;
import com.example.bucketlist.domain.QPoster;
import com.example.bucketlist.domain.QPosterComment;
import com.example.bucketlist.domain.QProfileImage;
import com.example.bucketlist.dto.response.comment.PosterCommentResponse;
import com.example.bucketlist.repository.querydsl.PosterCommentRepositoryCustom;
import com.example.bucketlist.utils.DefaultProfileImageUtil;
import com.example.bucketlist.utils.S3Uploader;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Transactional
public class PosterCommentRepositoryImpl implements PosterCommentRepositoryCustom {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;
    private final S3Uploader s3Uploader;

    @Autowired
    public PosterCommentRepositoryImpl(EntityManager entityManager, S3Uploader s3Uploader) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
        this.s3Uploader = s3Uploader;
    }

    @Override
    public Page<PosterCommentResponse> findPosterCommentsByPosterId(Long posterId, int page, int size) {

        QPosterComment posterComment = QPosterComment.posterComment;
        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;

        Pageable pageable = PageRequest.of(page - 1, size);


        // 쿼리 빌드
        JPAQuery<Tuple> tupleJPAQuery = jpaQueryFactory
                .select(poster.id, member.id, member.nickname, member.email, profileImage.storeFileName, posterComment.id, posterComment.createdDate, posterComment.content)
                .from(poster)
                .innerJoin(posterComment).on(posterComment.poster.id.eq(poster.id))
                .leftJoin(member).on(member.id.eq(posterComment.member.id))
                .leftJoin(profileImage).on(profileImage.member.id.eq(member.id))
                .where(poster.id.eq(posterId))
                .offset((page - 1) * size)
                .limit(size)
                .orderBy(posterComment.id.desc());

        // 카운트 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(posterComment.count())
                .from(posterComment)
                .where(posterComment.poster.id.eq(posterId));

        List<PosterCommentResponse> posterCommentResponses = tupleJPAQuery
                .fetch()
                .stream()
                .map(tuple -> {

                    PosterCommentResponse posterCommentResponse = new PosterCommentResponse();
                    posterCommentResponse.setCommentId(tuple.get(posterComment.id));
                    posterCommentResponse.setPosterId(tuple.get(poster.id));
                    posterCommentResponse.setMemberId(tuple.get(member.id));
                    posterCommentResponse.setNickname(tuple.get(member.nickname));
                    posterCommentResponse.setContent(tuple.get(posterComment.content));

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    posterCommentResponse.setCreatedDate(tuple.get(posterComment.createdDate).format(dateTimeFormatter));

                    String profile = tuple.get(profileImage.storeFileName);
                    String email = tuple.get(member.email);
                    String provider = tuple.get(member.provider);
                    String providerId = tuple.get(member.providerId);

                    if (profile == null) {

                        if (email == null)
                            posterCommentResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(provider + "_" + providerId));
                        else
                            posterCommentResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(email));

                    } else {
                        posterCommentResponse.setProfileImg(s3Uploader.getProfileImgPath(profile));
                    }

                    return posterCommentResponse;
                })
                .collect(Collectors.toList());


        return PageableExecutionUtils.getPage(posterCommentResponses, pageable, countQuery::fetchOne);
    }
}
