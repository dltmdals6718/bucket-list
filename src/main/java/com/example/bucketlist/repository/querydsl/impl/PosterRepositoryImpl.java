package com.example.bucketlist.repository.querydsl.impl;

import com.example.bucketlist.domain.*;
import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.dto.response.QPosterDetailsResponse;
import com.example.bucketlist.repository.querydsl.PosterRepositoryCustom;
import com.example.bucketlist.utils.DefaultProfileImageUtil;
import com.example.bucketlist.utils.S3Uploader;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class PosterRepositoryImpl implements PosterRepositoryCustom {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;
    private final S3Uploader s3Uploader;

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

        List<String> tags = jpaQueryFactory
                .select(tag.name)
                .from(posterTag)
                .join(tag).on(posterTag.tag.id.eq(tag.id))
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
                .innerJoin(poster.member, member)
                .leftJoin(profileImage).on(poster.member.id.eq(profileImage.member.id))
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
}
