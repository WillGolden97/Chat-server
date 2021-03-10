/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import ConnectionFactory.ServerS;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import util.Messages;
import util.Status;

/**
 *
 * @author elder
 */
class TrataConexao implements Runnable{

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    Socket socket;
    ServerS server;
    
    public TrataConexao(Socket socket, ServerS server)
    {
        this.socket = socket;
        this.server = server;
    }
    private void trataConexao(Socket socket) throws IOException, ClassNotFoundException {
        // * Cliente ------SOCKET-----servidor
        //protocolo da aplicação
        /*
         4 - Tratar a conversação entre cliente e 
         servidor (tratar protocolo);
         */

        try {
            /* 3 - Criar streams de entrada e saída;*/

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            /*protocolo
             HELLO
             nome : String
             sobrenome: String
            
             HELLOREPLY
             OK, ERRO, PARAMERROR
             mensagem : String
            
             */
            /*4 - Tratar a conversação entre cliente e 
             servidor (tratar protocolo);*/
            System.out.println("Tratando...");
            Estados estado = Estados.CONECTADO;
            while (estado != Estados.SAIR) {

                Messages m = (Messages) input.readObject();
                
                server.conta(socket.getInetAddress()+"");
                
                //System.out.println("Mensagem do cliente:\n" + m);
              

                String operacao = m.getOperation();
                Messages reply = new Messages(operacao + "REPLY");
                //estados conectado autenticado
                switch (estado) {
                    case CONECTADO:
                        switch (operacao) {
                            case "LOGIN":
                                try {
                                    String user = (String) m.getParam("user");
                                    String pass = (String) m.getParam("pass");

                                    if (user.equals("ALUNO") && pass.equals("ESTUDIOSO")) {
                                        reply.setStatus(Status.OK);
                                        estado = Estados.AUTENTICADO;
                                    } else {
                                        reply.setStatus(Status.ERROR);
                                    }

                                } catch (Exception e) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro nos parâmetros do protocolo.");
                                }
                                break;
                            case "HELLO":
                                String nome = (String) m.getParam("nome");
                                String sobrenome = (String) m.getParam("sobrenome");

                                reply = new Messages("HELLOREPLY");

                                if (nome == null || sobrenome == null) {
                                    reply.setStatus(Status.PARAMERROR);
                                } else {
                                    reply.setStatus(Status.OK);
                                    reply.setParam("mensagem", "Hello World, " + nome + " " + sobrenome);

                                }
                                break;
                            case "SAIR":
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                //responder mensagem de erro: Não autorizado/ou inválida
                                reply.setStatus(Status.NOTAUTHORIZED);
                                reply.setParam("msg", "MENSAGEM NÃO AUTORIZADA OU INVÁLIDA!");

                                break;
                        }
                        break;
                    case AUTENTICADO:
                        switch (operacao) {
                            case "DIV":
                                try {

                                    Integer op1 = (Integer) m.getParam("op1");
                                    Integer op2 = (Integer) m.getParam("op2");
                                    //testar os dados
                                    reply = new Messages("DIVREPLY");
                                    if (op2 == 0) {
                                        reply.setStatus(Status.DIVZERO);
                                    } else {
                                        reply.setStatus(Status.OK);
                                       // System.out.println("Op1: " + op1 + " Op2: " + op2);
                                        float div = (float) op1 / op2;
                                        reply.setParam("res", div);
                                    }
                                } catch (Exception e) {
                                    reply = new Messages("DIVREPLY");
                                    reply.setStatus(Status.PARAMERROR);
                                }
                                break;
                            case "SOMA":
                                try {
                                    Integer op1 = (Integer) m.getParam("op1");
                                    Integer op2 = (Integer) m.getParam("op2");

                                    reply = new Messages("SOMAREPLY");

                                    if (op1 == null || op2 == null) {
                                        reply.setStatus(Status.PARAMERROR);
                                    } else {

                                        int resultado = op1 + op2;
                                        reply.setStatus(Status.OK);
                                        reply.setParam("res", resultado);
                                    }
                                } catch (Exception e) {
                                    //construir uma mensagem de erro...
                                    reply = new Messages("SOMAREPLY");
                                    System.out.println("erro: " + e.getMessage());
                                    reply.setStatus(Status.ERROR);
                                }
                                break;
                            case "SUB":
                                try {
                                    Integer op1 = (Integer) m.getParam("op1");
                                    Integer op2 = (Integer) m.getParam("op2");

                                    reply = new Messages("SUBREPLY");

                                    if (op1 == null || op2 == null) {
                                        reply.setStatus(Status.PARAMERROR);
                                    } else {

                                        int resultado = op1 - op2;
                                        reply.setStatus(Status.OK);
                                        reply.setParam("res", resultado);
                                    }
                                } catch (Exception e) {
                                    //construir uma mensagem de erro...
                                    reply = new Messages("SUBREPLY");
                                    System.out.println("erro: " + e.getMessage());
                                    reply.setStatus(Status.ERROR);
                                }
                                break;
                            case "MUL":
                                try {
                                    Integer op1 = (Integer) m.getParam("op1");
                                    Integer op2 = (Integer) m.getParam("op2");

                                    reply = new Messages("MULREPLY");

                                    if (op1 == null || op2 == null) {
                                        reply.setStatus(Status.PARAMERROR);
                                    } else {

                                        int resultado = op1 * op2;
                                        reply.setStatus(Status.OK);
                                        reply.setParam("res", resultado);
                                    }
                                } catch (Exception e) {
                                    //construir uma mensagem de erro...
                                    reply = new Messages("MULREPLY");
                                    System.out.println("erro: " + e.getMessage());
                                    reply.setStatus(Status.ERROR);
                                }
                                break;
                            case "LOGOUT":
                                reply.setStatus(Status.OK);
                                estado = Estados.CONECTADO;
                                break;
                            case "SAIR":
                                //DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                reply.setStatus(Status.NOTAUTHORIZED);
                                reply.setParam("msg", "MENSAGEM NÃO AUTORIZADA OU INVÁLIDA!");
                                break;
                        }
                        break;
                    case SAIR: //ESTADP
                        break;

                }

                output.writeObject(reply);
                output.flush();//cambio do rádio amador
            }
            //4.2 - Fechar streams de entrada e saída
            input.close();
            output.close();
        } catch (IOException e) {
            //tratamento de falhas
            System.out.println("Problema no tratamento da conexão com o cliente: " + socket.getInetAddress());
            System.out.println("Erro: " + e.getMessage());
            throw e;
        } finally {
            //final do tratamento do protocolo
            /*4.1 - Fechar socket de comunicação entre servidor/cliente*/
            fechaSocket(socket);
        }

    }
      private void fechaSocket(Socket s) throws IOException {
        s.close();
    }
    
    @Override
    public void run()
    {
        System.out.println("Iniciando thread do cliente +" +socket.getInetAddress());
        try {
            trataConexao(socket);
        } catch (IOException ex) {
            Logger.getLogger(TrataConexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TrataConexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
