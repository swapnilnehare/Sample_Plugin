package cordova.sample;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.INITIALIZE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
/**
 * This class echoes a string called from JavaScript.
 */
public class sample extends CordovaPlugin {

    private FingerprintManager mFingerPrintManager;
    private KeyguardManager mKeyguardManager;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init FingerprintAuth");

        if (android.os.Build.VERSION.SDK_INT < 23) {
            return;
        }

        mKeyguardManager = cordova.getActivity().getSystemService(KeyguardManager.class);
        mFingerPrintManager = cordova.getActivity().getApplicationContext()
                            .getSystemService(FingerprintManager.class);

    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if(action.equals("register"))
        {
            this.register(args,callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    private void register(JSONArray args, CallbackContext callback)
    {
        if(args != null)
        {
                try
                {
                     String username = args.getJSONObject(0).getString("username");
                     String password = args.getJSONObject(0).getString("password");
                     genarateKey(username);
                     byte[] publicKeyBytes = publicKey.getEncoded();
                     String publicKeyString = Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP);
                     Log.i("Public key",publicKeyString);
                     JSONObject jsonParam = new JSONObject();
                     jsonParam.put("username",us);
                     jsonParam.put("password",pw);
                     jsonParam.put("publickey",publicKeyString);
                     Log.i("JsonObject",jsonParam.toString());
                     new SendDeviceDetails().execute(jsonParam.toString());
                    
                          
                }catch(Exception ex)
                {
                    callback.error("Something went wrong "  + ex);
                }
        }else
        {
            callback.error("Please donot pass null value");
        }
    }
    class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data="";
            try{

                URL url = new URL("http://192.168.43.176:8080/authenticate");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(params[0]);
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
                Log.i("data",data);


            }
            catch (Exception e)
            {
                Log.d("error", "user data send failed!", e);
            }
            return data;
        }
}
}
