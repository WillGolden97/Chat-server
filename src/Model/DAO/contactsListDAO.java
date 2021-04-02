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
                Contatos.add(c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return Contatos;
    }

    public int checkContact(String contactNickName, String nickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs;
        int count = 1;
        try {
            stmt = con.prepareStatement("SELECT count(messages.Idmessage) as checkNickName FROM messages WHERE messages.MsgFrom = '"+nickName+"' AND messages.MsgTo = '"+contactNickName+"' OR messages.MsgFrom = '"+contactNickName+"' AND messages.MsgTo = '"+nickName+"'");
            rs = stmt.executeQuery();
            while (rs.next()) {
                    count = rs.getInt("checkNickName");
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
        return count;
    }

    public Contact search(String nickName) {

        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Contact contact = new Contact();
        try {
            stmt = con.prepareStatement("SELECT * FROM clientes WHERE clientes.nickName LIKE '" + nickName + "'");
            rs = stmt.executeQuery();
            while (rs.next()) {
                contact.setNickName(rs.getString("nickName"));
                contact.setNome(rs.getString("nomeCliente"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return contact;
    }
}
