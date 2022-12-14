/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.as.ejb3.timerservice;

import org.jboss.metadata.ejb.parser.jboss.ejb3.AbstractEJBBoundMetaData;

/**
 * Encapsulates timer service meta data for an EJB component.
 * @author Stuart Douglas
 * @author Paul Ferraro
 */
public class TimerServiceMetaData extends AbstractEJBBoundMetaData {
    private static final long serialVersionUID = 8290412083429128705L;

    private String dataStoreName;
    private String persistentProvider;
    private String transientProvider;

    public String getDataStoreName() {
        return dataStoreName;
    }

    public void setDataStoreName(final String dataStoreName) {
        this.dataStoreName = dataStoreName;
    }

    public String getPersistentTimerManagementProvider() {
        return this.persistentProvider;
    }

    public void setPersistentTimerManagementProvider(String persistentProvider) {
        this.persistentProvider = persistentProvider;
    }

    public String getTransientTimerManagementProvider() {
        return this.transientProvider;
    }

    public void setTransientTimerManagementProvider(String transientProvider) {
        this.transientProvider = transientProvider;
    }

    @Override
    public String toString() {
        return String.format("data-store=%s, persistent-provider=%s, transient-provider=%s", this.dataStoreName, this.persistentProvider, this.transientProvider);
    }
}
