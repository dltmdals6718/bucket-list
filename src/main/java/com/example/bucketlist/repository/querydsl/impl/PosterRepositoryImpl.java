package com.example.bucketlist.repository.querydsl.impl;

import com.example.bucketlist.domain.*;
import com.example.bucketlist.dto.response.*;
import com.example.bucketlist.repository.querydsl.PosterRepositoryCustom;
import com.example.bucketlist.utils.DefaultProfileImageUtil;
import com.example.bucketlist.utils.S3Uploader;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLTemplates;
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

import java.util.*;
import java.util.stream.Collectors;


@Repository
@Transactional
public class PosterRepositoryImpl implements PosterRepositoryCustom {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;
    private final S3Uploader s3Uploader;

    @Autowired
    public PosterRepositoryImpl(EntityManager entityManager, S3Uploader s3Uploader) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        this.s3Uploader = s3Uploader;
    }

    @Override
    public Optional<PosterDetailsResponse> findPosterDetailsById(Long posterId) {

        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;
        QPosterTag posterTag = QPosterTag.posterTag;
        QTag tag = QTag.tag;

        List<String> tags = jpaQueryFactory
                .select(tag.name)
                .from(tag)
                .leftJoin(posterTag).on(posterTag.tag.id.eq(tag.id))
                .where(posterTag.poster.id.eq(posterId))
                .fetch();

        PosterDetailsResponse posterDetailsResponse = jpaQueryFactory
                .select(new QPosterDetailsResponse(
                        poster.id,
                        member.id,
                        member.email,
                        member.provider,
                        member.providerId,
                        member.nickname,
                        profileImage.storeFileName,
                        poster.createdDate.stringValue(),
                        poster.title,
                        poster.content,
                        Expressions.constant(tags)
                ))
                .from(poster)
                .join(member).on(member.id.eq(poster.member.id))
                .leftJoin(profileImage).on(profileImage.member.id.eq(member.id))
                .where(poster.id.eq(posterId))
                .fetchOne();

        String email = posterDetailsResponse.getEmail();
        String provider = posterDetailsResponse.getProvider();
        String providerId = posterDetailsResponse.getProviderId();
        String profileImg = posterDetailsResponse.getProfileImg();

        if (profileImg == null) {

            if (email == null)
                posterDetailsResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(provider + "_" + providerId));
            else
                posterDetailsResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(email));

        } else {
            posterDetailsResponse.setProfileImg(s3Uploader.getProfileImgPath(profileImg));
        }

        return Optional.ofNullable(posterDetailsResponse);
    }

    @Override
    public Page<PosterOverviewResponse> findPosterOverview(int page, int size, List<String> tags) {

        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;
        QPosterTag posterTag = QPosterTag.posterTag;
        QTag tag = QTag.tag;

        Pageable pageable = PageRequest.of(page - 1, size);

        // 쿼리 빌드
        JPAQuery<Tuple> tupleJPAQuery = jpaQueryFactory
                .select(poster.id, member.id, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.content, poster.createdDate)
                .from(poster)
                .leftJoin(member).on(member.id.eq(poster.member.id))
                .leftJoin(profileImage).on(profileImage.member.id.eq(member.id))
                .leftJoin(posterTag).on(posterTag.poster.id.eq(poster.id))
                .leftJoin(tag).on(tag.id.eq(posterTag.tag.id))
                .where(poster.isPrivate.isFalse())
                .groupBy(poster.id, member.id, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.content, poster.createdDate)
                .offset((page - 1) * size)
                .limit(size)
                .orderBy(poster.id.desc());

        // 카운트 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(poster.id.countDistinct())
                .from(poster)
                .leftJoin(posterTag).on(posterTag.poster.id.eq(poster.id))
                .leftJoin(tag).on(tag.id.eq(posterTag.tag.id))
                .where(poster.isPrivate.isFalse());

        // 태그 이름 필터링
        if (tags != null && tags.size() > 0) {
            tupleJPAQuery.where(tag.name.in(tags));
            countQuery.where(tag.name.in(tags));
        }

        List<PosterOverviewResponse> overviewResponses = tupleJPAQuery.fetch()
                .stream()
                .map(tuple -> {

                    PosterOverviewResponse posterOverviewResponse = new PosterOverviewResponse();
                    posterOverviewResponse.setMemberId(tuple.get(member.id));
                    posterOverviewResponse.setPosterId(tuple.get(poster.id));
                    posterOverviewResponse.setTitle(tuple.get(poster.title));
                    posterOverviewResponse.setCreatedDate(tuple.get(poster.createdDate).toString());

                    String profile = tuple.get(profileImage.storeFileName);
                    String email = tuple.get(member.email);
                    String provider = tuple.get(member.provider);
                    String providerId = tuple.get(member.providerId);

                    if (profile == null) {

                        if (email == null)
                            posterOverviewResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(provider + "_" + providerId));
                        else
                            posterOverviewResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(email));

                    } else {
                        posterOverviewResponse.setProfileImg(s3Uploader.getProfileImgPath(profile));
                    }

                    List<String> tagNamesList = jpaQueryFactory
                            .select(tag.name)
                            .from(posterTag)
                            .join(posterTag.tag, tag)
                            .where(posterTag.poster.id.eq(tuple.get(poster.id)))
                            .fetch();
                    posterOverviewResponse.setTags(tagNamesList);
                    return posterOverviewResponse;
                })
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(overviewResponses, pageable, countQuery::fetchOne);
    }
}
