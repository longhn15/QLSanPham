// package com.example.QLSanPham.security;

// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.example.QLSanPham.entity.User;
// import com.example.QLSanPham.repository.UserRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class UserDetailsServiceImpl implements UserDetailsService {

//     private final UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         User user = userRepository.findByUsername(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

//         // Chuyển đổi User Entity của mình thành UserDetails của Spring Security
//         return org.springframework.security.core.userdetails.User
//                 .withUsername(user.getUsername())
//                 .password(user.getPassword())
//                 .roles(user.getRoles().toArray(new String[0])) // VD: ["ADMIN", "USER"]
//                 .disabled(!user.isActive())
//                 .build();
//     }
// }
