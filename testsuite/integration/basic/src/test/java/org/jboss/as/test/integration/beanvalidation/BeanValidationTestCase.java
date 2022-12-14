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

package org.jboss.as.test.integration.beanvalidation;

import jakarta.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests Jakarta Bean Validation within a Jakarta EE component
 * <p/>
 * User: Jaikiran Pai
 */
@RunWith(Arquillian.class)
public class BeanValidationTestCase {

    @EJB(mappedName = "java:module/StatelessBean")
    private StatelessBean slsb;

    @Deployment
    public static JavaArchive getDeployment() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "bean-validation-test.jar");
        jar.addClass(StatelessBean.class);

        return jar;
    }

    /**
     * Test that {@link jakarta.validation.Validation#buildDefaultValidatorFactory()} works fine within an EJB
     */
    @Test
    public void testBuildDefaultValidatorFactory() {
        this.slsb.buildDefaultValidatorFactory();
    }

    /**
     * Test that {@link jakarta.validation.Validator} and {@link jakarta.validation.ValidatorFactory} are injected in a
     * EJB
     */
    @Test
    public void testValidatorInjection() {
        Assert.assertTrue("Validator wasn't injected in EJB", this.slsb.isValidatorInjected());
        Assert.assertTrue("ValidatorFactory wasn't injected in EJB", this.slsb.isValidatorFactoryInjected());
    }

    /**
     * Tests that {@link jakarta.validation.ValidatorFactory#getValidator()} returns a validator
     */
    @Test
    public void testGetValidatorFromValidatorFactory() {
        Assert.assertNotNull("Validator from ValidatorFactory was null", this.slsb.getValidatorFromValidatorFactory());
    }
}
