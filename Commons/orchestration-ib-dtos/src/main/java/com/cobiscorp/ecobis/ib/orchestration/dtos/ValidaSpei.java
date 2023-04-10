package com.cobiscorp.ecobis.ib.orchestration.dtos;

public class ValidaSpei {

    private boolean resultado;
    private int codigoError;
    private String descripcionError;

    public ValidaSpei() {
        this.resultado = true;
        this.codigoError = 0;
        this.descripcionError = "Procesamiento exitoso.";
    }

    public boolean isResultado() {
        return resultado;
    }

    public void setResultado(boolean resultado) {
        this.resultado = resultado;
    }

    public int getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(int codigoError) {
        this.codigoError = codigoError;
    }

    public String getDescripcionError() {
        return descripcionError;
    }

    public void setDescripcionError(String descripcionError) {
        this.descripcionError = descripcionError;
    }
}
