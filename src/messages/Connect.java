package messages;

public class Connect extends Message {

    private int level;

    public Connect(int fromProcessId, int toProcessId, int level) {
        super(fromProcessId, toProcessId, MessageType.CONNECT);
        this.level = level;
    }

    public int getLevel() { return level; }

    @Override
    public String toString() {
        return super.toString() + ", level: " + level;
    }
}
