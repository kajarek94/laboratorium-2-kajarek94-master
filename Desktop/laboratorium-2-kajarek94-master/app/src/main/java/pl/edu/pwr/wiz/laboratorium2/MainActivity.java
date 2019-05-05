package pl.edu.pwr.wiz.laboratorium2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_SMS = 3;

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_CONTACT_DATA = 1;
    public static final int IMAGE_GALLERY_REQUEST = 20;
//    private static final int REQUEST_CONTACT_SMS = 2;

    private String number;
    Button sendButton;
    EditText txtNumber;
    TextView txtMessage;
    private ImageView imageView;
    //    Button b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sendButton = findViewById(R.id.sendButton);
        txtNumber = findViewById(R.id.txtNumber);
        txtMessage = findViewById(R.id.txtMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View view) {
                                              sendSMS();
                                          }
                                      }


        );
        //get a reference to the image view that holds the image that the user will see
        imageView = (ImageView) findViewById(R.id.viewImage);
    }

        public void onImageGalleryClicked(View v){
        //invoke the image gallery using an implicit intent
            Intent photoPickerIntent = new Intent (Intent.ACTION_PICK);

            //where do we want to find the data?
            File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String pictureDirectoryPath = pictureDirectory.getPath();
            //get a URI representation
            Uri data = Uri.parse(pictureDirectoryPath);

            //set the data and type (get all image types)
            photoPickerIntent.setDataAndType(data, "image/*");
            startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

        }



            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                return takePhoto();

            case R.id.action_contacts:
                return fetchContact();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Callback po nadaniu praw dostępu */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Dostęp nadany, uruchamiamy ponownie zrobienie zdjęcia
                    takePhoto();
                } else {
                    // Dostęp nie udany. Wyświetlamy Toasta
                    Toast.makeText(getApplicationContext(), R.string.access_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Dostęp nadany, uruchamiamy ponownie zrobienie zdjęcia
                    fetchContact();
                } else {
                    // Dostęp nie udany. Wyświetlamy Toasta
                    Toast.makeText(getApplicationContext(), R.string.access_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Dostęp nadany, uruchamiamy ponownie zrobienie zdjęcia
                    callNumber();
                } else {
                    // Dostęp nie udany. Wyświetlamy Toasta
                    Toast.makeText(getApplicationContext(), R.string.access_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_SMS: {
                // Dostęp nadany, uruchamiamy ponownie wysyłanie SMS-a
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                } else {
                    // Dostęp nie udany. Wyświetlamy Toasta
                    Toast.makeText(getApplicationContext(), R.string.access_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /* Callback do rezultatów z różnych wywoływanych aktywności */
    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                //if we are here, everything processed successfully
                if (requestCode == IMAGE_GALLERY_REQUEST){
                    //if we are here, we are hearing back from the image gallery

                    //the address of the image on the SD Card
                Uri imageURI = data.getData();

                //declare a stream to read the image data from SD Card
                    InputStream inputStream;

                    //we are gettin an input stream, based on the URI of the image
                    try {
                        inputStream = getContentResolver().openInputStream(imageURI);

                        //get a bitmap from the stream
                      Bitmap image =  BitmapFactory.decodeStream(inputStream);

                      //show the image to the user
                      imageView.setImageBitmap(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();

                    }

                }
            }


                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            /* Czyscimy aktualny content */
            ViewGroup contentMain = this.findViewById(R.id.content_main);
            contentMain.removeAllViews();

            /* Tworzymy nowy obrazek */
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(imageBitmap);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

            /* Dodajemy nowy obrazek do layoutu */
            contentMain.addView(imageView);
        } else if (requestCode == REQUEST_CONTACT_DATA && resultCode == RESULT_OK) {
                    /* Czyscimy aktualny content */
                    ViewGroup contentMain = this.findViewById(R.id.content_main);
                    contentMain.removeAllViews();

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);

                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        /* Tworzymy pole na nazwę */
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        TextView nameView = new TextView(this);
                        nameView.setText(name);

                        /* Ustawiamy parametry dla widoku */
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        nameView.setLayoutParams(params);

                        /* Dodajemy nowy tekst do layoutu */
                        contentMain.addView(nameView);

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            number = phones.getString(phones.getColumnIndex("data1"));

                            /* Tworzymy nowe pole tekstowe na numer */
                            TextView numberView = new TextView(this);
                            numberView.setText(number);

                            /* Ustawiamy parametry */
                            numberView.setLayoutParams(params);

                            /* Dodajemy do layoutu */
                            contentMain.addView(numberView);

                            /* Dodajemy przycisk do dzwonienia */
                            Button callBtn = new Button(this);
                            callBtn.setText(R.string.button_call);
                            callBtn.setLayoutParams(params);

                            /* Obsluga klikniecia w przycisk */
                            callBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    callNumber();
                                }
                            });

                            /* Dodajemy do layoutu */
                            contentMain.addView(callBtn);

                            /* Dodajemy przycisk do wysyłania SMS-ów */
                            Button smsButton = new Button(this);
                            smsButton.setText(R.string.button_sms);
                            smsButton.setLayoutParams(params);

                            /* Obsluga klikniecia w przycisk */
                            smsButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    sendSMS();
                                }

                            });

                            /* Dodajemy do layoutu */
                            contentMain.addView(smsButton);

                        }

                    }

                }
        }

            /* Pobieranie zdjęcia */
            private boolean takePhoto () {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);

                    return false;
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    return true;
                }

                return false;
            }

            /* Pobieranie kontaktu */
            private boolean fetchContact () {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_CONTACTS);

                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT_DATA);

                final TextView text1 = findViewById(R.id.text1);

                String nr = text1.getText().toString();
                intent.putExtra("key", nr);

                return true;
            }

            /* Dzwonienie do aktualnie wybranej osoby */
            private boolean callNumber () {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_PHONE);

                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);

                return true;
            }

            //Wysłanie SMS-a
            protected void sendSMS () {
                number = txtNumber.getText().toString();

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SMS);
                    }
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("tel:" + number));
                    startActivity(intent);

                }

                String smsBody = "Spóźnię się na spotkanie.";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, smsBody, null, null);

                String SMS_SENT = "SMS_SENT";
                String SMS_DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 3, new Intent(SMS_SENT), 0);
                PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 3, new Intent(SMS_DELIVERED), 0);

                ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);
                ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
                ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

                for (int i = 0; i < smsBodyParts.size(); i++) {
                    sentPendingIntents.add(sentPendingIntent);
                    deliveredPendingIntents.add(deliveredPendingIntent);
                }


// For when the SMS has been sent
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_DELIVERED));

// Send a text based SMS
            smsManager.sendMultipartTextMessage(number, null, smsBodyParts, sentPendingIntents, deliveredPendingIntents);
        }
    }
