/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.test.integration.ejb.interceptor.invocationcontext;

import jakarta.annotation.PostConstruct;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

/**
 * @author OndrejChaloupka
 */
public class MethodInterceptor {
    private static final Logger log = Logger.getLogger(MethodInterceptor.class);

    @AroundInvoke
    Object intercept(InvocationContext ctx) throws Exception {
        String ret = InvocationContextChecker.checkBeanInterceptorContext(ctx, "Class", "Method");
        return ret + ctx.proceed();
    }

    Object interceptTimeout(InvocationContext ctx) throws Exception {
        String ret = InvocationContextChecker.checkTimeoutInterceptorContext(ctx, "Class", "Method");
        TimeoutBean.interceptorResults += ret;
        return ctx.proceed();
    }

    @PostConstruct
    void postConstruct(InvocationContext ctx) {
        log.trace("PostConstruct on MethodInterceptor called");
        if (ctx.getMethod() != null) {
            throw new RuntimeException("InvocationContext.getMethod() on lifecycle event has to be null");
        }
    }
}
