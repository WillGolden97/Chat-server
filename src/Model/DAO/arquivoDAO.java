/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.DAO;

import ConnectionFactory.ConnectionFactory;
import Model.bean.Arquivos;
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
public class arquivoDAO {

    public Arquivos read(String nomeHash) {

        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        Arquivos arquivo = new Arquivos();
        try {
            stmt = con.prepareStatement("SELECT * FROM `arquivos` INNER JOIN anexo on anexo.arquivo = arquivos.nomeHash WHERE nomeHash = '" + nomeHash + "'");
            rs = stmt.executeQuery();
            while (rs.next()) {
                arquivo.setNomeArquivo(rs.getString("nome"));
                arquivo.setHashArquivo(rs.getString("nomeHash"));
                arquivo.setArquivo(rs.getBytes("arquivo"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            ConnectionFactory.closeConnection(con,stmt,rs);
        }
        return arquivo;
    }

    public int checkFile(String nomeHash) {

        Connection con = ConnectionFactory.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            stmt = con.prepareStatement("SELECT COUNT(nomeHash) as checkHash FROM `arquivos` WHERE nomeHash = '" + nomeHash + "'");
            rs = stmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt("checkHash");
            }
        } catch (SQLException ex) {
            Logger.getLogger(contactsListDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(con,stmt,rs);
        }
        return count;
    }
}
