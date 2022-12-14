/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.as.clustering.infinispan.subsystem;

import org.infinispan.Cache;
import org.infinispan.interceptors.AsyncInterceptor;
import org.jboss.as.clustering.controller.BinaryCapabilityNameResolver;
import org.jboss.as.clustering.controller.FunctionExecutorRegistry;

/**
 * Executor for metrics based on a cache interceptor.
 * @author Paul Ferraro
 */
public class CacheInterceptorMetricExecutor<I extends AsyncInterceptor> extends CacheMetricExecutor<I> {

    private final Class<I> interceptorClass;

    public CacheInterceptorMetricExecutor(FunctionExecutorRegistry<Cache<?, ?>> executors, Class<I> interceptorClass) {
        super(executors);
        this.interceptorClass = interceptorClass;
    }

    public CacheInterceptorMetricExecutor(FunctionExecutorRegistry<Cache<?, ?>> executors, Class<I> interceptorClass, BinaryCapabilityNameResolver resolver) {
        super(executors, resolver);
        this.interceptorClass = interceptorClass;
    }

    @SuppressWarnings("deprecation")
    @Override
    public I apply(Cache<?, ?> cache) {
        return cache.getAdvancedCache().getAsyncInterceptorChain().findInterceptorExtending(this.interceptorClass);
    }
}
