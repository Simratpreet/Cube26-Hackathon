package simrat.cube26;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import simrat.cube26.data.Cube26DbHelper;
import simrat.cube26.model.PaymentGateway;

/**
 * Created by simrat on 12/3/16.
 */
public class SearchActivity extends AppCompatActivity {
    private String DEBUG_TAG = this.getClass().getName();
    private ListView searchResults;
    private Cube26DbHelper dbHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        searchResults = (ListView) findViewById(R.id.searchResults);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(DEBUG_TAG, query);
            doMySearch(query);
        }

    }
    private void doMySearch(String query){
        dbHelper = new Cube26DbHelper(getApplicationContext());
        final String[] values = dbHelper.searchQuery(query);
        if(values != null){
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.list_item, R.id.label, values);
            searchResults.setAdapter(adapter);
            searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(SearchActivity.this, PaymentGatewayDetail.class);
                    intent.putExtra("name", values[i]);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else{
            Toast.makeText(SearchActivity.this, "No payment gateway found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SearchActivity.this, PaymentGatewayList.class);
            startActivity(intent);
            finish();
        }
    }
}
