import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class ChatServer extends UnicastRemoteObject implements ChatService {
    private List<String> messages;

    protected ChatServer() throws RemoteException {
        super();
        messages = new ArrayList<>();
    }

    @Override
    public synchronized void sendMessage(String username, String message) {
        messages.add(username + ": " + message);
    }

    @Override
    public synchronized String[] getMessages() {
        return messages.toArray(new String[0]);
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("ChatServer", server);
            System.out.println("Chat server is running...");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
