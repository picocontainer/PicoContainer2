package org.nanocontainer.remoting.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RegistryHelper {
    private static Registry registry;

    public static Registry getRegistry() {
        return registry;
    }

    static {
        try {
            registry = LocateRegistry.createRegistry(9877);
        } catch (RemoteException e) {
            try {
                registry = LocateRegistry.getRegistry(9877);
            } catch (RemoteException e1) {
                throw new RuntimeException(e1.getMessage());
            }
        }
    }


}