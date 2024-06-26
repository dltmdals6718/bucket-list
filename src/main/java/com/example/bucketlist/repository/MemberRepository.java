package com.example.bucketlist.repository;

import com.example.bucketlist.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findMemberByLoginId(String loginId);
    Boolean existsByEmail(String email);
    Boolean existsByLoginId(String loginId);

}
