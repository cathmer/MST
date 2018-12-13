package messages;

public class Report extends Message {

    private int weight;

    public Report(int fromProcessId, int toProcessId, int weight) {
        super(fromProcessId, toProcessId, MessageType.REPORT);
        this.weight = weight;
    }

    public int getWeight() { return weight; }

    @Override
    public String toString() {
        return super.toString() + ", weight: " + weight;
    }
}
