/*
 * JBoss, Home of Professional Open Source
 * Copyright 2020, Red Hat Inc., and individual contributors as indicated
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

package org.jboss.as.test.integration.ee.naming.defaultbindings.datasource;

import org.junit.Assert;

import jakarta.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This can be used only in tests which remove default datasource binding from ee subsystem.
 *
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 */
public class DefaultDSWithNameServlet extends HttpServlet {

    private boolean hasLookup;

    @Resource(name = "ds")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            hasLookup = Boolean.parseBoolean(getServletConfig().getInitParameter("hasLookup"));
        } catch (Exception e) {
            hasLookup = false;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        if (hasLookup) {
            if (dataSource == null) {
                throw new ServletException("dataSource is null when lookup is defined in jboss-web.xml");
            }
        } else {
            if (dataSource != null) {
                throw new ServletException("No lookup is defined, dataSource should be null");
            }
        }

        if (hasLookup) {
            try {
                Assert.assertNotNull(new InitialContext().lookup("java:comp/env/ds"));
            } catch (NamingException e) {
                throw new IOException("Cannot lookup java:comp/env/ds when has lookup specified", e);
            }
        } else {
            try {
                new InitialContext().lookup("java:comp/env/ds");
                Assert.fail("lookup should fail when no lookup specified for 'java:comp/env/ds'");
            } catch (NamingException e) {
                Assert.assertTrue(e.getMessage().contains("env/ds"));
            }
        }
        out.print("OK");
        out.flush();
    }
}
