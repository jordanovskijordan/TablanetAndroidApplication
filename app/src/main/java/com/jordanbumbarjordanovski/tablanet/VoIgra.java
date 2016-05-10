package com.jordanbumbarjordanovski.tablanet;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class VoIgra extends Activity {
    // <editor-fold desc="Properties">
    private InputOutputObject inputoutput;
    private boolean issetinputoutput = false;
    private Handler handler;
    private int kojepored;//koj e po red igracot prv ili vtor
    private Drawable spilpozadina;
    private KonekcijaSoServer konekcijasoserver;


    private LinearLayout layoutprotivnikkarti, layoutkartimasa1,
            layoutkartimasa2, layoutjaskarti;

    private TextView rezultatjas, rezultatprotivnik;

    private LinearLayout.LayoutParams paramsmasa, params1masa, paramsigraci, params1igraci;
    private int paddingmasapx, paddingigracipx, marginmasapx, marginigracipx;

    private ArrayList<Integer[]> selektiranikarti;
    private int[] bodovinakarti;
//</editor-fold>

    public void handlerporaka(Object message, int operacija) {
        Message handlerporaka = Message.obtain();
        handlerporaka.arg1 = operacija;
        handlerporaka.obj = message;
        handler.sendMessage(handlerporaka);
    }

    //get set funkcii
    public synchronized void setInputoutput(InputOutputObject message1) {
        while (getIssetinputoutput())
            try {
                wait();
            } catch (InterruptedException e) {

            }
        inputoutput = message1;
        setIssetinputoutput(true);
        notify();
    }

    public synchronized InputOutputObject getInputoutput() {
        while (!getIssetinputoutput())
            try {
                wait();
            } catch (InterruptedException e) {

            }
        setIssetinputoutput(false);
        notify();
        return inputoutput;
    }

    public void setIssetinputoutput(boolean issetmessage1) {
        issetinputoutput = issetmessage1;
    }

    public boolean getIssetinputoutput() {
        return issetinputoutput;
    }

    public void setKojepored(int kojepored1) {
        kojepored = kojepored1;
    }

    public int getKojepored() {
        return kojepored;
    }


    //posle zavrsuvanjeto se resetira se
    public void reset() {
        setIssetinputoutput(false);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public Drawable getDrawable(String stringid) {

        return ContextCompat.getDrawable(getApplicationContext(), getResources().getIdentifier(stringid, "drawable", "com.jordanbumbarjordanovski.tablanet"));
    }


    public void dodadiKarta(LinearLayout layout, String stringiddrawable, Drawable drawable, int broj) {

        KartaObject karta = new KartaObject(getApplicationContext());
        karta.setBackgroundColor(Color.TRANSPARENT);
        karta.setIndexnakarta(broj);
        int brojnakarta;
        if (broj % 4 == 0) {
            brojnakarta = broj / 4;
        } else {
            brojnakarta = broj / 4 + 1;
        }
        if (broj >= 41 && broj <= 52) {
            brojnakarta += 1;
        }
        karta.setBrojnakarta(brojnakarta);
        if (drawable == null) {
            karta.setImageDrawable(getDrawable(stringiddrawable));
        } else {
            karta.setImageDrawable(drawable);
        }
        karta.setScaleType(ImageView.ScaleType.FIT_XY);

        if (layout.getChildCount() + 1 > 4) {
            layout.setWeightSum(layout.getWeightSum() + 1);
        } else layout.setWeightSum(4);

        if (layout == layoutkartimasa1 || layout == layoutkartimasa2) {
            if (layout.getChildCount() == 0) {
                karta.setLayoutParams(paramsmasa);
            } else {
                karta.setLayoutParams(params1masa);
            }
            karta.setPadding(paddingmasapx, paddingmasapx, paddingmasapx, paddingmasapx);

            karta.setOnClickListener(kliknakartinamasa);
        } else {
            if (layout.getChildCount() == 0) {
                karta.setLayoutParams(paramsigraci);
            } else {
                karta.setLayoutParams(params1igraci);
            }
            karta.setPadding(paddingigracipx, paddingigracipx, paddingigracipx, paddingigracipx);
            if (layout == layoutjaskarti) {
                karta.setOnClickListener(kliknakartijas);
            }
        }
        //karta.setBodovi(bodovi);
        layout.addView(karta);
    }


    public void initialUI() {

        //funkcijata novo delenje gi postavuva moite karti i kartite na protivnikot
        novodelenje(inputoutput.getKartivoraka());

        //KARTI NA MASA
        /*kartite na masata gi zemame od inputoutput objektot kako ArrayList
          od niv polovinata ke se rasporedat vo layoutkartimasa1 a drugite
          vo layoutkartimasa2
        */
        ArrayList<Integer> kartinamasa = inputoutput.getKartinamasa();
        //na pocetokot se 4 karti na masa
        for (int i = 0; i < 4; i++) {
            //imeto na fajlot na slikata za dadenata karta ja pravime
            //taka sto na "spil" ja dodavame brojkata na i-tata karta
            //do koja brojka pristapuvame so [0]
            //stringiddrawables[i] = "spil"+kartinamasa.get(i)[0];
            dodadiKarta(layoutkartimasa1, "spil" + kartinamasa.get(i), null, kartinamasa.get(i));
        }
        //kartite ke se postavat samo vo gorniot layout od kartite na masa
    }


    public void novodelenje(ArrayList<Integer> kartijas) {
        for (int i = 0; i < 6; i++) {
            dodadiKarta(layoutprotivnikkarti, null, spilpozadina, -1);
            dodadiKarta(layoutjaskarti, "spil" + kartijas.get(i), null, kartijas.get(i));
        }
    }


    //ovaa funkcija se povikuva za da izvrsi poteg
    public void poteg(int kojigrac, int kartaindex) {
        KartaObject karta;
        if (kojigrac == 1) {
            karta = (KartaObject) layoutjaskarti.getChildAt(kartaindex);
            if (kartaindex == 0 && layoutjaskarti.getChildCount() >= 2) {
                layoutjaskarti.getChildAt(1).setLayoutParams(paramsigraci);
            }
            for (int i = 0; i < layoutjaskarti.getChildCount(); i++) {
                layoutjaskarti.getChildAt(i).setEnabled(true);
            }
        } else {
            if (kartaindex == 0 && layoutprotivnikkarti.getChildCount() >= 2) {
                layoutprotivnikkarti.getChildAt(1).setLayoutParams(paramsigraci);
            }
            karta = (KartaObject) layoutprotivnikkarti.getChildAt(kartaindex);
            karta.setImageDrawable(getDrawable("spil" + inputoutput.getFrlenakarta()));
            int indexkarta = inputoutput.getFrlenakarta();
            karta.setIndexnakarta(indexkarta);
            int brojnakarta;
            if (indexkarta % 4 == 0) {
                brojnakarta = indexkarta / 4;
            } else {
                brojnakarta = indexkarta / 4 + 1;
            }
            if (indexkarta >= 41 && indexkarta <= 52) {
                brojnakarta += 1;
            }
            karta.setBrojnakarta(brojnakarta);
        }

        if (inputoutput.getSobranikarti().size() > 0) {
            sobiranjekartiodmasa();

            LinearLayout parent = (LinearLayout) karta.getParent();
            if (parent.getChildCount() <= 4) {
                parent.setWeightSum(4);
            } else parent.setWeightSum(parent.getWeightSum() - 1);
            parent.removeView(karta);

        } else {
            //ako nema sobrani karti znaci protivnikot frlil karta na masata
            frlanjekartanamasa(karta);
        }


        //go postavuvame rezultatot
        if (kojigrac == 1) {
            postavirezultat(rezultatjas, inputoutput.getZbirnapoenijas(), inputoutput.getBrojnapisankijas());

        } else {
            postavirezultat(rezultatprotivnik, inputoutput.getZbirnapoeniprotivnik(), inputoutput.getBrojnapisankiprotivnik());
        }
        for (int i = 0; i < layoutkartimasa1.getChildCount(); i++) {
            layoutkartimasa1.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }
        for (int i = 0; i < layoutkartimasa2.getChildCount(); i++) {
            layoutkartimasa2.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    //ovaa funkcija se povikuva koga se sobiraat karti od masata
    public void sobiranjekartiodmasa() {
        ArrayList<Integer[]> sobranikarti = inputoutput.getSobranikarti();
        int indexpromena = 0;//ako se odzema kartata sto e na pocetok na layout
        //togas taa e so index 0 i soodvetno site karti se pomestuvaat na levo
        //pa zatoa index 1 stanuva 0
        int layermasa1indexpromena, layermasa2indexpromena;
        layermasa1indexpromena = layermasa2indexpromena = 0;
        ArrayList<KartaObject> masa1 = new ArrayList<>();
        ArrayList<KartaObject> masa2 = new ArrayList<>();
        for (int i = 0; i < sobranikarti.size(); i++) {

            if (sobranikarti.get(i)[0] == 0) {
                //so get(i) go zemame i-tiot element od array listata a so vo zagradi [0]
                //imame zacuvano koj layer ni e toa ako get(i)[0] == 0 togas znaci deka
                //kartata sto se sobira e vo layoutkartimasa1 a dokolku e 1 togas e vo layoutkartimasa2

                KartaObject karta = (KartaObject) layoutkartimasa1.getChildAt(sobranikarti.get(i)[1]);
                masa1.add(karta);
                /*layoutkartimasa1.removeViewAt(sobranikarti.get(i)[1] - layermasa1indexpromena);
                layermasa1indexpromena++;*/
            } else {
                KartaObject karta = (KartaObject) layoutkartimasa2.getChildAt(sobranikarti.get(i)[1]);
                masa2.add(karta);
                /*layoutkartimasa2.removeViewAt(sobranikarti.get(i)[1] - layermasa2indexpromena);
                layermasa2indexpromena++;*/
            }

        }
        for (int i = 0; i < masa1.size(); i++) {
            layoutkartimasa1.removeView(masa1.get(i));
        }
        for (int i = 0; i < masa2.size(); i++) {
            layoutkartimasa2.removeView(masa2.get(i));
        }
    }

    //ovaa funkcija ke se povika koga ke se frla karta na masata
    public void frlanjekartanamasa(KartaObject karta) {
        LinearLayout parent = (LinearLayout) karta.getParent();
        parent.removeView(karta);
        if (parent.getChildCount() <= 4) {
            parent.setWeightSum(4);
        } else parent.setWeightSum(parent.getWeightSum() - 1);

        try {
            if (layoutkartimasa1.getChildCount() < 4 || layoutkartimasa1.getChildCount() == layoutkartimasa2.getChildCount()) {
                //ako vo prviot layer ima pomalku od 4 karti ili ako imaat isto karti so vtoriot layer
                if (layoutkartimasa1.getChildCount() < 4) {
                    karta.setLayoutParams(paramsmasa);
                    layoutkartimasa1.setWeightSum(4);
                } else {
                    karta.setLayoutParams(params1masa);
                    layoutkartimasa1.setWeightSum(layoutkartimasa1.getChildCount() + 1);
                }
                karta.setPadding(paddingmasapx, paddingmasapx, paddingmasapx, paddingmasapx);
                karta.setOnClickListener(kliknakartinamasa);
                karta.setVisibility(View.INVISIBLE);
                layoutkartimasa1.addView(karta);
                handlerporaka(karta, 6);
                karta.setVisibility(View.VISIBLE);
            } else {
                if (layoutkartimasa2.getChildCount() < 4) {
                    karta.setLayoutParams(paramsmasa);
                    layoutkartimasa2.setWeightSum(4);
                } else {
                    karta.setLayoutParams(params1masa);
                    layoutkartimasa2.setWeightSum(layoutkartimasa2.getChildCount() + 1);
                }
                karta.setPadding(paddingmasapx, paddingmasapx, paddingmasapx, paddingmasapx);
                karta.setOnClickListener(kliknakartinamasa);
                handlerporaka(karta, 6);
                layoutkartimasa2.addView(karta);
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    //ovaa funkcija se povikuva da go postavi rezultatot
    public void postavirezultat(TextView rezultat, int bodovi, int pisanki) {
        if (rezultat.getId() == R.id.rezultatjas) {
            String rez = "Jas :\n" + bodovi + " (" + pisanki + ")";
            rezultat.setText(rez);
        } else {
            String rez = "Protivnik :\n" + bodovi + " (" + pisanki + ")";
            rezultat.setText(rez);
        }

    }


    // <editor-fold desc="Klik na karti jas">

    View.OnClickListener kliknakartijas = new View.OnClickListener() {
        public void onClick(View v) {
            KartaObject frlenakarta = (KartaObject) v;
            //postavuvanje na vrednostite na inputoutput
            inputoutput.setFrlenakarta(frlenakarta.getIndexnakarta());
            inputoutput.setSobranikarti(new ArrayList<Integer[]>());
            int zbirnapoeni = inputoutput.getZbirnapoenijas();
            if (selektiranikarti.size() > 0) {
                //kartite se boduvaat dokolku ima zemanje od masata sto znaci deka selektiranite
                //karti treba da ima selektirani karti
                zbirnapoeni += bodovinakarti[frlenakarta.getIndexnakarta()];
            }
            for (int i = 0; i < selektiranikarti.size(); i++) {
                Integer[] sobranakarta = new Integer[2];
                sobranakarta[0] = selektiranikarti.get(i)[0];
                sobranakarta[1] = selektiranikarti.get(i)[1];
                zbirnapoeni += bodovinakarti[selektiranikarti.get(i)[2]];
                inputoutput.getSobranikarti().add(sobranakarta);
                inputoutput.getKartinamasa().remove(selektiranikarti.get(i)[2]);
            }
            inputoutput.getKartivoraka().remove(frlenakarta.getIndexnakarta());
            inputoutput.setZbirnapoenijas(zbirnapoeni);
            if (selektiranikarti.size() == layoutkartimasa1.getChildCount() + layoutkartimasa2.getChildCount()) {
                //dokolku sme sobrale se od masata
                inputoutput.setBrojnapisankijas(inputoutput.getBrojnapisankijas() + 1);
            }

            poteg(1, ((LinearLayout) v.getParent()).indexOfChild(v));

            setInputoutput(inputoutput);

            //koga se klikne na moite karti toa znaci deka posle ovaa instrukcija
            //potegot ke se isprakja na drugiot klient i treba da cekame dodeka toj ne vrati
            //pa zatoa mu pravime disable na celiot ekran
            for(int i=0;i<layoutjaskarti.getChildCount();i++){
                layoutjaskarti.getChildAt(i).setEnabled(false);
                layoutjaskarti.getChildAt(i).setY(0);
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);//disable na cel ekran
            selektiranikarti = null;
            selektiranikarti = new ArrayList<>();
        }
    };

    // </editor-fold>

    // <editor-fold desc="Klik na karti na masa">

    View.OnClickListener kliknakartinamasa = new View.OnClickListener() {
        public void onClick(View v) {
            //koga se klikne kartite na masa
            //prvo ja selektirame ili deselektirame kartata
            KartaObject kliknatakarta = (KartaObject) v;
            if (selektiranikarti.size() == 0) {
                Integer[] kartainfo = new Integer[5];
                if (kliknatakarta.getParent() == layoutkartimasa1) {
                    kartainfo[0] = 0;
                } else {
                    kartainfo[0] = 1;
                }
                kartainfo[1] = ((LinearLayout) v.getParent()).indexOfChild(v);//index na kartata vo layoutot
                kartainfo[2] = kliknatakarta.getIndexnakarta();//index na kartata vo spilot od 1 do 52
                kartainfo[3] = kliknatakarta.getBrojnakarta();//brojkata na kartata 1,2,3,4...13,14
                selektiranikarti.add(kartainfo);
                kliknatakarta.setBackgroundResource(R.drawable.border);
            } else {
                boolean selektiranaistata = false;
                int i = 0;
                for (; i < selektiranikarti.size(); i++) {
                    if (selektiranikarti.get(i)[2].equals(kliknatakarta.getIndexnakarta())) {
                        selektiranaistata = true;
                        break;
                    }
                }
                if (selektiranaistata) {
                    selektiranikarti.remove(i);
                    kliknatakarta.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    Integer[] kartainfo = new Integer[5];
                    if (kliknatakarta.getParent() == layoutkartimasa1) {
                        kartainfo[0] = 0;
                    } else {
                        kartainfo[0] = 1;
                    }
                    kartainfo[1] = ((LinearLayout) v.getParent()).indexOfChild(v);
                    kartainfo[2] = kliknatakarta.getIndexnakarta();
                    kartainfo[3] = kliknatakarta.getBrojnakarta();
                    selektiranikarti.add(kartainfo);
                    kliknatakarta.setBackgroundResource(R.drawable.border);
                }
            }
            if (selektiranikarti.size() == 0) {
                //dokolku nema selektirano ni edna karta
                for (int i = 0; i < layoutjaskarti.getChildCount(); i++) {
                    KartaObject kartaObject = (KartaObject) layoutjaskarti.getChildAt(i);
                    kartaObject.setY(0);
                    kartaObject.setEnabled(true);
                }
                return;
            }
            //potoa se sumiraat vrednostite na masata i soodvetno ovaa funkcija pravi
            //enable disable na moite karti
            ArrayList<Integer> selektiranikarti1 = new ArrayList<>();
            for (int i = 0; i < selektiranikarti.size(); i++) {
                selektiranikarti1.add(i);
            }
            for (int i = 0; i < layoutjaskarti.getChildCount(); i++) {
                KartaObject kartaObject = (KartaObject) layoutjaskarti.getChildAt(i);


                if (kombinaciirecursion(kartaObject, 0, new ArrayList<Integer>(), selektiranikarti1, new ArrayList<Integer>(), new ArrayList<Integer[]>())) {
                    kartaObject.setY(0);
                    kartaObject.setEnabled(true);
                } else {
                    kartaObject.setY(kartaObject.getHeight() / 2);
                    kartaObject.setEnabled(false);
                }
            }
        }
    };
    // </editor-fold>


    public ArrayList<Integer> najdipreostanati(ArrayList<Integer> eliminirani) {
        ArrayList<Integer> ostanato = new ArrayList<>();
        for (int i = 0; i < selektiranikarti.size(); i++) {
            int j = 0;
            for (; j < eliminirani.size(); j++) {
                if (eliminirani.get(j).equals(selektiranikarti.get(i)[2])) {
                    break;
                }
            }
            if (j == eliminirani.size()) {
                ostanato.add(i);
            }
        }
        return ostanato;
    }

    public boolean proverkakombinacija(ArrayList<Integer[]> losikombinacii, ArrayList<Integer> tempkarti, boolean dodadi) {
        Integer[] tempkarti1 = new Integer[tempkarti.size()];
        for (int i = 0; i < tempkarti.size(); i++) {
            tempkarti1[i] = selektiranikarti.get(tempkarti.get(i))[3];
        }
        Arrays.sort(tempkarti1);
        for (int i = 0; i < losikombinacii.size(); i++) {
            if (tempkarti1.length != losikombinacii.get(i).length) {
                //dokolku se goleminite im se razlicni prodolzuvame
                continue;
            }
            Integer[] losakombinacija = losikombinacii.get(i);
            for (int j = 0; j < tempkarti1.length; j++) {
                if (!tempkarti1[j].equals(losakombinacija[j])) {
                    //dokolku se razlikuvaat vrednostite
                    break;
                }
                if (j == tempkarti1.length - 1) {
                    //dokolku sme dosle do krajot znaci site se isti
                    return true;
                }
            }
        }
        if (dodadi) {
            losikombinacii.add(tempkarti1);
        }
        return false;
    }

    public boolean kombinaciirecursion(KartaObject karta, int index, ArrayList<Integer> tempkarti, ArrayList<Integer> selektiranikarti1,
                                       ArrayList<Integer> eliminirani, ArrayList<Integer[]> losikombinacii) {
        if (index == selektiranikarti1.size()) {
            return false;
        }
        for (int i = index; i < selektiranikarti1.size(); i++) {
            tempkarti.add(selektiranikarti1.get(i));

/*            if (proverkakombinacija(losikombinacii, tempkarti,false)) {
                return false;
            }*/
            if (sumiranjeselektiranikarti(tempkarti, karta)) {
                if (eliminirani.size() + tempkarti.size() == selektiranikarti.size()) {
                    return true;
                }
                for (int j = 0; j < tempkarti.size(); j++) {
                    eliminirani.add(selektiranikarti.get(tempkarti.get(j))[2]);
                }
                if (kombinaciirecursion(karta, 0, new ArrayList<Integer>(),
                        najdipreostanati(eliminirani), eliminirani, losikombinacii)) {
                    return true;
                }
                for (int j = 0; j < tempkarti.size(); j++) {
                    eliminirani.remove(eliminirani.size() - 1);
                }
            }/* else {
                //ne odgovara kombinacijata dodadi ja vo mnozestvoto neuspesni
                proverkakombinacija(losikombinacii,tempkarti,true);
            }*/
            if (kombinaciirecursion(karta, i + 1, tempkarti, selektiranikarti1, eliminirani, losikombinacii)) {
                return true;
            }
            tempkarti.remove(tempkarti.size() - 1);
        }
        return false;
    }


    //ovaa funkcija se povikuva koga se kliknuva na kartite na masata
    public boolean sumiranjeselektiranikarti(ArrayList<Integer> selektiranikarti1, KartaObject jaskarta) {
        int brojnaedinici = 0;
        //brojot na edinici sto se pojavuvaat vo selektiranite karti
        for (int i = 0; i < selektiranikarti1.size(); i++) {
            if (selektiranikarti.get(selektiranikarti1.get(i))[3].equals(1)) {
                brojnaedinici++;
            }
        }

        int sum = 0;
        int isti = 0;
        for (int j = 0; j < selektiranikarti1.size(); j++) {
            //ovde vo ovoj for ciklus proveruvame dali od selektiranite karti ima
            //karta sto e edinica ili ednakva so tekovnata karta vo raka ili pak dvete
            //onie karti koi ne se ni edno od ovie tri se sumiraat
            Integer selektiranakarta = selektiranikarti.get(selektiranikarti1.get(j))[3];
            if (!selektiranakarta.equals(1) && !selektiranakarta.equals(jaskarta.getBrojnakarta())) {
                //ako selektiranata karta ne e mojata karta i dodatno ne e edinica
                //taa se dodava vo sumata
                sum += selektiranakarta;

                if (sum > jaskarta.getBrojnakarta() && !jaskarta.getBrojnakarta().equals(1)) {
                    //dokolku sumata ja nadmine brojkata na tekovnata karta vo raka
                    //nema sto da proveruvame ponatamu
                    //ovde ne pripagja edinicata vo gorniot if ja zabranuvame pa zatoa ne
                    //proveruvame za nejzinite slucai
                    break;
                }

            } else if (selektiranakarta.equals(jaskarta.getBrojnakarta())) {
                //ako kartatavo raka e ista so kartata na masa se zgolemuva brojacot
                //koj ni ja dava brojkata na isti karti na masata so kartata vo raka
                isti++;
            }
        }

        boolean dozvolena = false;
        if (sum <= jaskarta.getBrojnakarta() || jaskarta.getBrojnakarta().equals(1)) {
            //ovde vleguvame samo dokolku sumata e pomala ili ednakva na tekovnata karta
            //ili pak ako e edinica pak vleguvame bidejki sumata moze da bide pogolema od nea
            //no treba da pravime podolu i proverka za ona koga edinicata e 11
            if (isti == 0) {
                //ni edna karta na masata ne e ista so tekovnata vo raka
                if (brojnaedinici == 0) {
                    //nema ni edna edinica na masata
                    if (jaskarta.getBrojnakarta().equals(1) && sum == 11) {
                        //kartata vo raka e edinica i zbirot na selektiranite karti e 11
                        //ne proveruvame za 1 bidejki brojnaedinici na masata e 0
                        dozvolena = true;
                    } else if (sum == jaskarta.getBrojnakarta()) {
                        //koga ke nemame ni edinici ni isti karti znaci
                        //site karti se sumirani i nemame specijalen slucaj
                        //i kartata vo raka treba da bide ednakva na sumata od site selektirani karti na masata
                        dozvolena = true;
                    } else {
                        //inaku e greska
                        dozvolena = false;
                    }
                } else {
                    //ovde bi se nasle dokolku kartata vo raka ne e edinica a na masata
                    //imame poveke edinici sto znaci treba da go ispitame samo slucajot za edinicite
                    int sum1 = sum + brojnaedinici;
                    int sum2 = sum + brojnaedinici + 10;
                    if (jaskarta.getBrojnakarta().equals(sum1) || jaskarta.getBrojnakarta().equals(sum2)) {
                        //dokolku e ispolnet uslovot da odgovara na edna od dvete kade edinicata ja
                        //racuname kako 1 i kako 11
                        dozvolena = true;
                    } else {
                        dozvolena = false;
                    }
                }
            } else {
                //ovde vleguvame dokolku imame isti karti na masata so kartata vo raka
                //kartata vo raka moze da bide edinica ili da ne bide
                //ova go pravime zatoa sto na primer moze da se desi da imame vo raka karta
                // 10 a na masa selektirani 5,5,10,10 vo tablanet pravilo e kartite koi moze da se
                //zemaat od masa se site onie koi sobrani ja davaat samata karta kako tuka 5+5 = 10
                //i plus kartite koi se isti na primer vo raka 10 i na masa moze da se desi da imame
                //drugi 3 desetki
                //ne moze da se desi na primer 5,5,7,3,10,10 na ovoj nacin ne moze da se kombiniraat
                //kartite razlicnite karti od kartata vo raka treba da go davaat zbirot na kartata
                //znaci 5+5+7+3 a ne 5+5 7+3
                if (jaskarta.getBrojnakarta().equals(1)) {
                    //kartata vo raka e edinica
                    if (sum == 11 || sum == 0 || sum == 10) {
                        //zbirot na kartite e 11 zbirot na onie koi ne se edinica primer 5+6
                        //ili pak ako zbirot e nula site karti na masata se edinici
                        //ili pak moze da bide slucajot da imame zbir bez edinicite 10 i vo kombinacija
                        //da vleze plus edinicata
                        //primer vo raka A i na masa A,A,3,7 ili A,10
                        dozvolena = true;
                    } else {
                        //zbirot na kartite koi se razlicni od edinica ne e 11 ili
                        //kartite na masa ne se site edinici
                        dozvolena = false;
                    }

                } else {
                    //kartata vo raka ne e edinica
                    if (brojnaedinici > 0) {
                        int sum1 = sum + brojnaedinici;
                        int sum2 = sum + brojnaedinici + 10;
                        if (jaskarta.getBrojnakarta().equals(sum1) || jaskarta.getBrojnakarta().equals(sum2)) {
                            dozvolena = true;
                        } else {
                            dozvolena = false;
                        }
                    } else if (jaskarta.getBrojnakarta().equals(sum) || sum == 0) {
                        //dokolku zbirot na kartite koi se razlicni od kartata vo raka e ednakov na kartata
                        //ili ako sumata e 0 toa znaci deka site selektirani karti se kako kartata vo raka
                        dozvolena = true;
                    } else {
                        dozvolena = false;
                    }
                }
            }
        }
        return dozvolena;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vo_igra);
        inputoutput = new InputOutputObject();


        //handler za UI Thread
        // <editor-fold desc="handler za UI Thread">

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.arg1) {
                    case 0://Poraka dobiena od serverot (greska)
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 1://kraj na igrata
                        if (msg.arg2 == 1) {
                            //igrata zavrsila uspesno
                            //treba da se ispise dali pobedil ili izgubil igracot
                            //i eventualno ako sakaat pak da igraat
                        }
                        reset();
                        break;
                    case 2://prvata poraka sto se dobiva od serverot e redot na igracot
                        //dali e prv ili vtor
                        kojepored = Integer.parseInt(msg.obj.toString());
                        break;
                    case 3://protivnikot odigral i go dobivame InputOutputObject
                        //objektot so site informacii vo vrska so igrata
                        inputoutput = (InputOutputObject) msg.obj;
                        //tuka treba da se povika funkcija koja ke
                        //vrsi enable na ui i ke go postavi ui
                        //spored vrednostite vo "inputoutput"


                        //2 ni e deka se raboti za protivnikot
                        //i generirame random karta koja ke bide frlena
                        poteg(2, new Random().nextInt(layoutprotivnikkarti.getChildCount()));

                        if (layoutjaskarti.getChildCount() == 0 && kojepored == 1) {
                            //novo delenje
                            novodelenje(inputoutput.getKartivoraka());
                        }
                        //vrsime pak enable na interfejsot
                        for (int i = 0; i < layoutjaskarti.getChildCount(); i++) {
                            //layoutjaskarti.getChildAt(i).setY(0);
                            layoutjaskarti.getChildAt(i).setEnabled(true);
                        }
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        break;
                    case 4://pocetok na igrata
                        inputoutput = (InputOutputObject) msg.obj;
                        initialUI();
                        if (kojepored == 1) {
                            Toast.makeText(getApplicationContext(), "Vie ste prv na poteg", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Vie ste vtor na poteg", Toast.LENGTH_SHORT).show();
                            for(int i=0;i<layoutjaskarti.getChildCount();i++){
                                layoutjaskarti.getChildAt(i).setEnabled(false);
                            }
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);//disable na cel ekran
                        }
                        break;
                    case 5:
                        ArrayList<Integer> novikarti = (ArrayList<Integer>) msg.obj;
                        novodelenje(novikarti);
                        break;
                    case 6:
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                        alphaAnimation.setDuration(1000);
                        KartaObject kartaObject = (KartaObject) msg.obj;
                        kartaObject.startAnimation(alphaAnimation);
                        break;


                }
            }
        };
//</editor-fold>

        // <editor-fold desc="Inicijalizacija na elementite">
        layoutprotivnikkarti = (LinearLayout) findViewById(R.id.layoutprotivnikkarti);
        layoutkartimasa1 = (LinearLayout) findViewById(R.id.layoutkartimasa1);
        layoutkartimasa2 = (LinearLayout) findViewById(R.id.layoutkartimasa2);
        layoutjaskarti = (LinearLayout) findViewById(R.id.layoutjaskarti);
        rezultatjas = (TextView) findViewById(R.id.rezultatjas);
        rezultatprotivnik = (TextView) findViewById(R.id.rezultatprotivnik);

        paddingmasapx = (int) convertDpToPixel(5, getApplicationContext());
        paddingigracipx = (int) convertDpToPixel(10, getApplicationContext());
        marginmasapx = 0;
        marginigracipx = (int) convertDpToPixel(-30, getApplicationContext());
        spilpozadina = getDrawable("spilpozadina");


        paramsmasa = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params1masa = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsigraci = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params1igraci = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsmasa.weight = params1masa.weight = paramsigraci.weight = params1igraci.weight = 1;
        params1masa.setMargins(marginmasapx, 0, 0, 0);
        params1masa.setMarginStart(marginmasapx);
        params1igraci.setMargins(marginigracipx, 0, 0, 0);
        params1igraci.setMarginStart(marginigracipx);

        selektiranikarti = new ArrayList<>();
        bodovinakarti = new int[53];
        for (int i = 1; i <= 52; i++) {
            if (i <= 4 || i >= 37 || i == 6) {
                //ako e A(1) ili ako e od 10 pogolemo bez 10 baklava ili ako e
                //2 detelina sto se naogja na 6 pozicija

                bodovinakarti[i] = 1;
            } else if (i == 36) {
                // ako e 10 baklava vredi 2 boda
                bodovinakarti[i] = 2;
            } else
                bodovinakarti[i] = 0;// site drugi se 0 bodovi
        }

        Thread konekcijaThread = new Thread(new KonekcijaSoServer(VoIgra.this, handler));
        konekcijaThread.start();

        //</editor-fold>

    }

    // <editor-fold desc="Back button handler">


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

// </editor-fold>
}
