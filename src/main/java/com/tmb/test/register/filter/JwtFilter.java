package com.tmb.test.register.filter;

import com.tmb.test.register.exception.TokenExpiredException;
import com.tmb.test.register.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final AuthenticationService service;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        Optional<String> authorizeHeader = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
        try {
            if (authorizeHeader.isPresent() && authorizeHeader.get().startsWith("Bearer")) {
                String[] bearer = authorizeHeader.get().split(" ");
                String token = bearer[1];
                String userId = service.verifyToken(token);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } catch (TokenExpiredException ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
