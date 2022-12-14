/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.as.test.integration.jpa.basic;

import java.util.Map;
import jakarta.annotation.Resource;
import jakarta.ejb.EJBContext;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContexts;

/**
 * stateless session bean
 *
 * @author Scott Marlow
 */
@Stateless
@PersistenceContexts({
        @PersistenceContext(name = "pu1", unitName = "pu1"),
        @PersistenceContext(name = "pu2", unitName = "pu2")
})
public class SLSBPersistenceContexts {

    @Resource
    EJBContext ctx;

    public Map<String, Object> getPU1Info() {
        EntityManager em = (EntityManager) ctx.lookup("pu1");
        return em.getEntityManagerFactory().getProperties();
    }

    public Map<String, Object> getPU2Info() {
        EntityManager em = (EntityManager) ctx.lookup("pu2");
        return em.getEntityManagerFactory().getProperties();
    }

}
