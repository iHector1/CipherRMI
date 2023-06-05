import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainClient extends JFrame {
    private JLabel execLabel;
    private JLabel forkLabel;
    private JLabel seqLabel;
    private JButton textoButton;
    private JButton numberButton;
    private JTextField numTextField;
    private JTextArea seqTextArea;
    private JTextArea forkTextArea;
    private JTextArea execTextArea;

    private static ImplementationClient client;


    public MainClient(String name) {
        // Configurar la ventana principal
        setTitle("Ventana principal de "+name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Crear el panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Crear el panel de números
        JPanel numPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel numLabel = new JLabel("Números:");
        numTextField = new JTextField(20);
        textoButton = new JButton("Abrir Archivo");
        numberButton = new JButton("Numero");
        textoButton.addActionListener(e->this.abrirArchivo());
        numberButton.addActionListener(e->sendNumber());
        numPanel.add(numLabel);
        numPanel.add(numTextField);
        numPanel.add(numberButton);
        numPanel.add(textoButton);

        // Crear el panel de secuencias
        JPanel seqPanel = new JPanel(new BorderLayout());
        seqLabel = new JLabel("Secuencial:");
        seqTextArea = new JTextArea(10, 50);
        seqTextArea.setLineWrap(true);
        seqTextArea.setWrapStyleWord(true);
        JScrollPane seqScrollPane = new JScrollPane(seqTextArea);
        JButton seqButton = new JButton("Ejecutar");
        seqButton.addActionListener(e -> this.secuencial());
        seqPanel.add(seqLabel, BorderLayout.NORTH);
        seqPanel.add(seqScrollPane, BorderLayout.CENTER);
        seqPanel.add(seqButton, BorderLayout.SOUTH);

        // Crear el panel de fork
        JPanel forkPanel = new JPanel(new BorderLayout());
        forkLabel = new JLabel("Fork:");
        forkTextArea = new JTextArea(10, 50);
        forkTextArea.setLineWrap(true);
        forkTextArea.setWrapStyleWord(true);
        JScrollPane forkScrollPane = new JScrollPane(forkTextArea);
        JButton forkButton = new JButton("Ejecutar");
        forkButton.addActionListener(e -> this.fork());
        forkPanel.add(forkLabel, BorderLayout.NORTH);
        forkPanel.add(forkScrollPane, BorderLayout.CENTER);
        forkPanel.add(forkButton, BorderLayout.SOUTH);

        // Crear el panel de executor
        JPanel execPanel = new JPanel(new BorderLayout());
        execLabel = new JLabel("Executor:");
        execTextArea = new JTextArea(10, 50);
        execTextArea.setLineWrap(true);
        execTextArea.setWrapStyleWord(true);
        JScrollPane execScrollPane = new JScrollPane(execTextArea);
        JButton execButton = new JButton("Ejecutar");
        execButton.addActionListener(e -> this.executor());
        execPanel.add(execLabel, BorderLayout.NORTH);
        execPanel.add(execScrollPane, BorderLayout.CENTER);
        execPanel.add(execButton, BorderLayout.SOUTH);

        // Crear el panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> this.limpiar());
        buttonPanel.add(clearButton);

        // Agregar los componentes al panel principal
        mainPanel.add(numPanel, BorderLayout.NORTH);
        mainPanel.add(seqPanel, BorderLayout.WEST);
        mainPanel.add(forkPanel, BorderLayout.CENTER);
        mainPanel.add(execPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Agregar el panel principal a la ventana principal
        getContentPane().add(mainPanel);
        this.setVisible(true);
    }


    public ActionListener limpiar(){
        numTextField.setText("");
        seqTextArea.setText("");
        forkTextArea.setText("");
        execTextArea.setText("");
        seqLabel.setText("Secuencial: ");
        forkLabel.setText("Fork: ");
        execLabel.setText("Executor: ");

        return null;
    }
    public void sendNumber(){
        if(this.numTextField.getText().isEmpty()){
            JOptionPane.showMessageDialog(this,"Inserta el numero de desplazamiento");
        }else if(!this.numTextField.getText().chars().allMatch(Character::isDigit)){
            JOptionPane.showMessageDialog(this,"No debe contener letras");
        }else{
            try{
                System.out.println(this.numTextField.getText());
                int number = Integer.parseInt(this.numTextField.getText());
                client.server.getNumber(number);
            }catch (RemoteException e){
                 throw new RuntimeException(e);
            }

        }
    }
    public ActionListener abrirArchivo(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            // Obtener el archivo seleccionado por el usuario
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Leer el contenido del archivo y guardarlo en una cadena
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(System.lineSeparator());
                }
                String fileContent = stringBuilder.toString();
                client.server.receiveMessage(fileContent);
                //System.out.println(fileContent);
                JOptionPane.showMessageDialog(this,"Archivo Cargado");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private void executor(){
        System.out.println("executor");
        try{
            client.server.startCipher(3, client.name);
            this.execLabel.setText("Executor: "+client.time+" milisegundos");
        }catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        execTextArea.setText(client.text);


    }

    private void secuencial(){
        System.out.println("secuencial");
        try {
            client.server.startCipher(1, client.name);
            this.seqTextArea.setText(client.text);
            this.seqLabel.setText("Secuencial: "+client.time);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }

    private void fork(){
        try {
            client.server.startCipher(2, client.name);
            this.forkLabel.setText("Fork: "+client.time+"ms");
            this.forkTextArea.setText(client.text);
        }catch (RemoteException e){
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {

        String name = JOptionPane.showInputDialog("Inserta nombre de usuario");
        String ip = JOptionPane.showInputDialog("Inserta la ip del servidor");
        String portString = JOptionPane.showInputDialog("Inserta el puerto");
        int port = Integer.parseInt(portString);
        try {
            Registry rmi = LocateRegistry.getRegistry(ip,port);
            InterfaceServer server = (InterfaceServer) rmi.lookup("cipher");
            client=new ImplementationClient(name,server);
            new MainClient (name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
