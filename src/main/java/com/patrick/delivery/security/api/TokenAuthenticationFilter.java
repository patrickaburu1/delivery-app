package com.patrick.delivery.security.api;

import com.patrick.delivery.security.service.CustomApiUserDetailsService;
import com.patrick.delivery.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class TokenAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Log logger = LogFactory.getLog(this.getClass());
    private final TokenHelper tokenHelper;
    private final CustomApiUserDetailsService userDetailsService;


    public TokenAuthenticationFilter(TokenHelper tokenHelper, CustomApiUserDetailsService userDetailsService) {

        this.tokenHelper = tokenHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authToken = httpRequest.getHeader("Authorization");
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
            if (this.tokenHelper.isTokenExpired(authToken)) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getOutputStream().println(new ResponseModel("003", "Your session has expired, please login").toString());
                MDC.clear();
            } else if (!this.tokenHelper.isTokenValid(authToken)) {
                logger.info("===========Invalid Token: " + authToken);
                //((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);

                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getOutputStream().println(new ResponseModel("01", "Invalid request").toString());

                MDC.clear();
            } else {
                ((HttpServletResponse) response).setStatus(200);

                String email = this.tokenHelper.getUserNameFromToken(authToken);

                logger.info("*********TOKEN VALID: " + email);
                //logger.info("*********TOKEN VALID: "+this.tokenHelper.validateToken(authToken,userDetails.getUsername()));

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                    Map<String, Object> ret = this.tokenHelper.validateToken(authToken, userDetails.getUsername());
                    if ((Boolean) ret.get("valid")) {
                        TokenBasedAuthentication authentication = new TokenBasedAuthentication(userDetails);
                        authentication.setToken(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Proceed only for successful requests
                        chain.doFilter(request, response);
                        MDC.clear();
                    } else {
                        logger.info("===========Invalid Token Claims: " + authToken);

                        ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getOutputStream().println(new ResponseModel("01", "Invalid request").toString());

                        MDC.clear();
                    }
                } else {
                    logger.info("===========Email not null, Security context: " + email);

                    ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getOutputStream().println(new ResponseModel("01", "Invalid request").toString());

                    MDC.clear();
                }
            }
        } else {

            chain.doFilter(request, response);
            MDC.clear();
        }
    }
}
