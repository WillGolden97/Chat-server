/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.DAO.clientDAO;
import Model.DAO.contactsListDAO;
import Model.DAO.messagesDAO;
import Model.bean.Message;
import com.formdev.flatlaf.FlatDarkLaf;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import util.Communication;
import util.States;

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
        System.out.println("Sessão : " + i++ + "\nWait connection...");
        Socket socket = serverSocket.accept();
        return socket;
    }

    Socket socket;
    Server server;

    public void TreatConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    private void treatConnection(Socket socket) throws IOException, ClassNotFoundException {

        try {
            contactsListDAO cDAO = new contactsListDAO();
            messagesDAO mDAO = new messagesDAO();
            clientDAO cliDAO = new clientDAO();
            
            ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Tratando...");
            States states = States.CONNECTED;

            while (states != States.EXIT) {
                Communication communication = (Communication) input.readObject();
                String operation = communication.getOperation();
                switch (states) {
                    case CONNECTED:
                        communication = executeOperation(operation, communication, cDAO, mDAO, cliDAO);
                        if (String.valueOf(communication.getParam("LOGINREPLY")).equals("OK")) {
                            states = states.AUTHENTICATED;
                        }
                        break;
                    case AUTHENTICATED:
                        System.out.println("LOGADO!");
                        communication = executeOperation(operation, communication, cDAO, mDAO, cliDAO);
                        break;
                    default:
                        break;
                }
                outPut.writeObject(communication);
                outPut.flush();
            }
            input.close();
            outPut.close();
        } catch (IOException ex) {
            System.out.println("Problema no tratamento da conexão com o cliente: " + socket.getInetAddress());
            System.out.println("Erro: " + ex.getMessage());
        } finally {
            closeSocket(socket);
        }
    }

    private Communication executeOperation(String op, Communication communication, contactsListDAO cDAO, messagesDAO mDAO, clientDAO cliDAO) {
        Communication comunic;
        switch (op) {
            case "LOGIN":
                String loginReply = cliDAO.authenticated((String) communication.getParam("nickName"), (String) communication.getParam("password"));
                communication.setParam("LOGINREPLY", loginReply);
                System.out.println("login reply :" + loginReply);
                break;
            case "READ":
                communication.setParam("READREPLY", cDAO.read((String) communication.getParam("nickName")));
                break;
            case "MESSAGE":
                List<Message> message = mDAO.read((String) communication.getParam("nickName"), (String) communication.getParam("contactNickName"));
                communication.setParam("MESSAGEREPLY", message);
                break;
            case "CREATEMESSAGE":
                mDAO.create((Message) communication.getParam("SENDEDMESSAGE"));
                communication.setParam("STATUSMESSAGE", mDAO.getStatus());
                break;
            default:
                break;
        }
        comunic = communication;
        return comunic;
    }

    private void closeSocket(Socket s) throws IOException {
        s.close();
        System.out.println("Encerrada conexão!");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
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
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
