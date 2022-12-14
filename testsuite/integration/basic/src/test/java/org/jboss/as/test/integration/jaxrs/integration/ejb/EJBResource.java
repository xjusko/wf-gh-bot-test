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
package org.jboss.as.test.integration.jaxrs.integration.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("ejbInterceptor")
@Produces({"text/plain"})
@Stateless(name = "CustomName")
@Interceptors(EjbInterceptor.class)
public class EJBResource implements EjbInterface {

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @GET
    public String getMessage() throws SystemException {
        if(transactionSynchronizationRegistry.getTransactionStatus() != Status.STATUS_ACTIVE) {
            throw new RuntimeException("Transaction not active, not an EJB invocation");
        }
        return "Hello";
    }
}
