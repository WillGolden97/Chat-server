/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.DAO.arquivoDAO;
import Model.DAO.clientDAO;
import Model.DAO.contactsListDAO;
import Model.DAO.messagesDAO;
import Model.bean.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Communication;
import util.States;

/**
 *
 * @author William
 */
public class TreatConnection implements Runnable {

    private final Socket socket;

    public TreatConnection(Socket socket, Server server) {
        this.socket = socket;
    }

    public void treatConnection(Socket socket) throws IOException, ClassNotFoundException {

        try {

            ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Tratando...");
            States states = States.CONNECTED;

            while (states != States.EXIT) {
                Communication communication = (Communication) input.readObject();
                String operation = communication.getOperation();
                switch (states) {
                    case CONNECTED:
                        communication = executeOperation(operation, communication);
                        if (String.valueOf(communication.getParam("LOGINREPLY")).equals("OK")) {
                            states = states.AUTHENTICATED;
                        }
                        break;
                    case AUTHENTICATED:
                        System.out.println("LOGADO!");
                        communication = executeOperation(operation, communication);
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

    private Communication executeOperation(String op, Communication communication) {
        messagesDAO mDAO = new messagesDAO();
        clientDAO cliDAO = new clientDAO();
        arquivoDAO arqDAO = new arquivoDAO();
        contactsListDAO contactDAO = new contactsListDAO();
        Communication comunic;

        switch (op) {
            case "LOGIN":
                String loginReply = cliDAO.authenticated((String) communication.getParam("nickName"), (String) communication.getParam("password"));
                communication.setParam("LOGINREPLY", loginReply);
                System.out.println("login reply :" + loginReply);
                break;
            case "READ":
                contactsListDAO cDAO = new contactsListDAO();
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
            case "DELETEMESSAGE":
                mDAO.delete((int) communication.getParam("idMessage"));
                communication.setParam("STATUSMESSAGE", mDAO.getStatus());
                break;
            case "DOWNLOADFILE":
                communication.setParam("DOWNLOADFILEREPLY", arqDAO.read((String) communication.getParam("nomeHash")));
                break;
            case "CHECKFILE":
                communication.setParam("CHECKFILEREPLY", arqDAO.checkFile((String) communication.getParam("nomeHash")));
                System.out.print("count " + arqDAO.checkFile((String) communication.getParam("nomeHash")));
                break;
            case "CHECKCLIENT":
                communication.setParam("CHECKCLIENTREPLY", cliDAO.checkClient((String) communication.getParam("nickName")));
                break;
            case "CHECKCONTACT":
                communication.setParam("CHECKCONTACTREPLY", contactDAO.checkContact((String) communication.getParam("nickName"), (String) communication.getParam("contactNickName")));
                break;
            case "SEARCHCONTACT":
                communication.setParam("SEARCHCONTACTREPLY", contactDAO.search((String) communication.getParam("nickName")));
                break;
            case "CREATEACCOUNT":
                byte[] pictureC = (byte[]) communication.getParam("picture");
                String formatC = (String) communication.getParam("format");
                String nameC = (String) communication.getParam("name");
                String nickNameC = (String) communication.getParam("nickName");
                String passwordC = (String) communication.getParam("password");
                communication.setParam("CREATEACCOUNTREPLY", cliDAO.createAccount(pictureC, formatC, nameC, nickNameC, passwordC));
                break;
            case "EDITACCOUNT":
                byte[] pictureE = (byte[]) communication.getParam("picture");
                String formatE = (String) communication.getParam("format");
                String nameE = (String) communication.getParam("name");
                String nickNameE = (String) communication.getParam("nickName");
                String passwordE = (String) communication.getParam("password");
                communication.setParam("EDITACCOUNTREPLY", cliDAO.editAccount(pictureE, formatE, nameE, nickNameE, passwordE));
                break;
            case "PROFILEIMAGE":
                communication.setParam("PROFILEIMAGEREPLY", cliDAO.profilePic((String) communication.getParam("nickName")));
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

    @Override
    public void run() {
        try {
            System.out.println("Iniciando thread do cliente +" + socket.getInetAddress());
            treatConnection(socket);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(TreatConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
