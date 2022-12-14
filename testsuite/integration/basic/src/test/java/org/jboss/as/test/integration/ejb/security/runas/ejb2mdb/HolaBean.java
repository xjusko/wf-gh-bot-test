/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.as.test.integration.ejb.security.runas.ejb2mdb;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Remote;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

/**
 * Returns hola greeting for INTERNAL_ROLE.
 *
 * @author Ondrej Chaloupka
 */
@Stateless(name = "Hola")
@RolesAllowed({})
@Remote(Hola.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class HolaBean implements Hola {
    public static final String SAYING = "Hola";

    @Resource
    private SessionContext context;

    @RolesAllowed("INTERNAL_ROLE")
    public String sayHola() {
        return String.format("%s %s", SAYING, getName());
    }

    private String getName() {
        return context.getCallerPrincipal().getName();
    }

}
