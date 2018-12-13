package sending;

import messages.*;
import network.Component;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SendMessage implements SendMessage_RMI {

    private Component component;

    public SendMessage(Component component) {
        this.component = component;
    }

    public void sendMessage(Message msg) {
        // Start new thread here
        new Thread(() -> {
            try {
//                int randomNum = ThreadLocalRandom.current().nextInt(0, 3);
//                try {
//                    TimeUnit.SECONDS.sleep(randomNum);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                // Switch between different message types here
                switch (msg.getMsgType()) {
                    case TEST:
                        Test test = (Test) msg;
                        component.receiveTest(test);
                        break;
                    case ACCEPT:
                        Accept accept = (Accept) msg;
                        component.receiveAccept(accept);
                        break;
                    case REJECT:
                        Reject reject = (Reject) msg;
                        component.receiveReject(reject);
                        break;
                    case REPORT:
                        Report report = (Report) msg;
                        component.receiveReport(report);
                        break;
                    case CONNECT:
                        Connect connect = (Connect) msg;
                        component.receiveConnect(connect);
                        break;
                    case INITIATE:
                        Initiate initiate = (Initiate) msg;
                        component.receiveInitiate(initiate);
                        break;
                    case CHANGEROOT:
                        ChangeRoot changeRoot = (ChangeRoot) msg;
                        component.receiveChangeRoot(changeRoot);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String registryName, String receiver, Message message) {
        // Sends a message using the remote method of another host
        try {
            String name = registryName;
//            String receiver = "localhost";
            Registry remoteReg = LocateRegistry.getRegistry(receiver);
            SendMessage_RMI send = (SendMessage_RMI) remoteReg.lookup(name);
            System.out.println("Sent message: " + message);
            send.sendMessage(message);
        } catch (Exception e) {
            System.err.println("Sending request exception:");
            e.printStackTrace();
        }
    }
}
