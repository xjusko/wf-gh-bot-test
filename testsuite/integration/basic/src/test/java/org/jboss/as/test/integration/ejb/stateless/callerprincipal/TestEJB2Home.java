package org.jboss.as.test.integration.ejb.stateless.callerprincipal;

import jakarta.ejb.CreateException;
import jakarta.ejb.EJBHome;
import java.rmi.RemoteException;

public interface TestEJB2Home extends EJBHome {

   TestEJB2 create() throws RemoteException, CreateException;
}

