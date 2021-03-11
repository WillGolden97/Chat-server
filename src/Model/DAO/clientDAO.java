/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William
 */
public class clientDAO {

    public String authenticated(String nickName, String password) {
        String reply = "Nickname ou senha errada!";
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt;
        ResultSet rs;
        try {
            stmt = con.prepareStatement("SELECT count(nickName) as result FROM clientes WHERE nickName='" + nickName + "' AND senha = '" + password + "'");
            rs = stmt.executeQuery();       
            while (rs.next()) {
                if (rs.getInt("result") == 1) {
                    reply = "OK";
                    System.out.println("Autenticado");
                } 
            }
        } catch (MySQLSyntaxErrorException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reply;
    }
}
