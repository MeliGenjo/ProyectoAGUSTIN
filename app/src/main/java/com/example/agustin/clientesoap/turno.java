package com.example.agustin.clientesoap;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by Agustin on 17/05/2016.
 */
public class turno implements KvmSerializable {

    private Integer id_servicio;
    private Integer id_usuario;





    /**
     * @return the id_servicio
     */
    public int getId_servicio() {
        return id_servicio;
    }

    /**
     * @param id_servicio the id_servicio to set
     */
    public void setId_servicio(int id_servicio) {
        this.id_servicio = id_servicio;
    }

    /**
     * @return the id_usuario
     */
    public int getId_usuario() {
        return id_usuario;
    }

    /**
     * @param id_usuario the id_usuario to set
     */
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }



    @Override
    public Object getProperty(int pid) {
        // TODO Auto-generated method stub
        switch (pid) {
            case 0:
                return this.id_servicio;

            case 1:
                return this.id_usuario;



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
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "id_servicio";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "id_usuario";
                break;


        }

    }

    @Override
    public void setProperty(int index, Object value) {
        // TODO Auto-generated method stub
        switch (index) {
            case 0:
                this.id_servicio = (Integer)value;
                break;
            case 1:
                this.id_usuario = (Integer)value;
                break;


        }

    }






}
