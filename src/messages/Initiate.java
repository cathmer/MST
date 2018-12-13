package messages;

import network.Component;

public class Initiate extends Message {

    private int level;
    private int name;
    private Component.State state;

    public Initiate(int fromProcessId, int toProcessId, int level, int name, Component.State state) {
        super(fromProcessId, toProcessId, MessageType.INITIATE);
        this.level = level;
        this.name = name;
        this.state = state;
    }

    public int getLevel() {
        return level;
    }

    public int getName() {
        return name;
    }

    public Component.State getState() {
        return state;
    }

    @Override
    public String toString() {
        return super.toString() + ", level: " + level + ", name: " + name + ", state: " + state;
    }
}
