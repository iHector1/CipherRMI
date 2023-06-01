import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args){
        String ip = JOptionPane.showInputDialog("Inserte la ip del servidor");
        String portString = JOptionPane.showInputDialog("Inserte el puerto");
        int port = Integer.parseInt(portString);
        try{
            System.setProperty("java.rmi.servre,hostname",ip);
            Registry rmi = LocateRegistry.createRegistry(port);
            rmi.rebind("cipher",new Implementation());
            System.out.println("Servidor activo" +
                    "\nIP: "+ip+":"+portString);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}
