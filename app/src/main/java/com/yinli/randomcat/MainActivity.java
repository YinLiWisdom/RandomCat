package com.yinli.randomcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;
import com.yinli.randomcat.adapters.GridAdapter;
import com.yinli.randomcat.data.CatImage;
import com.yinli.randomcat.utils.Typefaces;
import com.yinli.randomcat.utils.XMLHelper;

import org.parceler.Parcels;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private static final String URL = "http://thecatapi.com/api/images/get?format=xml&results_per_page=20";
    private static final String CONNERROR = "connection_error";
    private static final String XMLERROR = "xml_error";
    private static final String SUCCESS = "success";

    private static boolean connected = false;
    private static boolean refresh = true;

    private NetworkReceiver receiver = new NetworkReceiver();
    private List<CatImage> catImages;
    private TitanicTextView titanicTextView;
    private GridView gridView;
    private TextView errorText;
    private LinearLayout errorContainer;
    private Titanic titanic;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register network broadcast receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        errorContainer = (LinearLayout) findViewById(R.id.connectionErrorContainer);
        errorText = (TextView) findViewById(R.id.errorText);
        gridView = (GridView) findViewById(R.id.gridView);

        init();

        adapter = new GridAdapter(MainActivity.this, catImages);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(MainActivity.this);
    }

    private void init() {
        catImages = (List<CatImage>) getLastCustomNonConfigurationInstance();
        if (catImages == null) {
            catImages = new ArrayList<>();
            loadDataFromNetwork();
        }
    }

    public void refreshClick(View view) {
        loadDataFromNetwork();
    }

    private void loadDataFromNetwork() {
        updateConnectedFlags();
        // Only allowed to refresh the page if the device has a network connection
        if (refresh) {
            loadData();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return catImages;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateConnectedFlags() {
        ConnectivityManager connManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            connected = true;
        } else {
            connected = false;
        }
    }

    private void loadData() {
        if (connected) {
            errorContainer.setVisibility(View.GONE);
            new DownloadXmlTask().execute(URL);
        } else {
            showErrorPage(CONNERROR);
        }
    }

    private void showErrorPage(String errorType) {
        errorContainer.setVisibility(View.VISIBLE);
        if (TextUtils.equals(errorType, CONNERROR)) {
            errorText.setText(R.string.connection_error_text);
        } else if (TextUtils.equals(errorType, XMLERROR)) {
            errorText.setText(R.string.xml_error_text);
        }

    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startLoading();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return CONNERROR;
            } catch (XmlPullParserException e) {
                return XMLERROR;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.equals(result, CONNERROR)) {
                showErrorPage(CONNERROR);
            } else if (TextUtils.equals(result, XMLERROR)) {
                showErrorPage(XMLERROR);
            } else {
                endLoading();
                updateGrid();
            }
        }
    }

    private void updateGrid() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        intent.putExtra("position", position);
        Parcelable list = Parcels.wrap(catImages);
        intent.putExtra("images", list);
        startActivity(intent);
    }

    private void startLoading() {
        titanicTextView = (TitanicTextView) findViewById(R.id.titanicLoading);
        titanicTextView.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
        titanicTextView.setVisibility(View.VISIBLE);
        titanic = new Titanic();
        titanic.start(titanicTextView);
    }

    private void endLoading() {
        titanic.cancel();
        titanicTextView.setVisibility(View.INVISIBLE);
    }

    private String loadXmlFromNetwork(String url) throws XmlPullParserException, IOException {
        InputStream stream = null;
        try {
            stream = downloadUrl(url);
            catImages.clear();
            catImages.addAll(new XMLHelper().parse(stream));
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return SUCCESS;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister network broadcast receiver
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateConnectedFlags();
            if (refresh) {
                loadData();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                refresh = true;
            } else {
                refresh = false;
            }
        }
    }
}
