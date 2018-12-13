package sending;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StartRegistry {

    /**
     * Method to start the registry.
     * @param registryName : the name of the registry.
     * @param sendMsg : the Send object which can be invoked by a different process.
     */
    public void startRegistry(String registryName, SendMessage sendMsg) {
        // Initiate registry
        try {
            String name = registryName;
            SendMessage_RMI stub =
                    (SendMessage_RMI) UnicastRemoteObject.exportObject(sendMsg, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
//            System.out.println("Send bound to " + registryName);
        } catch (Exception e) {
            System.err.println("Send exception:");
            e.printStackTrace();
        }
    }
}
