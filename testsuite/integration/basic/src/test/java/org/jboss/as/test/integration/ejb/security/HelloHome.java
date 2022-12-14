package org.jboss.as.test.integration.ejb.security;

import java.rmi.RemoteException;
import jakarta.ejb.CreateException;
import jakarta.ejb.EJBHome;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public interface HelloHome extends EJBHome {
    HelloRemote create() throws CreateException, RemoteException;
}
