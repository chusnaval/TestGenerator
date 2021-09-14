package org.chusnaval.etg;

import java.util.Date;


public class TestPojoModos {

    /**
     * Codigo de modo.
     */
    private String cdModo;

    /**
     * Descripcion de modo.
     */
    private String dsModo;

    /**
     * Indicador alta.
     */
    private boolean lgAlta;


    /**
     * Fecha de modificacion del registro
     */
    private Date fhModificacion;

    /**
     * Usuario que graba o modifica el registro
     */
    private String cdUsuario;

  
    /**
     * Constructor de la clase.
     */
    public TestPojoModos() {
        super();
    }

    /**
     * Devuelve el valor del atributo.
     * @return valor del atributo.
     */
    public String getCdModo() {
        return cdModo;
    }

    /**
     * Asigna el valor recogido en el par�metro al atributo.
     * @param cdModo valor del atributo.
     */
    public void setCdModo(String cdModo) {
        this.cdModo = cdModo;
    }

    /**
     * Devuelve el valor del atributo.
     * @return valor del atributo.
     */
    public String getDsModo() {
        return dsModo;
    }

    /**
     * Asigna el valor recogido en el par�metro al atributo.
     * @param dsModo valor del atributo.
     */
    public void setDsModo(String dsModo) {
        this.dsModo = dsModo;
    }

    /**
     * Devuelve el valor del atributo.
     * @return valor del atributo.
     */
    public boolean isLgAlta() {
        return lgAlta;
    }

    /**
     * Asigna el valor recogido en el par�metro al atributo.
     * @param lgAlta valor del atributo.
     */
    public void setLgAlta(boolean lgAlta) {
        this.lgAlta = lgAlta;
    }
  
    /**
     * Devuelve el valor del atributo.
     * @return valor del atributo.
     */
    public Date getFhModificacion() {
        return fhModificacion;
    }

  
    /**
     * Asigna el valor recogido en el par�metro al atributo.
     * @param fhModificacion valor del atributo.
     */
    public void setFhModificacion(Date fhModificacion) {
        this.fhModificacion = fhModificacion;
    }

    /**
     * Devuelve el valor del atributo.
     * @return valor del atributo.
     */
    public String getCdUsuario() {
        return cdUsuario;
    }

    /**
     * Asigna el valor recogido en el par�metro al atributo.
     * @param cdUsuario valor del atributo.
     */
    public void setCdUsuario(String cdUsuario) {
        this.cdUsuario = cdUsuario;
    }


}
