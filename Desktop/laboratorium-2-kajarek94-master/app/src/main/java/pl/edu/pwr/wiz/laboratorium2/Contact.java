package pl.edu.pwr.wiz.laboratorium2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Contact extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
           String contactNumber = extras.getString("key");

           final TextView txtNumber = (TextView) findViewById(R.id.txtNumber);
           if (contactNumber != null) {
               txtNumber.setText(contactNumber);
           }

        }

    }

    public void onClick(View view) {
        finish();
    }
    @Override
    public void finish(){
        Intent data = new Intent();

        TextView text1 = (TextView) findViewById(R.id. text1);

        String returnString = text1.getText().toString();
        data.putExtra("powrot", returnString);
        setResult(RESULT_OK, data);
        super.finish();

    }
}

