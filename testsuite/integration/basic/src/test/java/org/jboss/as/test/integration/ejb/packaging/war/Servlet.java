/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Inc., and individual contributors as indicated
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

package org.jboss.as.test.integration.ejb.packaging.war;

import java.io.IOException;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * @author Ondrej Chaloupka
 */
public class Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    JarBean jarBean;

    @EJB
    WarBean warBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object attr = req.getParameter("archive");

        if ("jar".equals(attr)) {
            try {
                resp.getOutputStream().print(jarBean.checkMe());
            } catch (Exception e) {
                resp.getOutputStream().print("error");
            }
        }
        if ("war".equals(attr)) {
            try {
                resp.getOutputStream().print(warBean.checkMe());
            } catch (Exception e) {
                resp.getOutputStream().print("error");
            }
        }
    }

}
