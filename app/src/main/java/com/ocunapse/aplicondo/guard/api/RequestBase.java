package com.ocunapse.aplicondo.guard.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ocunapse.aplicondo.guard.GuardApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestBase extends AsyncTask<Void,Void, String> implements Serializable {

    public static GuardApp application = new GuardApp();
    public static Gson g = new GsonBuilder().create();
    public static String server = "https://aplicondo.ocunapse.com/v1/api";
    public static boolean Debug = true;
    public static boolean Live = true;




    //    public static String gameServer = RequestBase.Live ?"http://www.arenakl.com/api/api.php" : "https://arenagames.worldquest.io/api.php"   ;
//
//    private static String[] reallocations = {"http://www.arenakl.com/api/api.php", "http://www.arenattdi.com/api/api.php"};
//    private static String[] testlocations = {"https://arenagames.worldquest.io/api.php", "https://arenagames.worldquest.io/api.php"};
//    public static String[] locations = RequestBase.Live ? reallocations : testlocations;
//
    public String Action;

    public static class APIError{
        public int code = 0;
        public String message;
        public String cause;
    }

    public APIError ServerLost;

    public enum CallType{
        POST {
            @Override
            public String toString() {
                return "POST";
            }
        },
        GET {
            @Override
            public String toString() {
                return "GET";
            }
        }
    }

    private static TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs,
                        String authType) {}
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs,
                        String authType) {}
            }
    };


    public interface LoginResult{           void get(LoginRequest.LoginRes res);            }
    public interface UnitListResult{        void get(UnitListRequest.UnitListRes res);      }
//
//    //game
//    public interface TableListResult{       void get(TableListRequest.TableListRes res);            }
//    public interface ReceiptGameResult{    void get(RegisterGameRequest.RegisterGameRes res);      }
//    public interface GameBookResult{        void get(GameBookRequest.GameBookRes res);              }
//    public interface CancelBookResult{      void get(CancelBookRequest.CancelBookRes res);          }
//    public interface UpdateContactResult{   void get(UpdateContactRequest.UpdateContactRes res);    }
//    public interface GetPositionResult{     void get(GetPositionRequest.GetPositionRes res);        }
//
//    //iot
//    public interface SettingsResult{        void get(SettingsRequest.iot res);                      }



    @Override
    protected String doInBackground(Void... voids) {
        return null;
    }

    public String Request(String data, String server, CallType ct) {
        ServerLost = new APIError();
        ServerLost.message = "Sorry Server Down Please try again Later";
        ServerLost.code = 999;
        if(server.startsWith("https"))
            return HttpsRequest(data, server, ct);
        else
            return HttpRequest(data, server, ct);
    }

    public String HttpsRequest(String data, String server, CallType ct){
        String TAG = Tagit(server);
        String text = "";
        BufferedReader reader = null;

        try {
            URL url = new URL(server);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //HttpURLConnection conn =(HttpURLConnection) url.openConnection();

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            conn.setUseCaches(false);
            conn.setRequestProperty("Authorization","BEARER "+application.getToken());
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if(ct == CallType.POST) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                //DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                //wr.write(data.getBytes());
                wr.flush();
                wr.close();
            }


            int respCode = conn.getResponseCode();
            Log.d("responsecode",respCode+"");
            InputStreamReader in ;
            if(respCode != 200) {
                in = new InputStreamReader(conn.getErrorStream());
            }
            else
                in = new InputStreamReader(conn.getInputStream());
            reader = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null)
            { sb.append(line); }

            text = sb.toString();
            LOG(TAG,sb.toString());
            conn.disconnect();
        }
        catch(Exception ex) {
            Log.e(TAG,ex.getMessage(),ex);
        }
        finally
        {
            try {  if(reader != null)
                reader.close(); }
            catch(Exception ex) { Log.e(TAG, ex.getMessage(), ex); }

        }
        return text;
    }

    public String HttpRequest(String data, String server,CallType ct){
        String TAG = Tagit(server);
        String text = "";
        BufferedReader reader = null;

        try {
            URL url = new URL(server);
            //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();

//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            conn.setUseCaches(false);
            if(ct == CallType.POST) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                //DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                //wr.write(data.getBytes());
                wr.flush();
                wr.close();
            }


            int respCode = conn.getResponseCode();
            Log.d("responsecode",respCode+"");
            InputStreamReader in ;
            if(respCode == 400) {
                in = new InputStreamReader(conn.getErrorStream());
            }
            else
                in = new InputStreamReader(conn.getInputStream());
            reader = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null)
            { sb.append(line); }

            text = sb.toString();
            LOG(TAG,sb.toString());
            conn.disconnect();
        }
        catch(Exception ex) {
            Log.e(TAG,ex.getMessage(),ex);
        }
        finally
        {
            try {  if(reader != null)
                reader.close(); }
            catch(Exception ex) { Log.e(TAG, ex.getMessage(), ex); }

        }
        return text;
    }

    static String Tagit(String url) {
        //String res = "basic";
        String[] tg = url.split("/");
        String key = (tg[tg.length-1]);//.split("\\."))[0];
        return key;
    }

    public static void LOG( String tag, String msg){
        if(Debug) Log.d(tag,msg);
    }



    //            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        final MediaType JSON = MediaType.get("application/json");
//                        OkHttpClient client = new OkHttpClient();
//                        RequestBody formBody = RequestBody.create("{\"userId\":\"" + uname + "\",\"password\":\"" + md5(pwd).toLowerCase() + "\"}", JSON);
//                        Request request = new Request.Builder()
//                                .post(formBody)
//                                .url("https://aplicondo.ocunapse.com/api/login")
//                                .build();
//                        try {
//                            Response response = client.newCall(request).execute();
//                            System.out.println(response.body().string());
////                            {"success":false,"error":{"message":"Invalid credentials","cause":"","code":1040}}
//                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//                            startActivity(i);
//                        } catch (IOException err) {
//                            System.out.println("err = " + err);
//                            Snackbar.make(binding.getRoot(),"FUck", Snackbar.LENGTH_LONG).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

//            thread.start();

}