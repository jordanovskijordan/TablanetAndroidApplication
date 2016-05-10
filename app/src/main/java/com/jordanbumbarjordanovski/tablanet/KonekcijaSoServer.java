package com.jordanbumbarjordanovski.tablanet;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class KonekcijaSoServer implements Runnable {
    private VoIgra voIgra;
    private Handler handler;

    private boolean error = false;
    private Socket socket = null;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    public KonekcijaSoServer(VoIgra mainActivity1, Handler handler1) {
        voIgra = mainActivity1;
        handler = handler1;
    }


    /*isprakjame poraki do handler-ot UI Thread-ot ili do glavniot thread
     za da vrsi promena na kontrolite koi gi poseduva bidejki vo
     android ne moze drug thread da menuva nekoja kontrola bidejki ne ja kreiral
     i mora da komunicira so UI Thread-ot
     operacija = {0->poraka , 1->reset na igrata ,
     2->za setiranje koj mu e redot na dadeniot igrac
     3->znaci deka e primen objektot so site informacii
     i treba da se prosledi na UI Thread-ot za da se prikazat informaciite
     */
    public void handlerporaka(Object message, int operacija) {
        Message handlerporaka = Message.obtain();
        handlerporaka.arg1 = operacija;
        handlerporaka.obj = message;
        handler.sendMessage(handlerporaka);
    }

    public boolean procitajOdSocket(boolean novodelenje){
        try {
            //se prima potegot na protivnikot od serverot
            if (novodelenje) {
                try {
                    ArrayList<Integer> novikarti = (ArrayList<Integer>) objectInputStream.readObject();
                    handlerporaka(novikarti, 5);
                    Log.e("Novodelenje", "Novodelenje");
                } catch (ClassNotFoundException e) {

                }
                return true;
            }

            InputOutputObject inputobject = null;
            try {
                inputobject = (InputOutputObject) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                handlerporaka("Class not found exception od funkcija", 0);
                return false;
            }
            String inputmessage = inputobject.getMessage();
            //protivnikot se isklucil
            if (inputmessage.equals("Disconnected")) {
                handlerporaka("Protivnikot se iskluci!", 0);
                return false;
            } else {
                handlerporaka(inputobject, 3);
            }
        } catch (IOException e) {
            handlerporaka("Instrukcijata ne moze da se prevzeme od serverot exc", 0);
            return false;
        }
        return true;
    }

    public boolean zapisiNaSocket(){
        try {
            //se isprakja potegot na serverot
            objectOutputStream.writeObject(voIgra.getInputoutput());
        } catch (IOException e) {
            handlerporaka("Instrukcijata nemoze da se isprati na serverot", 0);
            return false;
        }
        return true;
    }

    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket();
            //ako ne moze da se konektira frla IOException
            socket.connect(new InetSocketAddress("192.168.43.154", 5222), 30000);

            //so setSoTimeout(13000) objectInputStream.read metodata ceka 13 sekundi
            //ako kako odgovor ima -1 znaci nisto ne dobila od serverot
            socket.setSoTimeout(1300000);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();


        } catch (IOException e) {
            //ovde dovagjame dokolku socket.connect ne uspee da se
            //konektira so remote hostot
            error = true;
            handlerporaka("Problem so konektiranje so serverot", 0);
        }


        if (!error) {//dokolku sme konektirani vleguvame vo ovoj blok
            byte[] bytes = new byte[5000];
            int size = 0;
            try {
                //ocekuvame povraten odgovor od serverot
                //i toa najvekje 13 sekundi

                size = inputStream.read(bytes);
                if (size == -1) {
                    error = true;
                    handlerporaka("Nema igracionline", 0);
                } else {
                    //serverot vratil deka nikoj ne se konektiral vo dadeniot interval
                    String inputmessage = new String(bytes, 0, size);
                    if (inputmessage.equals("nema igraci")) {
                        error = true;
                        handlerporaka("Nema online igraci", 0);
                    } else {
                        outputStream.write("ok".getBytes());//vrakjame potvrdna informacija do serverot
                        handlerporaka(inputmessage, 2);//2 e za setiranje koj mu e redot na dadeniot igrac
                        try {
                            objectInputStream = new ObjectInputStream(inputStream);
                            objectOutputStream = new ObjectOutputStream(outputStream);
                        } catch (EOFException e) {
                        }
                        InputOutputObject inputobject = (InputOutputObject) objectInputStream.readObject();


                        //serverot vratil odgovor
                        inputmessage = inputobject.getMessage();
                        if (inputmessage.equals("Disconnected")) {
                            //protivnikot se isklucil
                            error = true;
                            handlerporaka("Protivnikot se iskluci", 0);
                        } else {
                            //ova e pocetnata sostojba na igrata
                            handlerporaka(inputobject, 4);
                        }
                    }
                }
            } catch (IOException e) {
                //IOException
                error = true;
                handlerporaka("Izgubena konekcija so serverot", 0);

            } catch (ClassNotFoundException e) {
                error = true;
                handlerporaka("ClassNotFoundException", 0);
            }
            if (!error) {//dokolku nema error uspesno se konektirani dvete strani
                //i igrata moze da zapocne

                /*for ciklusot vrti 24 pati bidejki imame 24 potezi koj treba da
                * gi odigra korisnikot za da zavrsi igrata*/
                for (int i = 1; i <= 24; i++) {
                    if (voIgra.getKojepored() == 1) {
                        //ako e prv izbran da igra od serverot togas posle
                        //inicijalizacijata na igrata se ceka da go odigra potegot
                        if (!zapisiNaSocket()) {
                            error = true;
                            break;
                        }
                        if (!procitajOdSocket(false)) {

                            error = true;
                            break;
                        }
                    } else {
                        //ako e vtor posle inicijalizacijata ceka na odigranoto od
                        //protivnikot
                        if (!procitajOdSocket(false)) {
                            error = true;
                            break;
                        }
                        if (!zapisiNaSocket()) {
                            error = true;
                            break;
                        }
                        if (i % 6 == 0) {
                            procitajOdSocket(true);
                        }
                    }

                }
            }
            try {
                socket.close();//se zatvora socketot
            } catch (IOException e) {
                handlerporaka("Ne moze da se zatvori socket-ot", 0);
            }
        }
        //se isprakja poraka do handlerot na UI Thread da go resetira interfejsot
        Message message = Message.obtain();
        message.arg1 = 1;//1 znaci reset
        if (!error) {
            //arg2 = 1 znaci deka nemalo greska
            //a onaka ima greska
            message.arg2 = 1;
        }
        handler.sendMessage(message);
    }
}
