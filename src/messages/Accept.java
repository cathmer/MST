package messages;

public class Accept extends Message {

    public Accept(int fromProcessId, int toProcessId) {
        super(fromProcessId, toProcessId, MessageType.ACCEPT);
    }
}
