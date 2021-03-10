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
    
    public List<Message> read(String nickName,String contactNickName) {
        
        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt;
        ResultSet rs;
        List<Message> Messages = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT *, DATE_FORMAT(messages.date, '%H:%i') as HourMsg From messages WHERE messages.MsgFrom = '"+contactNickName+"' AND Messages.MsgTo = '"+nickName+"' OR messages.MsgFrom = '"+nickName+"' AND Messages.MsgTo = '"+contactNickName+"' ORDER BY messages.DATE DESC ");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setMessage(rs.getString("messages"));
                m.setContactName(rs.getString("MsgFrom"));
                m.setDate(rs.getString("HourMsg"));
//                System.out.println("From : "+rs.getString("MsgFrom")+ ", Message : \n"+rs.getString("messages"));                
                Messages.add(m);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Messages;
    }
}
