package by.bsuir.dormitoryinspection.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain)
          throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      try {
        String token = authHeader.substring(7);

        if (jwtService.validateToken(token)) {
          String username = jwtService.extractUsername(token);
          String role = jwtService.extractRole(token);

          User UserDetails = new User(
                  username,
                  "",
                  List.of(new SimpleGrantedAuthority("ROLE_" + role))
          );

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  UserDetails, null, UserDetails.getAuthorities()
          );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
          return;
        }
      } catch (Exception e) {
        log.error("JWT filter error: {}", e.getMessage());
      }
    }
    chain.doFilter(request, response);
  }
}
