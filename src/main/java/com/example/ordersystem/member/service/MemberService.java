package com.example.ordersystem.member.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.dto.LoginDto;
import com.example.ordersystem.member.dto.MemberResDto;
import com.example.ordersystem.member.dto.MemberSaveReqDto;
import com.example.ordersystem.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public Long save(MemberSaveReqDto dto){
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
        String password = passwordEncoder.encode(dto.getPassword());
        Member member = memberRepository.save(dto.toEntity(password));
        return member.getId();

    }

    public List<MemberResDto> findAll(){
        return memberRepository.findAll().stream().map(m->m.listDtoFromEntity()).collect(Collectors.toUnmodifiableList());
    }
    public MemberResDto myInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));
        MemberResDto dto = member.listDtoFromEntity();
        return dto;
    }


    public Member login(LoginDto dto){
        boolean check = true;
//        email존재여부
        Optional<Member> optionalMember = memberRepository.findByEmail(dto.getEmail());
        if(!optionalMember.isPresent()){
            check = false;
        }
//        password일치 여부
        if(!passwordEncoder.matches(dto.getPassword(), optionalMember.get().getPassword())){
            check =false;
        }
        if(!check){
            throw new IllegalArgumentException("email 또는 비밀번호가 일치하지 않습니다.");
        }
        return optionalMember.get();
    }
}
