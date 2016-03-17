package simrat.cube26;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import simrat.cube26.data.Cube26DbHelper;
import simrat.cube26.model.PaymentGateway;

public class PaymentGatewayList extends AppCompatActivity {

    private String DEBUG_TAG = this.getClass().getName().toString();
    private SwipeRefreshLayout refreshLayout;
    private ListView paymentgatewayList;
    private Button sortName;
    private Button sortRating;
    private SearchView searchView;
    private Switch setupSwitch;
    private Cube26DbHelper dbHelper;
    private ArrayAdapter<CharSequence> listRows;
    private String[] values;
    private Button openLikes;
    private ImageView downloadButton;
    private ImageView headerIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway_list);
        dbHelper = new Cube26DbHelper(getApplicationContext());
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        searchView = (SearchView) findViewById(R.id.searchView);
        setupSwitch = (Switch) findViewById(R.id.setupSwitch);
        headerIcon = (ImageView) findViewById(R.id.headerIcon);
        headerIcon.setBackgroundResource(R.drawable.home);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search by name or currency");
        sortName = (Button) findViewById(R.id.sortName);
        sortRating = (Button) findViewById(R.id.sortRating);
        paymentgatewayList = (ListView) findViewById(R.id.payment_gateway_list);
        openLikes = (Button) findViewById(R.id.openLikes);

        /* Download Section */
        LayoutInflater factory = getLayoutInflater();
        View v = factory.inflate(R.layout.list_item, null);
        downloadButton = (ImageView) v.findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "Download");
                LinearLayout linearLayout = (LinearLayout) view.getParent().getParent();
                LinearLayout layout = (LinearLayout) linearLayout.getChildAt(0);
                TextView label = (TextView) layout.findViewById(R.id.label);
                Log.d(DEBUG_TAG, label.getText().toString());
                //new DownloadFile().execute()
            }
        });

        /* Open Likes */
        openLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentGatewayList.this, LikedGateways.class);
                startActivity(intent);
            }
        });

        /* Run on app install to get API data */
        if(dbHelper.getCount() == 0){
            if(!isInternetAvailable())
                Toast.makeText(PaymentGatewayList.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
            final ProgressDialog progressDialog = new ProgressDialog(PaymentGatewayList.this);
            progressDialog.setMessage("Loading..");
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://hackerearth.0x10.info/api/payment_portals?type=json&query=list_gateway";
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("payment_gateways"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    PaymentGateway paymentGateway = new PaymentGateway();
                                    JSONObject curr = jsonArray.getJSONObject(i);
                                    paymentGateway.setName(curr.getString("name"));
                                    paymentGateway.setDescription(curr.getString("description"));
                                    paymentGateway.setBranding(curr.getString("branding"));
                                    paymentGateway.setCurrencies(curr.getString("currencies"));
                                    paymentGateway.setHow_to_url(curr.getString("how_to_document"));
                                    paymentGateway.setRating(curr.getString("rating"));
                                    paymentGateway.setSetup_fee(curr.getString("setup_fee"));
                                    paymentGateway.setTrans_fee(curr.getString("transaction_fees"));
                                    dbHelper.addGateway(paymentGateway);
                                }
                                values = dbHelper.getNames();
                                listRows = new ArrayAdapter<CharSequence>(PaymentGatewayList.this, R.layout.list_item, R.id.label, values);
                                listRows.notifyDataSetChanged();
                                paymentgatewayList.setAdapter(listRows);
                                progressDialog.hide();

                            } catch (JSONException e) {

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.print(error.getMessage());
                            progressDialog.hide();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("type", "json");
                    params.put("query", "list_gateway");
                    return params;
                }
            };
            queue.add(request);
        }
        else{

        }

        /* SQLite data fetch and Refresh Layout to load new data */
        values = dbHelper.getNames();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                Log.d(DEBUG_TAG, "Refreshing..");
                String url = "http://hackerearth.0x10.info/api/payment_portals?type=json&query=list_gateway";
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response.toString());
                                try {
                                    dbHelper.deleteRows();
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    JSONArray jsonArray = new JSONArray(jsonObject.getString("payment_gateways"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        PaymentGateway paymentGateway = new PaymentGateway();
                                        JSONObject curr = jsonArray.getJSONObject(i);
                                        paymentGateway.setName(curr.getString("name"));
                                        paymentGateway.setDescription(curr.getString("description"));
                                        paymentGateway.setBranding(curr.getString("branding"));
                                        paymentGateway.setCurrencies(curr.getString("currencies"));
                                        paymentGateway.setHow_to_url(curr.getString("how_to_document"));
                                        paymentGateway.setRating(curr.getString("rating"));
                                        paymentGateway.setSetup_fee(curr.getString("setup_fee"));
                                        paymentGateway.setTrans_fee(curr.getString("transaction_fees"));
                                        dbHelper.addGateway(paymentGateway);
                                    }

                                } catch (JSONException e) {

                                }
                                values = dbHelper.getNames();
                                listRows = new ArrayAdapter<CharSequence>(PaymentGatewayList.this, R.layout.list_item, R.id.label, values);
                                listRows.notifyDataSetChanged();
                                paymentgatewayList.setAdapter(listRows);
                                refreshLayout.setRefreshing(false);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.print(error.getMessage());
                                refreshLayout.setRefreshing(false);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("type", "json");
                        params.put("query", "list_gateway");
                        return params;
                    }
                };
                queue.add(request);
            }
        });

        listRows = new ArrayAdapter<CharSequence>(this, R.layout.list_item, R.id.label, values);
        paymentgatewayList.setAdapter(listRows);


        /* SetUp Fee Switch */
        setupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(DEBUG_TAG, "Checking..");
                if (b) {
                    values = dbHelper.getNamesOnSetupFee("1");
                } else {
                    values = dbHelper.getNames();
                }
                listRows = new ArrayAdapter<CharSequence>(PaymentGatewayList.this, R.layout.list_item, R.id.label, values);
                listRows.notifyDataSetChanged();
                paymentgatewayList.setAdapter(listRows);
            }
        });


        /* sort by name */
        sortName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(DEBUG_TAG, "name clicked");
                List<String> list = new ArrayList<String>(Arrays.asList(values));
                Collections.sort(list);
                int i = 0;
                for (String s : list) {
                    values[i] = s;
                    i++;
                }
                listRows = new ArrayAdapter<CharSequence>(PaymentGatewayList.this, R.layout.list_item, R.id.label, values);
                listRows.notifyDataSetChanged();
                paymentgatewayList.setAdapter(listRows);
            }
        });

        /* sort by rating */
        sortRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Double> map = new HashMap<String, Double>(dbHelper.getNameRating());
                List<Map.Entry<String, Double>> list =
                        new LinkedList<Map.Entry<String, Double>>(map.entrySet());

                // Sort list with comparator, to compare the Map values
                Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                    public int compare(Map.Entry<String, Double> o1,
                                       Map.Entry<String, Double> o2) {
                        return (o1.getValue()).compareTo(o2.getValue());
                    }
                });
                int i = 0;
                Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
                for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext(); ) {
                    Map.Entry<String, Double> entry = it.next();
                    values[i] = entry.getKey();
                    i++;
                    sortedMap.put(entry.getKey(), entry.getValue());
                }
                printMap(sortedMap);
                listRows = new ArrayAdapter<CharSequence>(PaymentGatewayList.this, R.layout.list_item, R.id.label, values);
                listRows.notifyDataSetChanged();
                paymentgatewayList.setAdapter(listRows);
            }
        });
        paymentgatewayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PaymentGatewayList.this, PaymentGatewayDetail.class);
                intent.putExtra("name", values[i]);
                startActivity(intent);
            }
        });



    }
    public void printMap(Map<String, Double> map) {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey()
                    + " Value : " + entry.getValue());
        }
    }
    private boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
    /*
    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "cube26pdf");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }



    public class FileDownloader {
        private static final int  MEGABYTE = 1024 * 1024;

        public void downloadFile(String fileUrl, File directory){
            try {

                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(directory);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

}
