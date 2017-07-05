package id.rimbaborne.loginregisterphpmysql.services;

import android.animation.ObjectAnimator;
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Admin on 7/1/2017.
 */

public class Controller  extends Application {
    public static final String TAG = Controller.class.getSimpleName();
    private RequestQueue requestQueue;
    private static Controller instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized Controller getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue  == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
