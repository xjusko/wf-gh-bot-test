/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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

package org.wildfly.clustering.singleton.server;

import java.util.function.Supplier;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.service.SimpleServiceNameProvider;
import org.wildfly.clustering.service.SupplierDependency;
import org.wildfly.clustering.singleton.SingletonElectionListener;
import org.wildfly.clustering.singleton.SingletonElectionPolicy;
import org.wildfly.clustering.singleton.SingletonService;
import org.wildfly.clustering.singleton.SingletonServiceBuilder;
import org.wildfly.clustering.singleton.service.SingletonServiceConfigurator;

/**
 * Local {@link SingletonServiceConfigurator} implementation that uses JBoss MSC 1.3.x service installation.
 * @author Paul Ferraro
 */
@Deprecated
public class LocalSingletonServiceBuilder<T> extends SimpleServiceNameProvider implements SingletonServiceBuilder<T>, LocalSingletonServiceContext {

    private final Service<T> service;
    private final SupplierDependency<Group> group;
    private volatile SingletonElectionListener listener;

    public LocalSingletonServiceBuilder(ServiceName name, Service<T> service, LocalSingletonServiceConfiguratorContext context) {
        super(name);
        this.service = service;
        this.group = context.getGroupDependency();
        this.listener = new DefaultSingletonElectionListener(name, this.group);
    }

    @Override
    public SingletonServiceBuilder<T> requireQuorum(int quorum) {
        // Quorum requirements are inconsequential to a local singleton
        return this;
    }

    @Override
    public SingletonServiceBuilder<T> electionPolicy(SingletonElectionPolicy policy) {
        // Election policies are inconsequential to a local singleton
        return this;
    }

    @Override
    public SingletonServiceBuilder<T> electionListener(SingletonElectionListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public ServiceBuilder<T> build(ServiceTarget target) {
        SingletonService<T> service = new LocalLegacySingletonService<>(this.service, this);
        return this.group.register(target.addService(this.getServiceName(), service));
    }

    @Override
    public Supplier<Group> getGroup() {
        return this.group;
    }

    @Override
    public SingletonElectionListener getElectionListener() {
        return this.listener;
    }
}
