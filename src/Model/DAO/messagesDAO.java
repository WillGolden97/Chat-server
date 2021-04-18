/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import Model.bean.Message;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
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
        PreparedStatement stmt = null;
        ResultSet rs;
        List<Message> Messages = new ArrayList<>();
        try {
            stmt = con.prepareStatement("call messagesWithAttachment('" + nickName + "','" + contactNickName + "')");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setIdMessage(rs.getInt("Idmessage"));
                m.setMessage(rs.getString("messages"));
                m.setFrom(rs.getString("MsgFrom"));
                m.setDate(rs.getString("HourMsg"));
                m.setNomeArquivo(rs.getString("nomeArquivo"));
                m.setHashArquivo(rs.getString("hashArquivo"));
                Messages.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
        return Messages;
    }

    public List<Message> readNotReceived(String nickName, String contactNickName) {

        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs;
        List<Message> Messages = null;
        int id = 0;
        try {
            stmt = con.prepareStatement("SELECT COUNT(Idmessage) AS contMSg FROM messages WHERE Messages.MsgFrom = '" + contactNickName + "' AND received = 0");
            rs = stmt.executeQuery();

            while (rs.next()) {
                id = rs.getInt("contMSg");
            }
        } catch (SQLException ex) {
            Logger.getLogger(messagesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (id != 0) {
            Messages = new ArrayList<>();
            try {
                stmt = con.prepareStatement("call messagesWithAttachment('" + nickName + "','" + contactNickName + "')");
                rs = stmt.executeQuery();
                while (rs.next()) {
                    Message m = new Message();
                    m.setIdMessage(rs.getInt("Idmessage"));
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
            try {
                stmt = con.prepareStatement("UPDATE messages SET received = ? WHERE msgFrom = ?");
                stmt.setInt(1, 1);
                stmt.setString(2, contactNickName);
                stmt.executeUpdate();
            } catch (MySQLSyntaxErrorException ex) {
                Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                ConnectionFactory.closeConnection(con, stmt);
            }
        } else {
            ConnectionFactory.closeConnection(con, stmt);
        }
        return Messages;
    }

    public List<Message> readNotReceivedAllContacts(String nickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs;
        List<Message> Messages = new ArrayList<>();
        try {
            stmt = con.prepareStatement("call firstMessageWithAttachment('" + nickName + "')");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setMessage(rs.getString("messages"));
                m.setFrom(rs.getString("MsgFrom"));
                m.setTo(rs.getString("MsgTo"));
                m.setDate(rs.getString("HourMsg"));
                Messages.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt = con.prepareStatement("UPDATE messages SET received = ? WHERE msgTo = ?");
            stmt.setInt(1, 1);
            stmt.setString(2, nickName);
            stmt.executeUpdate();
        } catch (MySQLSyntaxErrorException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
        return Messages;
    }

    public void create(Message m) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("INSERT into messages (messages,messages.MsgFrom,messages.MsgTo) VALUES (?,?,?)");
            stmt.setString(1, m.getMessage());
            stmt.setString(2, m.getFrom());
            stmt.setString(3, m.getTo());
            stmt.executeUpdate();
            setStatus("Mensagem enviada");
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
            setStatus(ex.toString());
        }

        if (m.getHashArquivo() != null) {
            try {
                stmt = con.prepareStatement("INSERT into arquivos (nomeHash,arquivo) VALUES (?,?)");
                stmt.setString(1, m.getHashArquivo());
                stmt.setBytes(2, m.getArquivo());
                stmt.executeUpdate();
                setStatus("Mensagem enviada e seu arquivo anexado");
            } catch (SQLException ex) {
                System.out.print(ex);
            }
        }
        if (m.getHashArquivo() != null) {
            try {
                stmt = con.prepareStatement("INSERT into anexo (nome,arquivo,mensagem) VALUES (?,?,?)");
                stmt.setString(1, m.getNomeArquivo());
                stmt.setString(2, m.getHashArquivo());
                stmt.setInt(3, lastMessageId(m.getFrom()));
                stmt.executeUpdate();
                setStatus("Mensagem enviada e seu arquivo anexado");
            } catch (SQLException ex) {
                System.out.print(ex);
            }
        }
        try {
            stmt = con.prepareStatement("UPDATE messages SET received = ? WHERE msgFrom = ?  WHERE Idmessage = (SELECT Idmessage FROM Messages WHERE MsgTo = ? ORDER BY Idmessage DESC LIMIT 0,1)");
            stmt.setInt(1, 0);
            stmt.setString(2, m.getFrom());
            stmt.setString(3, m.getTo());
            stmt.executeUpdate();
        } catch (MySQLSyntaxErrorException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
    }

    public void delete(int id, String nickName, String contacNickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("DELETE FROM messages WHERE messages.Idmessage = '" + id + "'");
            setStatus("Deletado com sucesso!");
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
            setStatus(ex.toString());
        }
        try {
            stmt = con.prepareStatement("UPDATE messages SET received = ? WHERE msgFrom = ?  WHERE Idmessage = (SELECT Idmessage FROM Messages WHERE MsgTo = ? ORDER BY Idmessage DESC LIMIT 0,1)");
            stmt.setInt(1, 0);
            stmt.setString(2, nickName);
            stmt.setString(3,contacNickName);
            stmt.executeUpdate();
        } catch (MySQLSyntaxErrorException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
    }

    public int lastMessageId(String nickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int id = 0;
        try {
            stmt = con.prepareStatement("SELECT Idmessage FROM messages WHERE Messages.MsgFrom = '" + nickName + "' ORDER BY Messages.Date DESC limit 1");
            rs = stmt.executeQuery();

            while (rs.next()) {
                id = rs.getInt("Idmessage");
            }
        } catch (SQLException ex) {
            Logger.getLogger(messagesDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return id;
    }
}
