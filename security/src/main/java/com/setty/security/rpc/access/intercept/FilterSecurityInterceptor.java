package com.setty.security.rpc.access.intercept;

import com.setty.security.access.SecurityMetadataSource;
import com.setty.security.access.intercept.AbstractSecurityInterceptor;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author HuSen
 * create on 2019/7/12 16:06
 */
public class FilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    @Override
    public Class<?> getSecureObjectClass() {
        return null;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {

    }
}
