package network;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphCreator {

    public ArrayList<Component> createCompleteGraph(int n) {
        ArrayList<Component> components = new ArrayList<>();
        HashMap<Integer, String> receiverMap = new HashMap<>();

        for (int i = 1; i <= n; i++) {
            receiverMap.put(i, "localhost");
        }

        for (int i = 1; i <= n; i++) {
            Component component = new Component(i, receiverMap);

            for (int j = 1; j <= n; j++) {
                Edge edge = null;
                if (j == i + 1 && i != n) {
                    edge = new Edge(i, i, j);
                } else if (i == n && j == 1) {
                    edge = new Edge(i, i, 1);
                } else if (j == i - 1 && i != 1) {
                    edge = new Edge(j, i, j);
                } else if (i == 1 && j == n) {
                    edge = new Edge(j, i, j);
                } else if (j != i) {
                    edge = new Edge(Integer.MAX_VALUE, i, j);
                }

                if (edge != null) {
                    component.addEdge(edge);
                }
            }

            components.add(component);
        }

        return components;
    }
}
