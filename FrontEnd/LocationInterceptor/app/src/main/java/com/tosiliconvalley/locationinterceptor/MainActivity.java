package com.tosiliconvalley.locationinterceptor;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bezyapps.floatieslibrary.Floaty;
import com.bezyapps.floatieslibrary.FloatyOrientationListener;
import com.google.gson.Gson;
import com.tosiliconvalley.locationinterceptor.communication.AllUsersLocationRequest;
import com.tosiliconvalley.locationinterceptor.communication.AllUsersLocationResponse;
import com.tosiliconvalley.locationinterceptor.data.AllUsersLocations;
import com.tosiliconvalley.locationinterceptor.communication.ServerInteraction;
import com.tosiliconvalley.locationinterceptor.data.AllUsersPathLink;
import com.tosiliconvalley.locationinterceptor.data.ChecheData;
import com.tosiliconvalley.locationinterceptor.data.UserLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "MainActivity";

    private static final Integer TIMEOUT = 5000;

    private static final String TEST_URL = "http://181.30.35.21:48080/central/getnumbers?bot_id=1&key=lalaland";

    private static final String TEST_JSON =
        "{" +
            "\"location\": \"Thames 1407, C1414DDC CABA, Argentina\"," +
            "\"map_url\": \"http://maps.google.com.ar/?q=-34.589286224,-58.4329376683\"," +
            "\"lat\": -34.5892," +
            "\"lon\": -58.4329," +
            "\"bars\": [" +
            "{" +
                "\"position\": {" +
                "\"lat\": -34.6036," +
                "\"lng\": -58.3815 " +
            "}," +
            "\"name\": \"Buenos Aires\"" +
            "}," +
            "{" +
                "\"position\": {" +
                "\"lat\": -34.5883, " +
                "\"lng\": -58.4315 " +
            "}," +
                "\"name\": \"L'Hotel Palermo\"" +
            "}" +
            "]" +
        "}";

    ClipboardController clipboardController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(LOGTAG, "MainActivity onStart");

        // toma el intent generado desde WA
        Intent intent = getIntent();

        if( intent != null && Intent.ACTION_VIEW.equals(intent.getAction()) )  {
            if (intent.getData() != null) {

                try {
                    // toma la data del intent y la parsea para obtener localizacion (latitud y
                    // longitud) y el nombre del usuario que la generó
                    URL url = new URL(intent.getData().toString());
                    String urlQuery = url.getQuery();
                    urlQuery = urlQuery.replaceAll("(q=loc:)", "");

                    String[] values = urlQuery.split("[()]");
                    String location[] = values[0].split("[,]");

                    UserLocation currentUserLocation = new UserLocation(
                            Double.parseDouble(location[0]),
                            Double.parseDouble(location[1]),
                            values[1]
                    );

                    // La nueva data de localizacion se persiste a nivel de aplicación de manera
                    // que esta información no se pierda la reiniciarse el contexto por causa de
                    // nuevo intent generado desde WhatsApp

                    //if(!MyApp.wasProcessed(currentUserLocation.getName().toString())) {

                        MyApp.addUserLocation(2, currentUserLocation); // el primer parametro representa
                        // al grupo de WhatsApp, para demo solo un grupo

                        Log.d(LOGTAG, "user added: " + currentUserLocation.toString());
                    //}
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        if (MyApp.getGroupData(2) != null) {
            // Se crea un string a partir de la información guardada en el contexto de la
            // aplicación (y no del Activity)
            StringBuilder stringBuilder = new StringBuilder();
            for (UserLocation ul : MyApp.getGroupData(2)) {
                stringBuilder.append(ul.toString());
                stringBuilder.append(System.getProperty("line.separator"));
            }

            // Se muestra en "Users Location Added" cada uno de los datos de localizacion
            // aportados por cada usuario del chat
            TextView usersLocationsTextView = (TextView) findViewById(R.id.usersLocationsTextView);
            usersLocationsTextView.setText(stringBuilder.toString());
        }

        View head = LayoutInflater.from(this).inflate(R.layout.float_head, null);
        View body = LayoutInflater.from(this).inflate(R.layout.float_body, null);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = Floaty.createNotification(
                this, "Floaty Demo", "Service Running", R.drawable.float_icon, resultPendingIntent);

        Floaty floaty = Floaty.createInstance(this, head, body, 100, notification);

        floaty.startService();

        Button sendRequestButton = (Button) body.findViewById(R.id.sendRequestButton);

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // se crea un contenedor de localizaciones de usuarios
                AllUsersLocations allUsersLocations = new AllUsersLocations(2);

                allUsersLocations.setUsersLocatonList(MyApp.getGroupData(2));
                //loadWithFakeUserData(allUsersLocations);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(allUsersLocations);

                TextView jsonRequestTextView = (TextView) findViewById(R.id.jsonRequestTextView);
                jsonRequestTextView.setText(jsonInString);

                AllUsersLocationRequest allUsersLocationRequest = new AllUsersLocationRequest(
                        "http",
                        "181.30.35.20",
                        58080,
                        "abInBev/botPost",
                        allUsersLocations
                );

                new HttpAsyncTask(getBaseContext()).execute(allUsersLocationRequest);
            }
        });
    }

    public class HttpAsyncTask extends AsyncTask<ServerInteraction, Void, String> {

        private Context uiContext;

        public HttpAsyncTask(Context uiContext){
            this.uiContext = uiContext;
        }

        @Override
        protected String doInBackground(ServerInteraction... serverInteraction) {

            ServerInteraction currentInteraction = serverInteraction[0];

            if (currentInteraction instanceof AllUsersLocationRequest) {
                AllUsersLocationRequest allUsersLocationsRequest =
                        (AllUsersLocationRequest) currentInteraction;

                Log.d(LOGTAG, "executing request");

                String url = allUsersLocationsRequest.getUrl();

                String response = sendJson(
                        allUsersLocationsRequest.getUrl(),
                        allUsersLocationsRequest.getJsonData()
                );

                return response;
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(LOGTAG, "received request: " + result);

            //result = TEST_JSON;
            if(result == "")
                result = "Server Error";

            //try {
                Gson gson = new Gson();
                //AllUsersPathLink allUsersPathLink = gson.fromJson(result, AllUsersPathLink.class);
                //ChecheData checheData = gson.fromJson(result, ChecheData.class);
/*
                if(isACompleteResponse(addPhonesTask)) {
                    Toast.makeText(uiContext, "New task received!", Toast.LENGTH_LONG).show();

                    File taskFile = new File(uiContext.getExternalFilesDir(null), TASK_FILE_NAME);
                    taskFile.exists();
                    taskFile.createNewFile();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(taskFile, true));
                    writer.write(gson.toString());
                    writer.close();

                    if(taskFile.delete())
                        Log.d(LOGTAG, "task file was deleted");
                    else Log.d(LOGTAG, "task file was NOT deleted");

                    // Stop scheduling
                    stopTaskRequestScheduling();

                    // Send task to service
                    myEventBus.post(addPhonesTask);
                }
                else {
                    Toast.makeText(uiContext, "Not valid task received!", Toast.LENGTH_LONG).show();

                    //avisar al servidor!!!

                    startTaskRequestScheduling(SCHEDULE_MINUTES);
                }
                */
            /*
            }
            catch (IOException e) {
                Log.e(LOGTAG, "can't create or write file");
            }
            */

            clipboardController = new ClipboardController();
            clipboardController.enableClipedTextListening();

            clipboardController.putTextToClipboard(LOGTAG,result);

            MyApp.cleanGroupData(2);
            Log.d(LOGTAG, "group data cleanned");

            //Button sendRequestButton = (Button) findViewById(R.id.sendRequestButton);
            //sendRequestButton.setEnabled(false);

            //TextView statusTextView = (TextView) findViewById(R.id.statusTextView);
            //statusTextView.setText("READY!");
        }

        // Verifica que
        private boolean isACompleteResponse(AllUsersPathLink allUsersPathLink) {
            if(allUsersPathLink.getGroup() != null &&
                    allUsersPathLink.getGlobalLocation() != null &&
                    allUsersPathLink.getUsersPathLinkList() != null
                    )
                return true;
            else {
                Log.d(LOGTAG, "one or more null values on BarLocationResponse object");
                return false;
            }
        }

        public String sendJson(String urlString, String jsonString) {

            try {
                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(TIMEOUT); //set timeout to 5 seconds

                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());

                writer.write(jsonString);
                writer.flush();
                writer.close();

                Log.d(LOGTAG, "RESPONSE " + connection.getResponseCode());

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8"));

                    String line = null;

                    StringBuilder sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    return  sb.toString();

                } else {
                    Log.e(LOGTAG,connection.getResponseMessage());
                }

            } catch (MalformedURLException e) {
                Log.e(LOGTAG, e.toString());
            } catch (IOException e) {
                Log.e(LOGTAG, e.toString());
            }

            return "";
        }

        // data is encoded as key-value pairs like standard HTML form
        public String GET(String urlString){

            StringBuffer chain = new StringBuffer("");
            HttpURLConnection connection = null;

            try{
                URL url = new URL(urlString);

                connection = (HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(TIMEOUT); //set timeout to 5 seconds

                // handle issues
                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // handle any other errors, like 404, 500,..
                }

                InputStream inputStream = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    chain.append(line);
                }
            }
            catch (MalformedURLException e) {
                Log.e(LOGTAG, "URL is invalid");
            }
            catch (java.net.SocketTimeoutException e) {
                Log.e(LOGTAG, "data retrieval or connection timed out");
            }
            catch (IOException e) {
                Log.e(LOGTAG, "could not read response body (could not create input stream)");
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return chain.toString();
        }
    }

    /**
     * Permite controlar funciones básicas del clipboard mediante la administración de un
     * ClipboardManager, acceso a sus métodos y asociación de listeners que ejecutan operaciones de
     * negocio
     */
    private class ClipboardController {

        ClipboardManager clipboardManager;

        public ClipboardController() {
            clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        }

        /**
         * Asigna al clipboard manager un listener encargado de detectar cambios en el clipboard y
         * ejecutar una acción en caso de darse esa situación
         */
        public void enableClipedTextListening() {
            ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
                public void onPrimaryClipChanged() {
                    performTextCheck();
                }
            };
            clipboardManager.addPrimaryClipChangedListener(listener);
        }

        public void putTextToClipboard(String label, String text) {
            ClipData clipData = ClipData.newPlainText(label, text);
            clipboardManager.setPrimaryClip(clipData);
        }

        public ClipData getTextFromClipboard() throws Exception {
            if (clipboardManager.hasPrimaryClip())
                return clipboardManager.getPrimaryClip();
            else throw new Exception();
        }

        /**
         * Verifica si el clip primario del clipboard tiene contenido de tipo texto y en ese caso
         * ejecuta una acción
         */
        private void performTextCheck() {
            if (clipboardManager.hasPrimaryClip()) {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.d(LOGTAG, "TEXT CLIPPED");
                }
            }
        }

    }

    private void loadWithFakeUserData(AllUsersLocations allUsersLocations) {

        TextView usersLocationsTextView = (TextView) findViewById(R.id.usersLocationsTextView);
        StringBuilder stringBuilder = new StringBuilder(usersLocationsTextView.getText());

        // Localizacion de usuario 1
        UserLocation testUserLocation1 = new UserLocation(-34.591075, -58.416102, "user1");
        allUsersLocations.addUserLocation(testUserLocation1);
        stringBuilder.append(testUserLocation1.toString());
        stringBuilder.append(System.getProperty("line.separator"));

        usersLocationsTextView.setText(stringBuilder.toString());

        // Localizacion de usuario 2
        UserLocation testUserLocation2 = new UserLocation(-34.599562, -58.422532, "user2");
        allUsersLocations.addUserLocation(testUserLocation2);
        stringBuilder.append(testUserLocation2.toString());
        stringBuilder.append(System.getProperty("line.separator"));

        usersLocationsTextView.setText(stringBuilder.toString());

        // Localizacion de usuario 2
        UserLocation testUserLocation3 = new UserLocation(-34.596925, -58.398473, "user3");
        allUsersLocations.addUserLocation(testUserLocation3);
        stringBuilder.append(testUserLocation3.toString());
        stringBuilder.append(System.getProperty("line.separator"));

        usersLocationsTextView.setText(stringBuilder.toString());

        // Localizacion de usuario 4
        UserLocation testUserLocation4 = new UserLocation(-34.623227, -58.397569, "user4");
        allUsersLocations.addUserLocation(testUserLocation4);
        stringBuilder.append(testUserLocation4.toString());
        stringBuilder.append(System.getProperty("line.separator"));

        usersLocationsTextView.setText(stringBuilder.toString());
    }

}