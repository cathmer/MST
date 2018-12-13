package network;

public class Edge {

    private int weight;
    private int source;
    private int otherEndpoint;
    private EdgeState edgeState;

    public enum EdgeState {
        IN_MST, NOT_IN_MST, MAYBE_IN_MST
    };

    public Edge(int weight, int source, int otherEndpoint) {
        this.weight = weight;
        this.source = source;
        this.otherEndpoint = otherEndpoint;
        this.edgeState = EdgeState.MAYBE_IN_MST;
    }

    public int getWeight() { return weight; }

    public int getOtherEndpoint() { return otherEndpoint; }

    public EdgeState getEdgeState() { return edgeState; }

    public void setEdgeState(EdgeState edgeState) {
        this.edgeState = edgeState;

        if (edgeState == EdgeState.IN_MST) {
            System.out.println("Edge from " + source + " to " + otherEndpoint + " with weight " + weight + " added to MST!");
        }
    }

    @Override
    public String toString() {
        return "[" + source + ", " + otherEndpoint + ", W:" + weight + ", S:" + edgeState + "]";
    }
}
