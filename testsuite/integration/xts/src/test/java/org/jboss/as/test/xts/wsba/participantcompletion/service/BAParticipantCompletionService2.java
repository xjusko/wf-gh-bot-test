/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

package org.jboss.as.test.xts.wsba.participantcompletion.service;

import org.jboss.as.test.xts.base.TestApplicationException;
import org.jboss.as.test.xts.util.ServiceCommand;

import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.servlet.annotation.WebServlet;

@WebService(serviceName = "BAParticipantCompletionService2",
        portName = "BAParticipantCompletion", name = "BAParticipantCompletion",
        targetNamespace = "http://www.jboss.com/jbossas/test/xts/ba/participantcompletion/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@HandlerChain(file = "/context-handlers.xml")
@WebServlet(name = "BAParticipantCompletionService2", urlPatterns = {"/BAParticipantCompletionService2"})
public class BAParticipantCompletionService2 extends BAParticipantCompletionSuperService {
    public static final String SERVICE_EVENTLOG_NAME = "baparticipant_completition_service2";

    @WebMethod
    public void saveData(ServiceCommand... serviceCommands) throws TestApplicationException {
        super.saveData(SERVICE_EVENTLOG_NAME, serviceCommands);
    }
}
