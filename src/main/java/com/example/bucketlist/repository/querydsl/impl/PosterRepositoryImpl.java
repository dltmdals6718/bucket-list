package com.example.bucketlist.repository.querydsl.impl;

import com.example.bucketlist.domain.*;
import com.example.bucketlist.dto.response.*;
import com.example.bucketlist.repository.querydsl.PosterRepositoryCustom;
import com.example.bucketlist.utils.DefaultProfileImageUtil;
import com.example.bucketlist.utils.S3Uploader;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
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
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
        this.s3Uploader = s3Uploader;
    }

    @Override
    public Optional<PosterDetailsResponse> findPosterDetailsById(Long posterId) {

        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;
        QPosterTag posterTag = QPosterTag.posterTag;
        QTag tag = QTag.tag;
        QPosterAchieve posterAchieve = QPosterAchieve.posterAchieve;
        QPosterLike posterLike = QPosterLike.posterLike;

        List<String> tags = jpaQueryFactory
                .select(tag.name)
                .from(tag)
                .leftJoin(posterTag).on(posterTag.tag.id.eq(tag.id))
                .where(posterTag.poster.id.eq(posterId))
                .fetch();

        Long likeCnt = jpaQueryFactory
                .select(posterLike.count())
                .from(posterLike)
                .where(posterLike.poster.id.eq(posterId))
                .fetchOne();

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
                        Expressions.constant(tags),
                        poster.isAchieve,
                        posterAchieve.content,
                        Expressions.constant(likeCnt)
                ))
                .from(poster)
                .join(member).on(member.id.eq(poster.member.id))
                .leftJoin(posterAchieve).on(posterAchieve.id.eq(poster.id))
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
    public Page<PosterOverviewResponse> findPosterOverview(int page, int size, List<String> tags, String keyword, String sort, String status) {

        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;
        QPosterTag posterTag = QPosterTag.posterTag;
        QTag tag = QTag.tag;
        QPosterLike posterLike = QPosterLike.posterLike;

        Pageable pageable = PageRequest.of(page - 1, size);

        StringPath likeCount = Expressions.stringPath("like_count");
        JPAQuery<Long> likeCountQuery = jpaQueryFactory
                .select(posterLike.count())
                .from(posterLike)
                .where(posterLike.poster.id.eq(poster.id));


        // 쿼리 빌드
        JPAQuery<Tuple> tupleJPAQuery = jpaQueryFactory
                .select(poster.id, member.id, member.nickname, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.pureContent, poster.createdDate,
                        poster.isAchieve,
                        ExpressionUtils.as(likeCountQuery, "like_count"))
                .from(poster)
                .leftJoin(member).on(member.id.eq(poster.member.id))
                .leftJoin(profileImage).on(profileImage.member.id.eq(member.id))
                .leftJoin(posterTag).on(posterTag.poster.id.eq(poster.id))
                .leftJoin(tag).on(tag.id.eq(posterTag.tag.id))
                .where(poster.isPrivate.isFalse())
                .groupBy(poster.id, member.id, member.nickname, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.pureContent, poster.createdDate, poster.isAchieve)
                .offset((page - 1) * size)
                .limit(size);

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

        // 키워드 필터링
        if (keyword != null && !keyword.isBlank()) {
            tupleJPAQuery
                    .where(poster.title.contains(keyword)
                            .or(poster.pureContent.contains(keyword)));
            countQuery
                    .where(poster.title.contains(keyword)
                            .or(poster.pureContent.contains(keyword)));
        }


        // 상태 필터링
        if (status != null) {
            if (status.equals("achieved")) {
                tupleJPAQuery
                        .where(poster.isAchieve.isTrue());
                countQuery
                        .where(poster.isAchieve.isTrue());
            } else if (status.equals("unachieved")) {
                tupleJPAQuery
                        .where(poster.isAchieve.isFalse());
                countQuery
                        .where(poster.isAchieve.isFalse());
            }
        }

        // 정렬
        if (sort != null) {
            if (sort.equals("id")) {
                tupleJPAQuery.orderBy(poster.id.desc());
            } else if (sort.equals("like")) {
                tupleJPAQuery.orderBy(likeCount.desc());
            }
        } else {
            tupleJPAQuery.orderBy(poster.id.desc());
        }

        List<PosterOverviewResponse> overviewResponses = tupleJPAQuery.fetch()
                .stream()
                .map(tuple -> {

                    PosterOverviewResponse posterOverviewResponse = new PosterOverviewResponse();
                    posterOverviewResponse.setMemberId(tuple.get(member.id));
                    posterOverviewResponse.setNickname(tuple.get(member.nickname));
                    posterOverviewResponse.setPosterId(tuple.get(poster.id));
                    posterOverviewResponse.setTitle(tuple.get(poster.title));
                    posterOverviewResponse.setContent(tuple.get(poster.pureContent));
                    posterOverviewResponse.setIsAchieve(tuple.get(poster.isAchieve));
                    posterOverviewResponse.setLikeCnt(tuple.get(11, Long.class));

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    posterOverviewResponse.setCreatedDate(tuple.get(poster.createdDate).format(dateTimeFormatter));

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

                    // 태그 목록 조회
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

    @Override
    public Page<PosterOverviewResponse> findPosterOverviewByMemberId(Long memberId, int page, int size, boolean includePrivate) {

        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;
        QPosterTag posterTag = QPosterTag.posterTag;
        QTag tag = QTag.tag;
        QPosterLike posterLike = QPosterLike.posterLike;

        Pageable pageable = PageRequest.of(page - 1, size);

        // 좋아요 개수 조회 서브쿼리
        JPAQuery<Long> likeCountQuery = jpaQueryFactory
                .select(posterLike.count())
                .from(posterLike)
                .where(posterLike.poster.id.eq(poster.id));

        // 쿼리 빌드
        JPAQuery<Tuple> tupleJPAQuery = jpaQueryFactory
                .select(poster.id, member.id, member.nickname, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.pureContent, poster.createdDate, poster.isAchieve, likeCountQuery)
                .from(poster)
                .leftJoin(member).on(member.id.eq(poster.member.id))
                .leftJoin(profileImage).on(profileImage.member.id.eq(member.id))
                .leftJoin(posterTag).on(posterTag.poster.id.eq(poster.id))
                .leftJoin(tag).on(tag.id.eq(posterTag.tag.id))
                .where(member.id.eq(memberId))
                .groupBy(poster.id, member.id, member.nickname, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.pureContent, poster.createdDate, poster.isAchieve)
                .orderBy(poster.id.desc())
                .offset((page - 1) * size)
                .limit(size);

        // 카운트 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(poster.id.countDistinct())
                .from(poster)
                .leftJoin(member).on(member.id.eq(poster.member.id))
                .where(member.id.eq(memberId));

        // 비공개 게시글 포함 여부
        if (!includePrivate) {
            tupleJPAQuery.where(poster.isPrivate.isFalse());
            countQuery.where(poster.isPrivate.isFalse());
        }

        List<PosterOverviewResponse> overviewResponses = tupleJPAQuery.fetch()
                .stream()
                .map(tuple -> {

                    PosterOverviewResponse posterOverviewResponse = new PosterOverviewResponse();
                    posterOverviewResponse.setMemberId(tuple.get(member.id));
                    posterOverviewResponse.setNickname(tuple.get(member.nickname));
                    posterOverviewResponse.setPosterId(tuple.get(poster.id));
                    posterOverviewResponse.setTitle(tuple.get(poster.title));
                    posterOverviewResponse.setContent(tuple.get(poster.pureContent));
                    posterOverviewResponse.setIsAchieve(tuple.get(poster.isAchieve));
                    posterOverviewResponse.setLikeCnt(tuple.get(likeCountQuery));

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    posterOverviewResponse.setCreatedDate(tuple.get(poster.createdDate).format(dateTimeFormatter));

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

                    // 태그 목록 조회
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


    @Override
    public Page<PosterOverviewResponse> findLikePosterOverviewByMemberId(Long memberId, int page, int size) {
        QPoster poster = QPoster.poster;
        QMember member = QMember.member;
        QProfileImage profileImage = QProfileImage.profileImage;
        QPosterTag posterTag = QPosterTag.posterTag;
        QTag tag = QTag.tag;
        QPosterLike posterLike = QPosterLike.posterLike;

        Pageable pageable = PageRequest.of(page - 1, size);

        // 좋아요 개수 조회 서브쿼리
        JPAQuery<Long> likeCountQuery = jpaQueryFactory
                .select(posterLike.count())
                .from(posterLike)
                .where(posterLike.poster.id.eq(poster.id));

        // 쿼리 빌드
        JPAQuery<Tuple> tupleJPAQuery = jpaQueryFactory
                .select(poster.id, member.id, member.nickname, member.email, member.provider, member.providerId, profileImage.storeFileName, poster.title, poster.pureContent, poster.createdDate, poster.isAchieve, likeCountQuery)
                .from(posterLike)
                .leftJoin(member).on(member.id.eq(poster.member.id))
                .leftJoin(profileImage).on(profileImage.member.id.eq(member.id))
                .leftJoin(poster).on(poster.id.eq(posterLike.poster.id))
                .where(posterLike.member.id.eq(memberId))
                .offset((page - 1) * size)
                .limit(size)
                .orderBy(posterLike.id.desc());

        // 카운트 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(posterLike.count())
                .from(posterLike)
                .leftJoin(member).on(member.id.eq(posterLike.member.id))
                .where(member.id.eq(memberId));

        List<PosterOverviewResponse> overviewResponses = tupleJPAQuery.fetch()
                .stream()
                .map(tuple -> {

                    PosterOverviewResponse posterOverviewResponse = new PosterOverviewResponse();
                    posterOverviewResponse.setMemberId(tuple.get(member.id));
                    posterOverviewResponse.setNickname(tuple.get(member.nickname));
                    posterOverviewResponse.setPosterId(tuple.get(poster.id));
                    posterOverviewResponse.setTitle(tuple.get(poster.title));
                    posterOverviewResponse.setContent(tuple.get(poster.pureContent));
                    posterOverviewResponse.setIsAchieve(tuple.get(poster.isAchieve));
                    posterOverviewResponse.setLikeCnt(tuple.get(likeCountQuery));

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    posterOverviewResponse.setCreatedDate(tuple.get(poster.createdDate).format(dateTimeFormatter));

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

                    // 태그 목록 조회
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
