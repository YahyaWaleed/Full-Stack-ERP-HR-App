package com.yahya.erphrapp.authentication.security;


import com.yahya.erphrapp.authentication.entity.HrUser;
import com.yahya.erphrapp.authentication.repository.HrUserRepository;
import com.yahya.erphrapp.exception.ResourceNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService
// any class that implements this interface must have a loadUserByUsername method
// Spring Security will automatically call this method whenever it needs to check a login
{
    // inject the  repo
    private final HrUserRepository hrUserRepository;

    public CustomUserDetailsService(HrUserRepository hrUserRepository) {
        this.hrUserRepository = hrUserRepository;
    }

    // implement the loadUserByUsername method
    @Override
    public UserDetails loadUserByUsername(String username)  {
        // find user by their username
        HrUser hrUser = hrUserRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("HR User", username));

        // return user details
        return new User( // translating HrUser to User that spring security understands
                hrUser.getUsername(),
                hrUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+hrUser.getRole().name()))
        );
    }
}
