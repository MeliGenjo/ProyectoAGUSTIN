package com.example.agustin.clientesoap;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LoginActivity extends Activity {
    String TAG = "Response";
    String resp;
    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

       // Toast.makeText(getApplicationContext(), "Estado de la sesión: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    // For testing puspose username, password is checked with sample data
                    // username = test
                    // password = test
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();


                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "La autenticación falló", "Por favor ingreso usuario y contraseña", false);
                }

            }
        });
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

            if (resp.equals("true")) {

                // Creating user login session
                // For testing i am stroing name, email as follow
                // Use user real data
                session.createLoginSession("Android Hive", "anroidhive@gmail.com");

                // Staring MainActivity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();

            } else {
                // username / password doesn't match
                alert.showAlertDialog(LoginActivity.this, "La autenticación falló", "Usuario/contraseña incorrecto", false);
            }



        }

    }


    public void calculate() {


        //parametros para turnos

        String METHOD_NAME = "validarUsuario";
        String SOAP_ACTION = "http://turnos/"+ METHOD_NAME;
        String NAMESPACE = "http://turnos/";
        // String URL = "http://10.0.2.2:8080/turnosWS/turnos?WSDL";
        String URL = MainActivity.direccionIp+"/turnosWS/turnos?WSDL";
        //  String URL = "http://148.211.167.58:8080/turnosWS/turnos?WSDL";




        try {
            SoapObject Request =
                    new SoapObject(NAMESPACE, METHOD_NAME);

                usuario usuario=new usuario();
            usuario.setContraseña(txtPassword.getText().toString());
            usuario.setEmail(txtUsername.getText().toString());





            PropertyInfo item = new PropertyInfo();
            item.setType(usuario.class);
            item.setName("usuario");
            item.setValue(usuario);




            Request.addProperty(item);
            Request.addProperty("usuario", usuario);

            SoapSerializationEnvelope soapEnvelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport =
                    new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);

            String aux=  soapEnvelope.getResponse().toString();

            resp=aux;

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
}