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
package org.jboss.as.test.integration.ejb.packaging.war.namingcontext;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.transaction.UserTransaction;

/**
 * @author Stuart Douglas
 */
@Stateless
@LocalBean
@TransactionManagement(value = TransactionManagementType.BEAN)
public class War1Ejb implements EjbInterface {

    @Resource
    public UserTransaction ut1;


    public UserTransaction lookupUserTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:comp/env/" + getClass().getName() + "/ut1");
    }

    @Override
    public UserTransaction lookupOtherUserTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:comp/env/" + getClass().getPackage().getName() + "War2Ejb/ut2");
    }
}
