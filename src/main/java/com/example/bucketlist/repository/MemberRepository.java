package com.example.bucketlist.repository;

import com.example.bucketlist.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByLoginId(String loginId);
    Boolean existsByEmail(String email);
    Boolean existsByLoginId(String loginId);
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

}
