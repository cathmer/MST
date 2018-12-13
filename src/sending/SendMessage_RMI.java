package sending;

import messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SendMessage_RMI extends Remote {

    void sendMessage(Message message) throws RemoteException;
}
