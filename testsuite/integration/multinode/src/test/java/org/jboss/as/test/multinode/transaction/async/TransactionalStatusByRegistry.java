/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.multinode.transaction.async;

import java.util.concurrent.Future;
import jakarta.annotation.Resource;
import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.transaction.TransactionSynchronizationRegistry;

/**
 * Asynchronously invoked bean where we expect that transaction registry returns
 * no active status for "current" transaction as propagation should not occur.
 *
 * @author Ondrej Chaloupka
 */
@Stateless
@Asynchronous
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TransactionalStatusByRegistry implements TransactionalRemote {

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    public Future<Integer> transactionStatus() {
        return new AsyncResult<Integer>(transactionSynchronizationRegistry.getTransactionStatus());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Future<Integer> asyncWithRequired() {
        throw new RuntimeException("Throw RuntimeException on purpose to cause the transaction rollback");
    }
}
