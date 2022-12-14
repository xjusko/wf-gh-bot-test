/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

import java.io.Serializable;
import java.util.function.Consumer;

import org.jboss.as.clustering.controller.CapabilityServiceConfigurator;
import org.jboss.as.controller.capability.CapabilityServiceSupport;
import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.wildfly.clustering.server.service.ClusteringCacheRequirement;
import org.wildfly.clustering.service.ServiceConfigurator;
import org.wildfly.clustering.service.SimpleServiceNameProvider;
import org.wildfly.clustering.singleton.service.SingletonServiceConfiguratorFactory;

/**
 * Configures a service providing a local {@link org.wildfly.clustering.singleton.service.SingletonServiceConfiguratorFactory}.
 * @author Paul Ferraro
 */
public class LocalSingletonServiceConfiguratorFactoryServiceConfigurator<T extends Serializable> extends SimpleServiceNameProvider implements CapabilityServiceConfigurator, LocalSingletonServiceConfiguratorFactoryContext {

    private final String containerName;
    private final String cacheName;

    private volatile ServiceName groupServiceName;

    public LocalSingletonServiceConfiguratorFactoryServiceConfigurator(ServiceName name, String containerName, String cacheName) {
        super(name);
        this.containerName = containerName;
        this.cacheName = cacheName;
    }

    @Override
    public ServiceConfigurator configure(CapabilityServiceSupport support) {
        this.groupServiceName = ClusteringCacheRequirement.GROUP.getServiceName(support, this.containerName, this.cacheName);
        return this;
    }

    @Override
    public ServiceBuilder<?> build(ServiceTarget target) {
        ServiceBuilder<?> builder = target.addService(this.getServiceName());
        Consumer<SingletonServiceConfiguratorFactory> factory = builder.provides(this.getServiceName());
        Service service = Service.newInstance(factory, new LocalSingletonServiceBuilderFactory(this));
        return builder.setInstance(service);
    }

    @Override
    public ServiceName getGroupServiceName() {
        return this.groupServiceName;
    }
}
