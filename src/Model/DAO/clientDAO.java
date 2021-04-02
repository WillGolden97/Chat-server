/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import Model.bean.ProfilePic;
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
        PreparedStatement stmt = null;
        ResultSet rs = null;
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
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return reply;
    }

    public int checkClient(String nickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs;
        int count = 1;
        try {
            stmt = con.prepareStatement("SELECT COUNT(clientes.nickName) AS checkNickName  FROM clientes WHERE clientes.nickName LIKE '" + nickName + "'");
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

    public String createAccount(byte[] picture, String format, String name, String nickName, String password) {
        String reply;
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO clientes (clientes.nomeCliente,clientes.nickName,clientes.senha) VALUES (?,?,?)");
            stmt.setString(1, name);
            stmt.setString(2, nickName);
            stmt.setString(3, password);
            stmt.executeUpdate();
            reply = "OK";
        } catch (NullPointerException ex) {
            reply = ex.toString();
        } catch (MySQLSyntaxErrorException ex) {
            reply = ex.toString();
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            reply = ex.toString();
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt = con.prepareStatement("INSERT INTO profilepicture (profilepicture.clienteId,profilepicture.picture,profilepicture.format) VALUES (?,?,?)");
            stmt.setString(1, nickName);
            stmt.setBytes(2, picture);
            stmt.setString(3, format);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(clientDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.print("Sem envio de imagem");
        }  finally {
            ConnectionFactory.closeConnection(con, stmt);
        }
        return reply;
    }

    public ProfilePic profilePic(String nickName) {
        Connection con = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProfilePic profilePic = new ProfilePic();
        try {
            stmt = con.prepareStatement("SELECT * FROM `profilepicture` INNER JOIN clientes on clientes.nickName = profilepicture.clienteId WHERE clientes.nickName LIKE '" + nickName + "'");
            rs = stmt.executeQuery();
            while (rs.next()) {
                profilePic.setPicture(rs.getBytes("picture"));
                profilePic.setFormat(rs.getString("format"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con, stmt, rs);
        }
        return profilePic;
    }
}
