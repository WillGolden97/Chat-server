/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import ConnectionFactory.ServerS;
import Model.Dao.contatosListDAO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import util.Messages;
import util.States;

/**
 *
 * @author William
 */
public class TreatConnection implements Runnable {

    Socket socket;
    ServerS server;

    public TreatConnection(Socket socket, ServerS server) {
        this.socket = socket;
        this.server = server;
    }

    private void treatConnection(Socket socket) throws IOException, ClassNotFoundException {
        contatosListDAO cDAO = new contatosListDAO();
        try {

            ObjectOutputStream outPut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            System.out.println("Tratando...");
            States states = States.CONNECTED;

            Messages message = (Messages) input.readObject();
            String operation = message.getOperation();
            try {
                switch (states) {
                    case CONNECTED:
                        switch (operation) {
                            case "READ":
                                message.setParam("READREPLY", cDAO.read((String) message.getParam("nickName")));
                                break;
                        }
                }
            } catch (NullPointerException ex) {

            }
//            System.out.println("Mensagem recebida ...\n" + message.getParam("message"));
//            message.setParam("message", JOptionPane.showInputDialog(null, "Envie mensagem para o cliente:"));
            outPut.writeObject(message);
            outPut.flush();
            input.close();
            outPut.close();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro!");
        } finally {
            closeSocket(socket);
        }
    }

    private void closeSocket(Socket s) throws IOException {
        s.close();
        System.out.println("Encerrada conexão!");
    }

    @Override
    public void run() {
        System.out.println("Iniciando thread do cliente +" + socket.getInetAddress());
        try {
            treatConnection(socket);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(TreatConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
