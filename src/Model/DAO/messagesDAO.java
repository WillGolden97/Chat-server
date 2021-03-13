/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import Model.bean.Message;
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
    
    public List<Message> read(String nickName,String contactNickName) {
        
        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt;
        ResultSet rs;
        List<Message> Messages = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT *, DATE_FORMAT(messages.date, '%H:%i') as HourMsg From messages WHERE messages.MsgFrom = '"+contactNickName+"' AND Messages.MsgTo = '"+nickName+"' OR messages.MsgFrom = '"+nickName+"' AND Messages.MsgTo = '"+contactNickName+"' ORDER BY DATE_FORMAT(messages.date, '%d/%m/%Y %H:%i:%s') ASC ");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setMessage(rs.getString("messages"));
                m.setFrom(rs.getString("MsgFrom"));
                m.setDate(rs.getString("HourMsg"));
//                System.out.println("From : "+rs.getString("MsgFrom")+ ", Message : \n"+rs.getString("messages"));                
                Messages.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Messages;
    }
    
    
    
    public void create (Message m) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement("INSERT into messages (messages,messages.MsgFrom,messages.MsgTo) VALUES (?,?,?)");
            stmt.setString(1, m.getMessage());
            stmt.setString(2, m.getFrom());
            stmt.setString(3, m.getTo());            
            stmt.executeUpdate(); 
            setStatus("Enviada com sucesso!");
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
            setStatus(ex.toString());            
        }
    }    
}
