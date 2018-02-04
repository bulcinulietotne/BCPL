package bulcina.bcpl;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JaunaBulcina extends AppCompatActivity{

    private ImageButton ibtnAttels;
    private EditText etBulcNos;
    private EditText etPasizmaksa;
    private EditText etRealizacija;
    private EditText etNerealizetais;
    private Button btnSaglabat;

    BulcinaDatabaseHelper db;
    Toast toast;
    Context context;
    Cursor cursor;
    int bulc_id = 0;
    Bitmap bitmap;
    byte[] attels;
    String strToast;
    int d = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jauna_bulcina);

        context = getApplicationContext();

        setTitle("Jauna bulciņa");

        ibtnAttels = findViewById(R.id.jauna_bulc_ibtn_attels);
        etBulcNos = findViewById(R.id.jauna_bulc_et_nos);
        etPasizmaksa = findViewById(R.id.jauna_bulc_et_pasizmaksa);
        etRealizacija = findViewById(R.id.jauna_bulc_et_realizacija);
        etNerealizetais = findViewById(R.id.jauna_bulc_et_nerealizetais);
        btnSaglabat = findViewById(R.id.jauna_bulc_btn_save);

        ibtnAttels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(takePicture, 0);//zero can be replaced with any action code

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });

        btnSaglabat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saglabatBulcinu();
            }
        });

        try {
            bulc_id = getIntent().getExtras().getInt("bulc_id");
        }
        catch (NullPointerException npe){
            Log.e("BCPL","Nevarēja iegūt bulc_id.", npe);
        }

        if (bulc_id > 0){

            setTitle("Bulciņas rediģēšana");

            db = BulcinaDatabaseHelper.getInstance(this);

            cursor = db.getBulcina(bulc_id);

            cursor.moveToFirst();

            String strBulcNos = cursor.getString(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_NOSAUKUMS));
            String strPasizmaksa = String.format("%.2f", cursor.getDouble(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_PASIZMAKSA)));
            String strRealizacija = String.format("%.2f", cursor.getDouble(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_REALIZACIJA)));
            String strNerealizetais = String.format("%.2f", cursor.getDouble(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_NEREALIZETAIS)));

            try {
                attels = cursor.getBlob(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_ATTELS));

                if (attels != null){
                    ibtnAttels.setBackgroundColor(0);
                    Bitmap bmpAttels = BitmapFactory.decodeByteArray(attels, 0, attels.length);
                    ibtnAttels.setImageBitmap(bmpAttels);
                }
            }
            catch (Exception e){
                Log.e("BCPL","Kļūda attēla parādīšanā.",e);
            }
            cursor.close();

            etBulcNos.setText(strBulcNos);
            etPasizmaksa.setText(strPasizmaksa);
            etRealizacija.setText(strRealizacija);
            etNerealizetais.setText(strNerealizetais);
        }
    }
    private void saglabatBulcinu(){
        String strBulcNos = etBulcNos.getText().toString();
        String strPasizmaksa = etPasizmaksa.getText().toString();
        String strRealizacija = etRealizacija.getText().toString();
        String strNerealizetais = etNerealizetais.getText().toString();

        db = BulcinaDatabaseHelper.getInstance(this);


        if(TextUtils.isEmpty(strBulcNos)){
            etBulcNos.setError("Bulciņas nosaukums nevar būt tukšs.");
        }
        else {
            if(TextUtils.isEmpty(strPasizmaksa) || TextUtils.isEmpty(strRealizacija) || TextUtils.isEmpty(strNerealizetais)){
                strToast = "Naudas vērtības nevar būt tukšas.";
                toast = Toast.makeText(context,strToast,d);
                toast.show();
            }
            else{
                try{
                    double pasizmaksa = Double.valueOf(strPasizmaksa);
                    double realizacija = Double.valueOf(strRealizacija);
                    double nerealizetais = Double.valueOf(strNerealizetais);
                    if (pasizmaksa>=realizacija){
                        strToast = "Bulciņas cepšanas izmaksām jābūt mazākām par cenu.";
                        toast = Toast.makeText(context,strToast,d);
                        toast.show();
                    }
                    else if (nerealizetais>=pasizmaksa){
                        strToast = "Bulciņas cepšanas izmaksām jābūt lielākām par pārpalikuma cenu.";
                        toast = Toast.makeText(context,strToast,d);
                        toast.show();
                    }
                    else if (bulc_id > 0){
                        if(db.updateBulcina(bulc_id,strBulcNos,pasizmaksa,realizacija,nerealizetais,attels)){
                            db.updatePasreizejoPrognozi(bulc_id,1);
                            db.updatePasreizejoPrognozi(bulc_id,0);
                            strToast = "Bulciņas rediģēšana ir veiksmīga.";
                            toast = Toast.makeText(context,strToast,d);
                            toast.show();
                        }
                        else {
                            strToast = "Bulciņas rediģēšana nav veiksmīga.";
                            toast = Toast.makeText(context,strToast,d);
                            toast.show();
                        }
                        finish();
                    }
                    else{
                        if(db.addBulcina(strBulcNos,pasizmaksa,realizacija,nerealizetais,attels)){
                            strToast = "Bulciņas ievade ir veiksmīga.";
                            toast = Toast.makeText(context,strToast,d);
                            toast.show();
                        }
                        else {
                            strToast = "Bulciņas ievade nav veiksmīga.";
                            toast = Toast.makeText(context,strToast,d);
                            toast.show();
                        }
                        finish();
                    }
                }
                catch (NumberFormatException nfs){
                    strToast = "Ievadīta nepareiza naudas vērtība!";
                    toast = Toast.makeText(context,strToast,Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ibtnAttels.setBackgroundColor(0);
                    ibtnAttels.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try{
                        ibtnAttels.setBackgroundColor(0);
                        bitmap = ImageUtility.decodeSampledBitmapFromByteArray(noUriUzByteArray(selectedImage),400,300);
                        ibtnAttels.setImageBitmap(bitmap);
                        attels = noBitmapUzByteArray(bitmap);
                    }
                    catch (Exception e){
                        Log.e("BCPL","Kļūda attēla uzstādīšanā.",e);
                    }
                }
                break;
        }
    }

    public byte[] noUriUzByteArray (Uri uri) throws IOException{
        InputStream iStream =   getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        try{
            while ((len = iStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        }
        catch (NullPointerException npe){
            Log.e("BCPL","Kļūda lasot ievades plūsmu.",npe);
        }

        return byteBuffer.toByteArray();
    }

    public byte[] noBitmapUzByteArray (Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


}
