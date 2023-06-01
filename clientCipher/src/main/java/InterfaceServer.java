import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServer extends Remote {
    void register(String name,InterfaceClient client) throws RemoteException;
    void receiveMessage(String message) throws RemoteException;
    void getNumber(int numeber) throws RemoteException;
    void sendMessage(String finalString,String name) throws RemoteException;
    void sendExecutionTime(long time) throws RemoteException;
    void startCipher(int type,String name) throws RemoteException;
    void reset()throws RemoteException;
}
