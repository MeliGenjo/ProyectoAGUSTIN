package com.example.agustin.clientesoap;


import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class informacion implements KvmSerializable {

    private String nombre_servicio;
    private int miTurno;
    private int turnoActual;
    private String tiempoAprox;
    private long distancia;

    /**
     * @return the nombre_servicio
     */
    public String getNombre_servicio() {
        return nombre_servicio;
    }

    /**
     * @param nombre_servicio the nombre_servicio to set
     */
    public void setNombre_servicio(String nombre_servicio) {
        this.nombre_servicio = nombre_servicio;
    }

    /**
     * @return the miTurno
     */
    public int getMiTurno() {
        return miTurno;
    }

    /**
     * @param miTurno the miTurno to set
     */
    public void setMiTurno(int miTurno) {
        this.miTurno = miTurno;
    }

    /**
     * @return the turnoActual
     */
    public int getTurnoActual() {
        return turnoActual;
    }

    /**
     * @param turnoActual the turnoActual to set
     */
    public void setTurnoActual(int turnoActual) {
        this.turnoActual = turnoActual;
    }

    /**
     * @return the tiempoAprox
     */
    public String getTiempoAprox() {
        return tiempoAprox;
    }

    /**
     * @param tiempoAprox the tiempoAprox to set
     */
    public void setTiempoAprox(String tiempoAprox) {
        this.tiempoAprox = tiempoAprox;
    }

    /**
     * @return the distancia
     */
    public double getDistancia() {
        return distancia;
    }

    /**
     * @param distancia the distancia to set
     */
    public void setDistancia(long distancia) {
        this.distancia = distancia;
    }

    @Override
    public Object getProperty(int pid) {
        // TODO Auto-generated method stub


        switch (pid) {
            case 0:
                return this.nombre_servicio;

            case 1:
                return this.miTurno;
            case 2:
                return this.turnoActual;
            case 3:
                return this.tiempoAprox;

            case 4:
                return this.distancia;



            default:
                break;

        }

        return null;
    }

    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 5;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable htable, PropertyInfo info) {
        // TODO Auto-generated method stub



        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "nombre_servicio";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "miTurno";
                break;
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "turnoActual";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "tiempoAprox";
                break;
            case 4:
                info.type = PropertyInfo.LONG_CLASS;
                info.name = "tiempoAprox";
                break;

        }

    }

    @Override
    public void setProperty(int index, Object value) {
        // TODO Auto-generated method stub

        switch (index) {
            case 0:
                this.nombre_servicio = value.toString();
                break;
            case 1:
                this.miTurno = Integer.valueOf(value.toString());
                break;
            case 2:
                this.turnoActual = Integer.valueOf(value.toString());
                break;
            case 3:
                this.tiempoAprox = value.toString();
                break;
            case 4:
                this.distancia = Integer.valueOf(value.toString());
                break;

        }

    }


}