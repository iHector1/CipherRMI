import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class Implementation extends UnicastRemoteObject implements InterfaceServer {
    public List<InterfaceClient> clients;
    public Map<String,InterfaceClient> clientMap;
    private String string;
    private int number;
    private boolean messageFlag, numberFlag;

    public Implementation() throws RemoteException{
        messageFlag = false;
        clients = new ArrayList<>();
        clientMap = new HashMap<>();
        string="";
        numberFlag=false;
    }

    @Override
    public void register(String name, InterfaceClient client) throws RemoteException{
        this.clients.add(client);
        this.clientMap.put(name,client);
        System.out.println(name+" fue agregado\n");
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        this.string = this.string+ "\n"+message;

    }

    @Override
    public void getNumber(int numeber) throws RemoteException {
        this.number=number;
        numberFlag = true;
        System.out.println("Numero recibido");
    }


    @Override
    public void sendMessage(String finalString, String name) throws RemoteException {
        clientMap.get(name).clientMessage(finalString);
        System.out.println("Ciphered message sent\n");
    }

    @Override
    public void sendExecutionTime(long time) throws RemoteException {
        for (InterfaceClient client : clients) {
            client.setExecutionTime(time);
        }
        System.out.println("Sending time\n");
    }

    @Override
    public void startCipher(int type, String name) throws RemoteException {
        if (messageFlag && numberFlag) {
            String finalString;
            long startTime, endTime;

            switch (type) {
                case 1:
                    //Sequential
                    startTime = System.nanoTime();

                    finalString = CaesarCipher.cipher(string, number);
                    sendMessage(finalString, name);

                    endTime = System.nanoTime();
                    sendExecutionTime((endTime - startTime) / 1000000);
                break;

                case 2:
                    //Fork Join
                    startTime = System.nanoTime();

                    ForkJoinPool pool = new ForkJoinPool();
                    CaesarCipher caesarCipher = new CaesarCipher(string, number);

                    finalString = pool.invoke(caesarCipher);

                    sendMessage(finalString, name);

                    endTime = System.nanoTime();
                    sendExecutionTime((endTime - startTime) / 1000000);
                break;

                case 3:
                    //ExecutorService
                    startTime = System.nanoTime();

                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        int third = string.length() / 3;
                        ExecutorService executorService = Executors.newFixedThreadPool(10);
                        Collection<Callable<String>> callables = new ArrayList<>();

                        callables.add(() -> CaesarCipher.cipher(String.valueOf(string).substring(0, third), number));
                        callables.add(() -> CaesarCipher.cipher(String.valueOf(string).substring(third, third * 2), number));
                        callables.add(() -> CaesarCipher.cipher(String.valueOf(string).substring(third * 2), number));

                        List<Future<String>> futures = executorService.invokeAll(callables);

                        for (Future<String> future : futures) {
                            stringBuilder.append(future.get());
                        }

                        sendMessage(stringBuilder.toString(), name);

                        endTime = System.nanoTime();
                        sendExecutionTime((endTime - startTime) / 1000000);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                break;

            }
        }
    }

    @Override
    public void reset() throws RemoteException {
        this.string = "";
    }

}
