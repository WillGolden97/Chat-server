/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import Model.bean.Message;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William
 */
public class messagesDAO {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Message> read(String nickName, String contactNickName) {

        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt;
        ResultSet rs;
        List<Message> Messages = new ArrayList<>();
        try {
            stmt = con.prepareStatement("call messagesWithAnexos('" + nickName + "','" + contactNickName + "')");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setMessage(rs.getString("messages"));
                m.setFrom(rs.getString("MsgFrom"));
                m.setDate(rs.getString("HourMsg"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setHashArquivo(rs.getString("hashArquivo"));
                Messages.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Messages;
    }

    public void create(Message m) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt;

        try {
            stmt = con.prepareStatement("INSERT into messages (messages,messages.MsgFrom,messages.MsgTo) VALUES (?,?,?)");
            stmt.setString(1, m.getMessage());
            stmt.setString(2, m.getFrom());
            stmt.setString(3, m.getTo());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
            setStatus(ex.toString());
        }

        try {
            stmt = con.prepareStatement("INSERT into arquivos (nome,nomeHash,arquivo) VALUES (?,?,?)");
            stmt.setString(1, m.getNomeArquivo());
            stmt.setString(2, m.getHashArquivo());
            stmt.setBytes(3, m.getArquivo());
            stmt.executeUpdate();
            setStatus("Mensagem enviada com sucesso mensagem e seus respectivo anexo");
        } catch (SQLException | NullPointerException ex) {
            System.out.print(ex);
        }
        try {
            stmt = con.prepareStatement("INSERT into anexo (arquivo,mensagem) VALUES (?,?)");
            stmt.setString(1, m.getHashArquivo());
            stmt.setInt(2, lastMessageId(m.getFrom()));
            stmt.executeUpdate();
            setStatus("Mensagem enviada com sucesso mensagem e seus respectivo anexado");
        } catch (SQLException | NullPointerException ex) {
            System.out.print(ex);
        }

    }

    public int lastMessageId(String nickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt;
        ResultSet rs;
        int id = 0;
        try {
            stmt = con.prepareStatement("SELECT Idmessage FROM messages WHERE Messages.MsgFrom = '" + nickName + "' ORDER BY Messages.Date DESC limit 1");
            rs = stmt.executeQuery();

            while (rs.next()) {
                id = rs.getInt("Idmessage");
            }
        } catch (SQLException ex) {
            Logger.getLogger(messagesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
}
