package com.example.agustin.clientesoap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;


public class MainActivity extends Activity {

    static final int id=1;
    static final String direccionIp="http://148.211.72.35:8080";
    //static final String direccionIp="http://148.211.167.58:8080";

    SessionManager session;



    String contents;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    String TAG = "Response";

    //SoapPrimitive resultString;
    String resultString;
    String [] resp;
    private int idTurno;

    private ProgressDialog _progressDialog;
    private int _progress = 0;
    private Handler _progressHandler;

    Button btnLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the main content layout of the Activity
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });







        _progressHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (_progress >= 100) {
                    _progressDialog.dismiss();
                } else {
                    _progress++;
                    _progressDialog.incrementProgressBy(1);
                    _progressHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };
    }





    //product qr code mode
    public void scanQR(View v) {

        session.checkLogin();
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(MainActivity.this, "No se encontró un escaner", "Descargar uno?", "Si", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

    //ponemos el progress bar
                showDialog(1);
                _progress = 0;
                _progressDialog.setProgress(0);
                _progressHandler.sendEmptyMessage(0);
      //


                AsyncCallWS task = new AsyncCallWS();
                task.execute();



            }
        }
    }


    public void cambiarActividad(){
        Intent inte = new Intent(this, datosTurno.class);
        Log.d("turnoadatos",String.valueOf(idTurno));
        inte.putExtra("idTurno",idTurno);
        inte.putExtra("notificado",0);
        _progressDialog.setProgress(100);
        startActivity(inte);
    }



    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

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
            Log.i(TAG, "onPostExecute");
           // Toast.makeText(MainActivity.this, "Se le ha otorgado el turno número: " + resp[0] , Toast.LENGTH_LONG).show();

            cambiarActividad();

        }

    }

    public void calculate() {

        //parametros para turnos

        String METHOD_NAME = "altaTurno";
        String SOAP_ACTION = "http://turnos/"+ METHOD_NAME;
        String NAMESPACE = "http://turnos/";
        // String URL = "http://10.0.2.2:8080/turnosWS/turnos?WSDL";
        String URL = direccionIp+"/turnosWS/turnos?WSDL";
      //  String URL = "http://148.211.167.58:8080/turnosWS/turnos?WSDL";




        try {
            SoapObject Request =
                    new SoapObject(NAMESPACE, METHOD_NAME);


             turno turno=new turno();
            turno.setId_servicio(Integer.valueOf(contents));
            turno.setId_usuario(id);



            PropertyInfo item = new PropertyInfo();
            item.setType(turno.getClass());
            item.setName("turno");
            item.setValue(turno);




            Request.addProperty(item);
            Request.addProperty("turno", turno);

            SoapSerializationEnvelope soapEnvelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport =
                    new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);

          String aux=  soapEnvelope.getResponse().toString();

            resp=aux.split("/");
            idTurno= Integer.parseInt(resp[1]);

            Log.d("response",aux.toString());


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

 //progress bar

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return new AlertDialog.Builder(this)
                        .setIcon(R.drawable.icon)
                        .setTitle("This is a dialog with some simple text...")
                        .setPositiveButton("OK", new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        Toast.makeText(getBaseContext(),
                                                "OK clicked!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("Cancel", new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        Toast.makeText(getBaseContext(),
                                                "Cancel clicked!", Toast.LENGTH_SHORT).show();
                                    }
                                })

                        .create();
            case 1:
                _progressDialog = new ProgressDialog(this);
                _progressDialog.setIcon(R.drawable.icon);
                _progressDialog.setTitle("Procesando...");
                _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                _progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Hide", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton)
                            {
                                Toast.makeText(getBaseContext(),
                                        "Hide clicked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                _progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton)
                            {
                                Toast.makeText(getBaseContext(),
                                        "Cancel clicked!", Toast.LENGTH_SHORT).show();
                            }
                        });
                return _progressDialog;
        }
        return null;
    }


}