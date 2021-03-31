/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import Model.bean.Contact;

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
public class contactsListDAO {

    public List<Contact> read(String nickName) {

        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Contact> Contatos = new ArrayList<>();
        try {
            stmt = con.prepareStatement("call contatos('" + nickName + "')");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Contact c = new Contact();
                c.setNickName(rs.getString("nickNameContato"));
                c.setNome(rs.getString("contato"));
                c.setUltimaMsg(rs.getString("Messages"));
                c.setDate(rs.getString("Date"));
                Contatos.add(c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con,stmt,rs);
        }
        return Contatos;
    }
}
