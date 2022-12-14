/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
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
package org.jboss.as.test.integration.ejb.singleton.concurrency.inheritance;

import java.util.concurrent.CountDownLatch;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;

/**
 * @author Stuart Douglas
 */
@Singleton
@Lock(LockType.READ)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonChildBean extends SingletonBaseBean {


    //this is now a read lock
    @AccessTimeout(value = 0)
    public void writeLockOverriddenByParent(CountDownLatch cont, CountDownLatch entered) throws InterruptedException {
        super.writeLockOverriddenByParent(cont, entered);
    }

    @Lock(LockType.WRITE)
    @AccessTimeout(value = 0)
    public void readLockOverriddenByParent(CountDownLatch cont, CountDownLatch entered) throws InterruptedException {
        super.readLockOverriddenByParent(cont, entered);
    }

}
