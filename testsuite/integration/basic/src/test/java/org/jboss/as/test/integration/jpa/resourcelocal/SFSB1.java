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

package org.jboss.as.test.integration.jpa.resourcelocal;

import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceUnit;

/**
 * stateful session bean
 *
 * @author Stuart Douglas
 */
@Stateful
public class SFSB1 {
    @PersistenceUnit(unitName = "mypc")
    private EntityManagerFactory emf;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void createEmployeeNoJTATransaction(String name, String address, int id) {
        EntityManager em = emf.createEntityManager();
        Employee emp = new Employee();
        emp.setId(id);
        emp.setAddress(address);
        emp.setName(name);

        EntityTransaction tx1 = em.getTransaction();
        try {
            tx1.begin();
            em.joinTransaction();
            em.persist(emp);
            tx1.commit();
        } catch (Exception e) {
            throw new RuntimeException("couldn't start tx", e);
        }
    }

    public void flushWithNoTx() {
        EntityManager em = emf.createEntityManager();
        em.flush();// should throw TransactionRequiredException
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Employee getEmployeeNoTX(int id) {
        EntityManager em = emf.createEntityManager();

        return em.find(Employee.class, id);
    }

}
