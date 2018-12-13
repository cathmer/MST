package application;

import network.Component;
import network.GraphCreator;
import network.GraphReader;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String hostName = "145.94.189.95";

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
            System.setProperty("java.rmi.server.hostname",hostName);
        }

        // Start registry
        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.exit(1);
        }

//        ArrayList<Component> components = new GraphReader().readGraph();
        ArrayList<Component> components = new GraphCreator().createCompleteGraph(1000);
        for (Component component : components) {
            System.out.println(component);
        }

        for (Component component : components) {
            component.graphReady();
        }
    }
}
