package messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private static final long serialVersionUID = 7526471155622776147L;
    private int fromProcessId;
    private int toProcessId;
    private MessageType msgType;

    public enum MessageType {
        INITIATE, TEST, ACCEPT, REJECT, REPORT, CHANGEROOT, CONNECT
    };

    public Message(int fromProcessId, int toProcessId, MessageType msgType) {
        this.fromProcessId = fromProcessId;
        this.toProcessId = toProcessId;
        this.msgType = msgType;
    }

    public int getFromProcessId() { return fromProcessId; }

    public int getToProcessId() { return toProcessId; }

    public MessageType getMsgType() { return msgType; }

    @Override
    public String toString() {
        return msgType + " message from " + fromProcessId + " to " + toProcessId;
    }
}
