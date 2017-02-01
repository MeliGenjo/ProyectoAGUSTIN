package com.example.agustin.clientesoap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Timer;
import java.util.TimerTask;


public class datosTurno extends Activity implements LocationListener {
  //gps
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;
int notificado;



Boolean cancelado=false;

    Timer timer;
    static final int id=1;
    informacion info= new informacion();
    double latitudServicio=0;
    double longitudServicio=0;
    double latitudMovil=0;
    double longitudMovil=0;

    String contents;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    String TAG = "Response";

    //SoapPrimitive resultString;
    String resultString;
    int idTurno;
    String respuestaCancelado;

    TextView nombre, miturno, turnoactual, tiempo, distancia;
    Button cancelar, ubicacion;

     private class RemindTask extends TimerTask {
        public void run() {

            AsyncCallInfo task = new AsyncCallInfo();
            task.execute();
            Log.d("timer", "estoy ejecutando");

        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the main content layout of the Activity
        setContentView(R.layout.activity_datos_turno);

        nombre=(TextView)findViewById(R.id.nombre);
        miturno=(TextView)findViewById(R.id.miturno);
        turnoactual=(TextView)findViewById(R.id.turnoactual);
        tiempo=(TextView)findViewById(R.id.tiempoaprox);
        distancia=(TextView)findViewById(R.id.dist);
        cancelar=(Button) findViewById(R.id.cancelarturno);
        ubicacion=(Button) findViewById(R.id.mapa);

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarActividad();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncCallCancel task = new AsyncCallCancel();
                task.execute();
            }
        });

        Bundle extras=getIntent().getExtras();
       idTurno=extras.getInt("idTurno");
        notificado=extras.getInt("notificado");


        AsyncCallInfo task = new AsyncCallInfo();
        task.execute();

        timer = new Timer();
        timer.schedule(new RemindTask(),1000, 5000); //delay on 1000 repeat on 5000



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }


    }



    public void cambiarActividad(){
        Intent inte = new Intent(this, MapsActivity.class);

        inte.putExtra("lat",latitudServicio);
        inte.putExtra("long", longitudServicio);

        startActivity(inte);
    }



    private class AsyncCallInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            if (cancelado != true) {
                Log.i(TAG, "onPostExecute");
                String[] aux = resultString.split("/");
                nombre.setText(aux[0]);
                miturno.setText(aux[1]);
                turnoactual.setText(aux[2]);
                tiempo.setText(aux[3]);
                //si el tiempo es menor a media hora emitir notifiacion
                String[] tiempoAux = aux[3].split("m");
                int tiempo = Integer.valueOf(tiempoAux[0].toString());
                if (tiempo < 30)
                    if (notificado == 0) {
                        displayNotification();
                        notificado = 1;
                    }

                latitudServicio = Double.valueOf(aux[4].toString());
                longitudServicio = Double.valueOf(aux[5].toString());


                // Toast.makeText(datosTurno.this, "Info " + resultString, Toast.LENGTH_LONG).show();


            }
        }

    }

    public void calculate() {

        //parametros para turnos

        String METHOD_NAME = "getInfoTurno";
        String SOAP_ACTION = "http://turnos/"+ METHOD_NAME;
        String NAMESPACE = "http://turnos/";
        // String URL = "http://10.0.2.2:8080/turnosWS/turnos?WSDL";
        String URL = MainActivity.direccionIp+"/turnosWS/turnos?WSDL";
        //  String URL = "http://148.211.167.58:8080/turnosWS/turnos?WSDL";




        try {
            SoapObject Request =
                    new SoapObject(NAMESPACE, METHOD_NAME);
            //recuperamos el id del turno

            Log.d("turno",String.valueOf(idTurno));

            Request.addProperty("id_turno", idTurno);


            SoapSerializationEnvelope soapEnvelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            soapEnvelope.setOutputSoapObject(Request);





            HttpTransportSE transport =
                    new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);


            resultString = soapEnvelope.getResponse().toString();
           // resultString = resp.getProperty("miTurno").toString();







            Log.d("respuesta: ", resultString);


            //si la respuesta es un tipo de dato, o estructura compleja usa esto
//SoapObject resp = (SoapObject)envelope.getResponse();
            //SoapObject resp = (SoapObject)soapEnvelope.getResponse();
            //resultString = resp.getProperty("return").toString();
//String id = resp.getProperty(“id”).toString();



        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    private class AsyncCallCancel extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            ejecutarCancel();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            timer.cancel();
            nombre.setText("---");
            miturno.setText("---");
            turnoactual.setText("---");
            tiempo.setText("---");
            distancia.setText("---");


           if(respuestaCancelado.equals("true"))
            Toast.makeText(datosTurno.this, "El turno ha sido cancelado", Toast.LENGTH_LONG).show();
            else
               Toast.makeText(datosTurno.this, "Error en la cancelacion" , Toast.LENGTH_LONG).show();


        }

    }

    public void ejecutarCancel() {

        //parametros para turnos

        String METHOD_NAME = "cancelarTurno";
        String SOAP_ACTION = "http://turnos/"+ METHOD_NAME;
        String NAMESPACE = "http://turnos/";
        // String URL = "http://10.0.2.2:8080/turnosWS/turnos?WSDL";
        String URL = MainActivity.direccionIp+"/turnosWS/turnos?WSDL";
        //  String URL = "http://148.211.167.58:8080/turnosWS/turnos?WSDL";

   cancelado=true;


        try {
            SoapObject Request =
                    new SoapObject(NAMESPACE, METHOD_NAME);
            //recuperamos el id del turno

            Log.d("turno",String.valueOf(idTurno));

            Request.addProperty("id_turno", idTurno);


            SoapSerializationEnvelope soapEnvelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            soapEnvelope.setOutputSoapObject(Request);





            HttpTransportSE transport =
                    new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);


            respuestaCancelado = soapEnvelope.getResponse().toString();
            // resultString = resp.getProperty("miTurno").toString();







            Log.d("respuesta: ", resultString);


            //si la respuesta es un tipo de dato, o estructura compleja usa esto
//SoapObject resp = (SoapObject)envelope.getResponse();
            //SoapObject resp = (SoapObject)soapEnvelope.getResponse();
            //resultString = resp.getProperty("return").toString();
//String id = resp.getProperty(“id”).toString();



        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private  double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }



    //GPS
    @Override
    public void onLocationChanged(Location location) {

        latitudMovil=location.getLatitude();
        longitudMovil=location.getLongitude();
        Double dist=distance(latitudServicio,latitudMovil,longitudServicio,longitudMovil,0.0,0.0);
        distancia.setText(String.valueOf(dist.intValue())+" mts.");

        LinearLayout RL = (LinearLayout)findViewById(R.id.Layout);

        if(dist>10000)
            RL.setBackgroundColor(Color.parseColor("#FF0000"));



    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

  //notificaciones

    protected void displayNotification() {
//---PendingIntent to launch activity if the user selects
// this notification---
        Intent i = new Intent(this, datosTurno.class);
        i.putExtra("notificationID", 1);
        i.putExtra("idTurno", idTurno);
        i.putExtra("notificado",1);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, i,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Bitmap bm = BitmapFactory.
                decodeResource(getResources(), R.drawable.ticketera);
        Notification notif = new Notification.Builder(getBaseContext())
                .setAutoCancel(true)
                .setContentTitle("Su turno esta próximo."
                        )
                .setContentText("Será atendido en aproximadamente: "
                        +tiempo.getText().toString())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bm)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSubText("Recuerde llegar a tiempo")   //API level 16
                .setNumber(100)
                .build();
//---100ms delay, vibrate for 250ms, pause for 100 ms and
// then vibrate for 500ms---
        notif.vibrate = new long[]{100, 250, 100, 500};
        nm.notify(1, notif);
    }


}