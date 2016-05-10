package com.jordanbumbarjordanovski.tablanet;

import android.content.Context;
import android.widget.ImageView;

public class KartaObject extends ImageView {

    private Integer indexnakarta,brojnakarta;

    public KartaObject(Context context) {
        super(context);
    }

    public Integer getIndexnakarta() {
        return indexnakarta;
    }

    public void setIndexnakarta(Integer indexnakarta1) {
        indexnakarta = indexnakarta1;
    }

    public Integer getBrojnakarta() {
        return brojnakarta;
    }

    public void setBrojnakarta(Integer brojnakarta1) {
        brojnakarta = brojnakarta1;
    }
}
