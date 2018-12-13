package network;

import messages.*;
import sending.SendMessage;
import sending.StartRegistry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Component {

    private int level;
    private int name;
    private State state;
    private Edge inBranch;
    private Edge testEdge;
    private Edge bestEdge;
    private int bestWeight;
    private int findCount;
    private int processId;
    private SendMessage sender;
    private ArrayList<Edge> edges;
    private HashMap<Integer, String> receiverMap;
    private ArrayList<Message> messageQueue;
    private final String REGISTRY_NAME = "SendMessage";
    private boolean somethingChanged = false;
    private boolean tryingQueue = false;

    public enum State {
        SLEEPING, FIND, FOUND
    };

    public Component(int processId, HashMap<Integer, String> receiverMap) {
        this.processId = processId;
        this.edges = edges;
        this.receiverMap = receiverMap;
        this.state = State.SLEEPING;
        this.sender = new SendMessage(this);
        StartRegistry startReg = new StartRegistry();
        startReg.startRegistry(REGISTRY_NAME + processId, sender);
        messageQueue = new ArrayList<>();
        edges = new ArrayList<>();

        bestWeight = Integer.MAX_VALUE;
    }

    public void graphReady() {
        new Thread(() -> {
            try {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 10 + 1);
                try {
                    TimeUnit.SECONDS.sleep(randomNum);
                    if (state == State.SLEEPING) {
                        wakeUp();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public synchronized void wakeUp() {
        System.out.println("Node " + processId + " is waking up!");
        Edge moe = findMOE();
        moe.setEdgeState(Edge.EdgeState.IN_MST);
        level = 0;
        state = State.FOUND;
        findCount = 0;
        int toProcessId = moe.getOtherEndpoint();
        Connect connect = new Connect(processId, toProcessId, 0);
        sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), connect);
    }

    public synchronized void receiveConnect(Connect connect) {
        if (state == State.SLEEPING) {
            wakeUp();
        }

        int receivedFromProcessId = connect.getFromProcessId();
        Edge j = findEdge(receivedFromProcessId);

        if (connect.getLevel() < level) {
            somethingChanged = true;
            j.setEdgeState(Edge.EdgeState.IN_MST);

            Initiate initiate = new Initiate(processId, receivedFromProcessId, level, name, state);
            sender.sendMessage(REGISTRY_NAME + receivedFromProcessId, receiverMap.get(receivedFromProcessId), initiate);
            if (state == State.FIND) {
                findCount++;
            }
        } else {
            if (j.getEdgeState() == Edge.EdgeState.MAYBE_IN_MST) {
                messageQueue.add(connect);
            } else {
                Initiate initiate = new Initiate(processId, receivedFromProcessId, level + 1, j.getWeight(), State.FIND);
                sender.sendMessage(REGISTRY_NAME + receivedFromProcessId, receiverMap.get(receivedFromProcessId), initiate);
            }
        }

        if (somethingChanged && !tryingQueue) {
            somethingChanged = false;
            tryQueue();
        }
    }

    public synchronized void receiveInitiate(Initiate initiate) {
        somethingChanged = true;
        level = initiate.getLevel();
        name = initiate.getName();
        state = initiate.getState();
        Edge j = findEdge(initiate.getFromProcessId());
        inBranch = j;
        bestEdge = null;
        bestWeight = Integer.MAX_VALUE;

        for (Edge e : edges) {
            if (!e.equals(j) && e.getEdgeState() == Edge.EdgeState.IN_MST) {
                int toProcessId = e.getOtherEndpoint();
                Initiate init = new Initiate(processId, toProcessId, level, name, state);
                sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), init);
                if (state == State.FIND) {
                    findCount++;
                }
            }
        }

        if (state == State.FIND) {
            test();
        }

        if (somethingChanged && !tryingQueue) {
            somethingChanged = false;
            tryQueue();
        }
    }

    public synchronized void test() {
        boolean allEdgesDecided = true;
        Edge minWeightEdge = null;
        int minWeight = Integer.MAX_VALUE;

        for (Edge e : edges) {
            if (e.getEdgeState() == Edge.EdgeState.MAYBE_IN_MST && e.getWeight() < minWeight) {
                allEdgesDecided = false;
                minWeight = e.getWeight();
                minWeightEdge = e;
            }
        }

        if (allEdgesDecided) {
            testEdge = null;
            report();
        } else {
            testEdge = minWeightEdge;
            int toProcessId = testEdge.getOtherEndpoint();
            Test test = new Test(processId, toProcessId, level, name);
            sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), test);
        }
    }

    public synchronized void receiveTest(Test test) {
        if (state == State.SLEEPING) {
            wakeUp();
        }

        if (test.getLevel() > level) {
            messageQueue.add(test);
        } else {
            int toProcessId = test.getFromProcessId();
            if (test.getName() != name) {
                Accept accept = new Accept(processId, toProcessId);
                sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), accept);
            } else {
                Edge j = findEdge(test.getFromProcessId());
                if (j.getEdgeState() == Edge.EdgeState.MAYBE_IN_MST) {
                    somethingChanged = true;
                    j.setEdgeState(Edge.EdgeState.NOT_IN_MST);
                }
                if (!j.equals(testEdge)) {
                    Reject reject = new Reject(processId, toProcessId);
                    sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), reject);
                } else {
                    test();
                }
            }
        }

        if (somethingChanged && !tryingQueue) {
            somethingChanged = false;
            tryQueue();
        }
    }

    public synchronized void receiveAccept(Accept accept) {
        testEdge = null;
        Edge j = findEdge(accept.getFromProcessId());
        if (j.getWeight() < bestWeight) {
            bestEdge = j;
            bestWeight = j.getWeight();
        }

        report();
    }

    public synchronized void receiveReject(Reject reject) {
        Edge j = findEdge(reject.getFromProcessId());
        if (j.getEdgeState() == Edge.EdgeState.MAYBE_IN_MST) {
            somethingChanged = true;
            j.setEdgeState(Edge.EdgeState.NOT_IN_MST);
        }
        test();

        if (somethingChanged && !tryingQueue) {
            somethingChanged = false;
            tryQueue();
        }
    }

    public synchronized void report() {
        if (findCount == 0 && testEdge == null) {
            somethingChanged = true;
            state = State.FOUND;
            int toProcessId = inBranch.getOtherEndpoint();
            Report report = new Report(processId, toProcessId, bestWeight);
            sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), report);
        }

        if (somethingChanged && !tryingQueue) {
            somethingChanged = false;
            tryQueue();
        }
    }

    public synchronized void receiveReport(Report report) {
        Edge j = findEdge(report.getFromProcessId());
        if (!j.equals(inBranch)) {
            findCount--;
            if (report.getWeight() < bestWeight) {
                bestWeight = report.getWeight();
                bestEdge = j;
            }

            report();
        } else {
            if (state == State.FIND) {
                messageQueue.add(report);
            } else if (report.getWeight() > bestWeight) {
                changeRoot();
            } else if (report.getWeight() == Integer.MAX_VALUE) {
                // TODO: HALT HERE SOMEHOW
                System.out.println("HALT!");
                System.out.println("Core: " + name);
                System.out.println("Level: " + level);
            }
        }
    }

    public synchronized void changeRoot() {
        int toProcessId = bestEdge.getOtherEndpoint();
        if (bestEdge.getEdgeState() == Edge.EdgeState.IN_MST) {
            ChangeRoot changeRoot = new ChangeRoot(processId, toProcessId);
            sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), changeRoot);
        } else {
            Connect connect = new Connect(processId, toProcessId, level);
            sender.sendMessage(REGISTRY_NAME + toProcessId, receiverMap.get(toProcessId), connect);
            bestEdge.setEdgeState(Edge.EdgeState.IN_MST);
            somethingChanged = true;
        }

        if (somethingChanged && !tryingQueue) {
            somethingChanged = false;
            tryQueue();
        }
    }

    public synchronized void receiveChangeRoot(ChangeRoot changeRoot) {
        changeRoot();
    }

    private void tryQueue() {
        if (messageQueue.isEmpty()) {
            return;
        }

        tryingQueue = true;
        ArrayList<Message> copiedQueue = new ArrayList<>(messageQueue);
        messageQueue.clear();
        for (Message msg : copiedQueue) {
            switch (msg.getMsgType()) {
                case TEST:
                    Test test = (Test) msg;
                    receiveTest(test);
                    break;
                case ACCEPT:
                    Accept accept = (Accept) msg;
                    receiveAccept(accept);
                    break;
                case REJECT:
                    Reject reject = (Reject) msg;
                    receiveReject(reject);
                    break;
                case REPORT:
                    Report report = (Report) msg;
                    receiveReport(report);
                    break;
                case CONNECT:
                    Connect connect = (Connect) msg;
                    receiveConnect(connect);
                    break;
                case INITIATE:
                    Initiate initiate = (Initiate) msg;
                    receiveInitiate(initiate);
                    break;
                case CHANGEROOT:
                    ChangeRoot changeRoot = (ChangeRoot) msg;
                    receiveChangeRoot(changeRoot);
                    break;
            }
        }

        tryingQueue = false;

        if (somethingChanged) {
            tryQueue();
        }
    }

    private Edge findMOE() {
        int minWeight = Integer.MAX_VALUE;
        Edge minEdge = null;
        for (Edge e : edges) {
            if (e.getWeight() < minWeight) {
                minEdge = e;
                minWeight = e.getWeight();
            }
        }

        return minEdge;
    }

    private Edge findEdge(int otherEndpoint) {
        for (Edge e : edges) {
            if (e.getOtherEndpoint() == otherEndpoint) {
                return e;
            }
        }

        return null;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    @Override
    public String toString() {
        String res = "Node " + processId + ", level: " + level + ", name: " + name + ", state: " + state + "\n";
        res += "Edges: " + edges.toString();
        return res;
    }
}
