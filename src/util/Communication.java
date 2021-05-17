/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Communication implements Serializable {

    private String operation;

    Map<String, Object> params;

    public Communication(String operacao) {
        this.operation = operacao;
        params = new HashMap<>();
    }

    public Communication() {
        params = new HashMap<>();
    }

    public String getOperation() {
        return operation;
    }

    public void setParam(String chave, Object valor) {
        params.put(chave, valor);
    }

    public Object getParam(String chave) {
        return params.get(chave);
    }
    
}
