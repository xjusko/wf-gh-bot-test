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

package org.jboss.as.test.integration.jpa.sibling;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.UserTransaction;

/**
 * stateful session bean
 *
 * @author Scott Marlow
 */
@Stateful
public class SFSBTopLevel {
    @PersistenceContext(unitName = "mypc")
    EntityManager em;

    @Resource
    SessionContext sessionContext;

    @EJB
    DAO1 otherBean1; // sibling 1

    @EJB
    DAO2 otherBean2; // sibling 2

    public String testfunc() {
        otherBean1.myFunction();
        otherBean2.myFunction();
        return "fine";
    }

    /**
     * The PostConstruct callback invocations occur before the first business method invocation on thebean.
     * This is at a point after which any dependency injection has been performed by the container.
     */
    @PostConstruct
    public void postconstruct() {
        //System.out.println("SFSBTopLevel PostConstruct occurred for " + this.toString() + ", current thread=" + Thread.currentThread().getName() + ", all dependency injection has been performed.");
    }

    public void createEmployee(String name, String address, int id) {


        Employee emp = new Employee();
        emp.setId(id);
        emp.setAddress(address);
        emp.setName(name);
        em.persist(emp);
    }

    // always throws a TransactionRequiredException
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void createEmployeeNoTx(String name, String address, int id) {


        Employee emp = new Employee();
        emp.setId(id);
        emp.setAddress(address);
        emp.setName(name);

        UserTransaction tx1 = sessionContext.getUserTransaction();
        try {
            tx1.begin();
            em.joinTransaction();
            em.persist(emp);
            tx1.commit();
        } catch (Exception e) {
            throw new RuntimeException("couldn't start tx", e);
        }

        em.flush();         // should throw TransactionRequiredException
    }


    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Employee getEmployeeNoTX(int id) {

        return em.find(Employee.class, id);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public String queryEmployeeNameNoTX(int id) {
        Query q = em.createQuery("SELECT e.name FROM Employee e");
        try {
            String name = (String) q.getSingleResult();
            return name;
        } catch (NoResultException expected) {
            return "success";
        } catch (Exception unexpected) {
            return unexpected.getMessage();
        }

    }


}
