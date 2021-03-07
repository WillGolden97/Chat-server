/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import util.Message;

/**
 *
 * @author William
 */
public class Server {
    private int i;
    private ServerSocket serverSocket;

    private void setServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);
    }

    private Socket waitConnection() throws IOException {   
        System.out.println("Sessão : "+i+++"\nWait connection...");
        Socket socket = serverSocket.accept();
        return socket;
    }

    private void closeSocket(Socket s) throws IOException {
        s.close();
        System.out.println("Encerrada conexão!");
    }

    private void treatConnection(Socket socket) throws IOException {
        try {
            
            ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            Message message = new Message();    
            message.setMessage((String) JOptionPane.showInputDialog(null,"Envie mensagem para o cliente:"));
            outPut.writeObject(message);
            
            outPut.flush();
            
            String msg = input.readUTF();
            System.out.println("Mensagem recebida ...");
            System.out.println(msg);
            
            input.close();
            outPut.close();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro!");
        } finally {
            closeSocket(socket);
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            System.out.println("Starting socket...\n");
            server.setServerSocket(2134);
            while (true) {
                Socket socket = server.waitConnection();
                System.out.println("Cliente conectado!");
                server.treatConnection(socket);
                System.out.println("Cliente finalizado\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
