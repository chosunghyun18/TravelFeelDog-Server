package travelfeeldog.domain.member.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import travelfeeldog.domain.member.model.Member;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    @PersistenceContext
    private final EntityManager em;

    public Optional<Member> saveMember(String nickName, int level, int exp, String imageUrl, String token) {
        try {
            findByToken(token).ifPresent(m -> {
                throw new IllegalStateException("가입되어 있습니다.");
            });
            Member member = Member.create(nickName, level, exp, imageUrl, token);
            em.persist(member);
            return Optional.of(member);
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findByToken(String token) {
        try {
            Member member = em.createQuery("SELECT m FROM Member m WHERE m.memberToken = :token", Member.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.of(member);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findByNickName(String nickName) {
        try {
            Member member = em.createQuery("SELECT m FROM Member m WHERE m.memberNickName = :nickName", Member.class)
                    .setParameter("nickName", nickName)
                    .getSingleResult();
            return Optional.of(member);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Member> findById(Long id) {
        try {
            Member member = em.createQuery("SELECT m FROM Member m WHERE m.memberId = :id", Member.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(member);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void deleteMember(String inputToken) {
        Member member = findByToken(inputToken).get();
        em.remove(member);
    }

    public Member updateMemberImageUrl(String memberToken, String newUrl) {
        Member member = findByToken(memberToken).get();
        member.setMemberImageUrl(newUrl);
        em.merge(member);
        return member;
    }

    public Member updateNickName(String memberToken, String newNick) {
        Member member = findByToken(memberToken).get();
        findByNickName(newNick).ifPresent(m -> {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        });
        member.setMemberNickName(newNick);
        em.merge(member);
        return member;
    }

    public Member updateExpAndLevel(String memberToken, int addingExp) {
        Member member = findByToken(memberToken).get();
        em.merge(member);
        return member;
    }

    public List<Member> findAll() {
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }
}