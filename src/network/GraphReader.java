package network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GraphReader {

    public ArrayList<Component> readGraph() {
        ArrayList<Component> components = new ArrayList<>();
        HashMap<Integer, Component> componentsMap = new HashMap<>();
        HashMap<Integer, String> hostMap = new HashMap<>();

        File file = new File("./graph2.conf");
        try {
            Scanner sc = new Scanner(file);
            int counter = Integer.MAX_VALUE;
            int numberOfComponents = 0;
            while (sc.hasNextLine()) {
                if (counter == Integer.MAX_VALUE) {
                    numberOfComponents = sc.nextInt();
                    counter = 0;
                } else if (counter < numberOfComponents) {
                    counter++;
                    int processId = sc.nextInt();
                    String host = sc.next();
                    hostMap.put(processId, host);
                } else {
                    int node1 = sc.nextInt();
                    int node2 = sc.nextInt();
                    int weight = sc.nextInt();

                    processEdge(weight, node1, node2, components, componentsMap, hostMap);
                    processEdge(weight, node2, node1, components, componentsMap, hostMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return components;
    }

    private void processEdge(int weight, int node1, int node2, ArrayList<Component> components,
                             HashMap<Integer, Component> componentsMap, HashMap<Integer, String> hostMap) {
        Edge edge = new Edge(weight, node1, node2);
        if (componentsMap.containsKey(node1)) {
            componentsMap.get(node1).addEdge(edge);
        } else {
            Component component = new Component(node1, hostMap);
            component.addEdge(edge);
            componentsMap.put(node1, component);
            components.add(component);
        }
    }
}
