package simrat.cube26;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import simrat.cube26.data.Cube26DbHelper;

/**
 * Created by simrat on 12/3/16.
 */
public class LikedGateways extends AppCompatActivity {
    private TextView headerText;
    private ListView likes;
    private Cube26DbHelper dbHelper;
    private ImageView headerIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liked_gateways);

        headerText = (TextView) findViewById(R.id.headerText);
        headerIcon = (ImageView) findViewById(R.id.headerIcon);

        headerIcon.setBackgroundResource(R.drawable.like);
        likes = (ListView) findViewById(R.id.likedGateways);

        dbHelper = new Cube26DbHelper(getApplicationContext());
        headerText.setText("LIKES");
        final String[] values = dbHelper.likes();
        if(values != null){
            ArrayAdapter<CharSequence> listRows;
            listRows = new ArrayAdapter<CharSequence>(LikedGateways.this, R.layout.list_item, R.id.label, values);
            likes.setAdapter(listRows);
            likes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(LikedGateways.this, PaymentGatewayDetail.class);
                    intent.putExtra("name", values[i]);
                    startActivity(intent);
                }
            });
        }
        else{
            Toast.makeText(LikedGateways.this, "No Liked gateways", Toast.LENGTH_SHORT).show();
        }
    }
}
