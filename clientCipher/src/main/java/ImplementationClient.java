import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ImplementationClient  extends UnicastRemoteObject implements InterfaceClient {
    public final InterfaceServer server;
    public final String name;
    public String text;
    public long time;

    public ImplementationClient(String name,InterfaceServer server) throws RemoteException{
        this.server = server;
        this.name = name;
        server.register(name,this);
    }

    @Override
    public void clientMessage(String message) throws RemoteException {
            text = message;
            System.out.println("text received");
    }

    @Override
    public void setExecutionTime(long time) throws RemoteException {
        this.time=time;
        System.out.println(time + " ms");
    }
}
