package com.SneakySolo.SecureWatch.Filter;

import com.SneakySolo.SecureWatch.Entity.EntityType;
import com.SneakySolo.SecureWatch.Entity.User;
import com.SneakySolo.SecureWatch.Repository.BlockedEntityRepository;
import com.SneakySolo.SecureWatch.Repository.UserRepository;
import com.SneakySolo.SecureWatch.Service.DetectionService;
import com.SneakySolo.SecureWatch.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final DetectionService detectionService;
    private final BlockedEntityRepository blockedRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                // continue coz spring security will not let it pass
            }
        }

        if (blockedRepository.existsByEntityTypeAndEntityValue(EntityType.IP, request.getRemoteAddr())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: IP is blocked");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.ValidateToken(token, user)) {

                User dbUser = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (dbUser.isBlocked()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Access denied: Account is blocked");
                    return;
                }

                detectionService.checkForRapidRequests(username, request.getRemoteAddr());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
