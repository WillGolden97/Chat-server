/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.Bean;

import java.io.Serializable;

/**
 *
 * @author William
 */
public class Contato implements Serializable {
    private String nome;
    private String ultimaMsg;
    private String data;
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUltimaMsg() {
        return ultimaMsg;
    }

    public void setUltimaMsg(String ultimaMsg) {
        this.ultimaMsg = ultimaMsg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
}
