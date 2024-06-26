package com.sonam.ecommerce.ecommercebackend.security;

import com.sonam.ecommerce.ecommercebackend.repository.TokenRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenRepo tokenRepo;

    // invoked for every request to the app.
    // Inspects 'Authorization' header to identify and validate a Bearer token.
    // Authenticate users if token is valid.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // checks for Authorization header in request
        String requestHeader = request.getHeader("Authorization");
        //checks for Bearer token
        logger.info(" Header :  {}", requestHeader);
        String username = null;
        String token = null;

        //If 'Authorization' and 'Bearer' are found, validate and authenticate the token
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7); //extract access token
            try {
                username = this.jwtHelper.extractUsername(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!");
               e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Invalid Token");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("Invalid Header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //fetch user detail from username
            //validate token, and create 'Authentication' object
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            //check if token is valid on database side.
            var isTokenValid = tokenRepo.findByToken(token)
                    .map(t-> !t.isExpired())
                    .orElse(false);

            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken && isTokenValid) {
                //set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication); //
            } else {
                logger.info("Validation failed!!");
            }
        }
        filterChain.doFilter(request, response);
    }
}
