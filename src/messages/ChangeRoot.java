package messages;

public class ChangeRoot extends Message {

    public ChangeRoot(int fromProcessId, int toProcessId) {
        super(fromProcessId, toProcessId, MessageType.CHANGEROOT);
    }
}
