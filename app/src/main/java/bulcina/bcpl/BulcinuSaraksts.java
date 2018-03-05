package bulcina.bcpl;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class BulcinuSaraksts extends AppCompatActivity {

    private ListView list;

    BulcinaDatabaseHelper db;
    Cursor cursor;
    BulcinaCursorAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulcinu_saraksts);
        setTitle(R.string.title_bulcinu_saraksts);
        db = BulcinaDatabaseHelper.getInstance(this);
        list = findViewById(R.id.bulc_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(), BulcinaDetails.class);
                int bulc_id = (int) id;
                intent.putExtra("bulc_id", bulc_id);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_bulcinu_saraksts, menu);
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();
        refreshList();
    }

    public void refreshList(){
        cursor = db.getAllBulcinas();
        adapter = new BulcinaCursorAdapter(this, R.layout.bulcinu_saraksts_row ,cursor,0);
        list.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean permission;
        switch (item.getItemId()){
            case R.id.action_add:
                intent = new Intent(this, JaunaBulcina.class);
                startActivity(intent);
                return true;
            case R.id.action_atvert_parskatu:
                intent = new Intent(this, BulcinuParskats.class);
                startActivity(intent);
                return true;
            case R.id.action_export:
                permission = isStoragePermissionGranted();
                if (permission){
                    if(db.exportDB(getApplicationContext())){
                        Toast toast = Toast.makeText(this,getString(R.string.pazinojums_eksportesana),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        Toast toast = Toast.makeText(this,getString(R.string.pazinojums_eksportesana_neveiksmiga),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                return true;
            case R.id.action_import:
                permission = isStoragePermissionGranted();
                if(permission){
                    if(db.importDB(getApplicationContext())){
                        Toast toast = Toast.makeText(this,getString(R.string.pazinojums_importesana),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        Toast toast = Toast.makeText(this,getString(R.string.pazinojums_importesana_neveiksmiga),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {
                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("BCPL","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}
