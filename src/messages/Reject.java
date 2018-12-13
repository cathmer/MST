package messages;

public class Reject extends Message {

    public Reject(int fromProcessId, int toProcessId) {
        super(fromProcessId, toProcessId, MessageType.REJECT);
    }
}
