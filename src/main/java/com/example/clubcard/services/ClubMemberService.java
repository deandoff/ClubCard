package com.example.clubcard.services;

import com.example.clubcard.domain.ClubMember;
import com.example.clubcard.repos.ClubMemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class ClubMemberService implements UserDetailsService {
    @Autowired
    private ClubMemberRepo clubMemberRepo;

    @Autowired
    public ClubMemberService(ClubMemberRepo clubMemberRepo) {
        this.clubMemberRepo = clubMemberRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ClubMember clubMember = clubMemberRepo.findByEmail(email);

        if (clubMember == null) {
           throw new UsernameNotFoundException(email);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(clubMember.getRole());

        return new User(clubMember.getEmail(), clubMember.getPassword(), Collections.singletonList(authority));
    }

    public ClubMember findByUsername(String email) {
        return clubMemberRepo.findByEmail(email);
    }
}
