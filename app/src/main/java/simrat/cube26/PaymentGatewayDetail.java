package simrat.cube26;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import simrat.cube26.data.Cube26DbHelper;
import simrat.cube26.model.PaymentGateway;

/**
 * Created by simrat on 12/3/16.
 */
public class PaymentGatewayDetail extends AppCompatActivity {
    private TextView name;
    private TextView rating;
    private TextView transFees;
    private TextView branding;
    private TextView headerText;
    private TextView description;
    private TextView currencies;
    private Button likeButton;
    private Button linkButton;
    private Button backButton;
    private ImageView headerIcon;
    private Cube26DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_gateway_detail);

        dbHelper = new Cube26DbHelper(getApplicationContext());
        name = (TextView) findViewById(R.id.name);
        rating = (TextView) findViewById(R.id.rating);
        transFees = (TextView) findViewById(R.id.trans_fees);
        branding = (TextView) findViewById(R.id.branding);
        description = (TextView) findViewById(R.id.description);
        currencies = (TextView) findViewById(R.id.currencies);
        likeButton = (Button) findViewById(R.id.likeButton);
        headerText = (TextView) findViewById(R.id.headerText);
        linkButton = (Button) findViewById(R.id.linkButton);
        backButton = (Button) findViewById(R.id.backButton);
        headerIcon = (ImageView) findViewById(R.id.headerIcon);

        headerText.setText("DETAIL");
        headerIcon.setBackgroundResource(R.drawable.detail);
        Intent data = getIntent();
        name.setText(data.getExtras().getString("name", ""));

        final PaymentGateway paymentGateway = dbHelper.getPaymentGateway(name.getText().toString());
        rating.setText("RATING : " + paymentGateway.getRating());
        if(paymentGateway.getBranding().equals("0"))
                branding.setText("BRANDING : NO" );
        else branding.setText("BRANDING : YES");
        transFees.setText("TRANSACTION FEES : " + paymentGateway.getTrans_fee());
        currencies.setText("SUPPORTED CURRENCIES : " + paymentGateway.getCurrencies());
        description.setText(paymentGateway.getDescription());

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.updateLikes(name.getText().toString()))
                    Toast.makeText(PaymentGatewayDetail.this, "Liked", Toast.LENGTH_SHORT).show();
            }
        });

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentGatewayDetail.this, WebLink.class);
                intent.putExtra("url", paymentGateway.getHow_to_url());
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentGatewayDetail.this, PaymentGatewayList.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
