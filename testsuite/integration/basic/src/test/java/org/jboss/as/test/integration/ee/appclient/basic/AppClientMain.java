/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.as.test.integration.ee.appclient.basic;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;

import org.jboss.logging.Logger;

/**
 * @author Stuart Douglas
 */
public class AppClientMain {
    private static final Logger logger = Logger.getLogger("org.jboss.as.test.appclient");

    @Resource(lookup = "java:comp/InAppClientContainer")
    private static boolean appclient;

    @EJB
    private static AppClientSingletonRemote appClientSingletonRemote;

    public static void main(final String[] params) {
        logger.trace("Main method invoked");

        if(!appclient) {
            logger.error("InAppClientContainer was not true");
            throw new RuntimeException("InAppClientContainer was not true");
        }

        try {
            appClientSingletonRemote.makeAppClientCall(params[0]);
            logger.trace("Main method invocation completed with success");
        } catch (Exception e) {
            logger.error("Main method failed", e);
        }
    }

}
