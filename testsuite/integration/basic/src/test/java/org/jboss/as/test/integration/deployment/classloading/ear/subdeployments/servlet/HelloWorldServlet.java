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

package org.jboss.as.test.integration.deployment.classloading.ear.subdeployments.servlet;

import org.jboss.as.test.integration.deployment.classloading.ear.subdeployments.ejb.EJBBusinessInterface;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Jaikiran Pai
 */
@WebServlet(name = "HelloWorldServlet", urlPatterns = HelloWorldServlet.URL_PATTERN)
public class HelloWorldServlet extends HttpServlet {

    public static final String URL_PATTERN = "/helloworld";

    public static final String PARAMETER_NAME = "message";
    @EJB
    private EJBBusinessInterface echoBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String message = req.getParameter(PARAMETER_NAME);
        if (message == null) {
            throw new ServletException(PARAMETER_NAME + " parameter not set in request");
        }
        final String echo = this.echoBean.echo(message);
        // print out the echo message
        resp.getOutputStream().print(echo);
    }
}
