package messages;

public class Test extends Message {

    private int level;
    private int name;

    public Test(int fromProcessId, int toProcessId, int level, int name) {
        super(fromProcessId, toProcessId, MessageType.TEST);
        this.level = level;
        this.name = name;
    }

    public int getLevel() { return level; }

    public int getName() { return name; }

    @Override
    public String toString() {
        return super.toString() + ", level: " + level + ", name: " + name;
    }
}
