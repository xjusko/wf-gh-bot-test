/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
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
package org.wildfly.extension.microprofile.config.test;

import jakarta.enterprise.inject.spi.DeploymentException;

import org.jboss.arquillian.container.spi.client.container.DeploymentExceptionTransformer;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2020 Red Hat inc.
 */
public class WildFlyDeploymentExceptionTransformer implements DeploymentExceptionTransformer {

    public Throwable transform(Throwable throwable) {
        // Due to https://issues.redhat.com/browse/WFARQ-59 if the deployment fails, WildFly Arquillian
        // returns a DeploymentException without a cause. In that case (throwable == null), we create a new DeploymentException
        // so that the test will properly get a DeploymentException (the actual cause of the deployment failure is lost though).
        if (throwable == null) {
            return new DeploymentException("Deployment on WildFly was unsuccessful. Look at the WildFly server logs to have more information on the actual cause of the deployment failure");
        }
        return throwable;
    }

}