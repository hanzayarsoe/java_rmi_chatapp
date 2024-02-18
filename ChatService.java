import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {
    void sendMessage(String username, String message) throws RemoteException;
    String[] getMessages() throws RemoteException;
}
