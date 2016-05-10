package com.jordanbumbarjordanovski.tablanet;

import java.io.Serializable;
import java.util.ArrayList;

public class InputOutputObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -98305956003065237L;
    /**
     *
     */
    private int brojnapisankijas, brojnapisankiprotivnik, zbirnapoenijas, zbirnapoeniprotivnik;
    private ArrayList<Integer> kartivoraka, kartinamasa;
    private ArrayList<Integer[]> sobranikarti;
    private int frlenakarta;
    private String message;

    public void setAll(int brojnapisankijas1, int brojnapisankiprotivnik1, int zbirnapoenijas1,
                       int zbirnapoeniprotivnik1, ArrayList<Integer> kartivoraka1, ArrayList<Integer> kartinamasa1,
                       int frlenakarta1, ArrayList<Integer[]> sobranikarti1, String message1) {
        brojnapisankijas = brojnapisankijas1;
        brojnapisankiprotivnik = brojnapisankiprotivnik1;
        zbirnapoenijas = zbirnapoenijas1;
        zbirnapoeniprotivnik = zbirnapoeniprotivnik1;
        kartivoraka = kartivoraka1;
        kartinamasa = kartinamasa1;
        frlenakarta = frlenakarta1;
        sobranikarti = sobranikarti1;
        message = message1;

    }


    public void setBrojnapisankijas(int brojnapisankijas1) {
        brojnapisankijas = brojnapisankijas1;
    }

    public void setBrojnapisankiprotivnik(int brojnapisankiprotivnik1) {
        brojnapisankiprotivnik = brojnapisankiprotivnik1;
    }

    public void setZbirnapoenijas(int zbirnapoenijas1) {
        zbirnapoenijas = zbirnapoenijas1;
    }

    public void setZbirnapoeniprotivnik(int zbirnapoeniprotivnik1) {
        zbirnapoeniprotivnik = zbirnapoeniprotivnik1;
    }

    public void setKartivoraka(ArrayList<Integer> kartivoraka1) {
        kartivoraka = kartivoraka1;
    }

    public void setKartinamasa(ArrayList<Integer> kartinamasa1) {
        kartinamasa = kartinamasa1;
    }

    public void setFrlenakarta(int frlenakarta1) {
        frlenakarta = frlenakarta1;
    }

    public void setSobranikarti(ArrayList<Integer[]> sobranikarti1) {
        sobranikarti = sobranikarti1;
    }

    public void setMessage(String message1) {
        message = message1;
    }

    public int getBrojnapisankijas() {
        return brojnapisankijas;
    }

    public int getBrojnapisankiprotivnik() {
        return brojnapisankiprotivnik;
    }

    public int getZbirnapoenijas() {
        return zbirnapoenijas;
    }

    public int getZbirnapoeniprotivnik() {
        return zbirnapoeniprotivnik;
    }

    public ArrayList<Integer> getKartivoraka() {
        return kartivoraka;
    }

    public ArrayList<Integer> getKartinamasa() {
        return kartinamasa;
    }

    public int getFrlenakarta() {
        return frlenakarta;
    }

    public ArrayList<Integer[]> getSobranikarti() {
        return sobranikarti;
    }

    public String getMessage() {
        return message;
    }


}
