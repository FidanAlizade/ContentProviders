package com.example.contentproviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.contentproviders.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    ListView listView;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        listView = findViewById(R.id.listView);

        //1)
        //1. proqram acilan kimi izin verilmeyibse izin istensin
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS},1);
        }


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                //3)
                //1. eger izin verilibse content resolver sorgu yaratsin cursor verileri oxuyub liste add etsin
                //2. ve array adapterle baglasin ekran listine
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    ContentResolver contentResolver = getContentResolver();
                    //bu string dizisi icerisinde hangi columns- lari almaq isteyirikse onlari yazacagiq
                    String [] projection = {ContactsContract.Contacts.DISPLAY_NAME};
                    //selection filtrdi ona null vee bilerik
                    //selection args(meselen Where)) da null du eger selection nulldursa
                    //sortOrder dizi neye gore duzulsun
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null,
                            ContactsContract.Contacts.DISPLAY_NAME);
                    if (cursor != null){
//verileri alip bir contact Liste koyacaz sonra onu da olusturdugumuz Listeye baglayacaz
                        ArrayList<String> contactList = new ArrayList<String>();
                        String columnIx = ContactsContract.Contacts.DISPLAY_NAME;
                        while (cursor.moveToNext()){
                            contactList.add(cursor.getString(cursor.getColumnIndex(columnIx)));
                        }
                        cursor.close();

//listenin her bir itemlerini ozunde tuta bilecek bir row xml olmadigina gore androidin ozunun
//yaratdigi android.R.layout.simple_list_item_1 i istifade edirik. Ama istesek layoutda onu custom da yarada bileridik
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,contactList);
                        listView.setAdapter(arrayAdapter);
                    }

                }
                //2)
                //1. fab butonuna tiklandiqda izin verilmeyibse snackbar yaransin
                //2. izin isteme mentiqi gosterilmelidirse gosterilsin ve button qoysun,
                //3. gosterilmelii deyilse app-in settingsine gondersin
                else{
                    Snackbar.make(view,"Permission needed", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)){
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_CONTACTS},1);
                            }
                            else{

                                Intent intent = new Intent();

                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(),null);
                                intent.setData(uri);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                    }).show();
                }
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}