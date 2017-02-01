package com.example.agustin.clientesoap;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Agustin on 25/05/2016.
 */
public class usuario implements KvmSerializable {
    private String email;
    private String contraseña;


    public usuario(){}

    public usuario(String email, String contraseña){
        this.email=email;
        this.contraseña=contraseña;
    }
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the contraseña
     */
    public String getContraseña() {
        return contraseña;
    }

    /**
     * @param contraseña the contraseña to set
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public Object getProperty(int pid) {
        // TODO Auto-generated method stub
        switch (pid) {
            case 0:
                return this.email;

            case 1:
                return this.contraseña;



            default:
                break;

        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable htable, PropertyInfo info) {
        // TODO Auto-generated method stub

        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "email";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "contraseña";
                break;


        }

    }

    @Override
    public void setProperty(int index, Object value) {
        // TODO Auto-generated method stub
        switch (index) {
            case 0:
                this.email = value.toString();
                break;
            case 1:
                this.contraseña = value.toString();
                break;


        }

    }




}
