package bulcina.bcpl;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;


public class BulcinaDetails extends AppCompatActivity{

    private ImageView ivAttels;
    private TextView tvBulcNos;
    private TextView tvPasizmaksa;
    private TextView tvRealizacija;
    private TextView tvNerealizetais;
    private Button btnIevaditPieprasijumu;
    private Button btnApskatitPieprasijumaVesturi;
    private TextView tvPasizmaksaLabel;
    private TextView tvRealizacijaLabel;
    private TextView tvNerealizetaisLabel;

    BulcinaDatabaseHelper db;
    Cursor cursor;
    DialogFragment dialog;
    Bundle bundle;
    Intent intent;
    int bulc_id;
    Animation move_up_button_anim, move_down_button_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulcina_details);

        setTitle(R.string.title_bulcina_details);

        ivAttels = findViewById(R.id.bulc_det_image);
        tvBulcNos = findViewById(R.id.bulc_det_tv_nos);
        tvPasizmaksa = findViewById(R.id.bulc_det_tv_pasizmaksa);
        tvRealizacija = findViewById(R.id.bulc_det_tv_realizacija);
        tvNerealizetais = findViewById(R.id.bulc_det_tv_nerealizetais);
        btnIevaditPieprasijumu = findViewById(R.id.bulc_det_btn_ievadit_piepr);
        btnApskatitPieprasijumaVesturi = findViewById(R.id.bulc_det_btn_apskatit_piepr_vesturi);
        tvNerealizetaisLabel = findViewById(R.id.bulc_det_tv_nerealizetais_label);
        tvPasizmaksaLabel = findViewById(R.id.bulc_det_tv_pasizmaksa_label);
        tvRealizacijaLabel = findViewById(R.id.bulc_det_tv_realizacija_label);

        move_up_button_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move);
        move_down_button_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move_down);

        startSlideUpAnimation();
        startSlideDownAnimation();

        btnIevaditPieprasijumu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle = new Bundle();
                bundle.putInt("bulc_id", bulc_id);
                dialog = new PieprasijumaIevadesDialogs();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "pieprasijuma ievade");
            }
        });

        btnApskatitPieprasijumaVesturi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), PieprasijumaVesture.class);
                intent.putExtra("bulc_id", bulc_id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
            }
        });

        db = BulcinaDatabaseHelper.getInstance(this);

        try{
            bulc_id = getIntent().getExtras().getInt("bulc_id");
        }
        catch (NullPointerException npe){
            Log.e("BCPL","Kluda bulc_id iegusana.",npe);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        cursor = db.getBulcina(bulc_id);

        cursor.moveToFirst();

        String strBulcNos = cursor.getString(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_NOSAUKUMS));
        String strPasizmaksa = String.format(Locale.US,"%.2f €",cursor.getDouble(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_PASIZMAKSA)));
        String strRealizacija = String.format(Locale.US,"%.2f €",cursor.getDouble(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_REALIZACIJA)));
        String strNerealizetais = String.format(Locale.US,"%.2f €",cursor.getDouble(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_NEREALIZETAIS)));

        try {
            byte[] attels = cursor.getBlob(cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_ATTELS));

            if (attels != null){
                Bitmap bmpAttels = BitmapFactory.decodeByteArray(attels, 0, attels.length);
                ivAttels.setImageBitmap(bmpAttels);
            }
        }
        catch (Exception e){
            Log.e("BCPL","Kluda attela paradisana.",e);
        }
        cursor.close();

        tvBulcNos.setText(strBulcNos);
        tvPasizmaksa.setText(strPasizmaksa);
        tvRealizacija.setText(strRealizacija);
        tvNerealizetais.setText(strNerealizetais);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_bulcina_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            intent = new Intent(getApplicationContext(), JaunaBulcina.class);
            intent.putExtra("bulc_id", bulc_id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
            return true;
        }
        else{
            if (id == R.id.action_delete){
                bundle = new Bundle();
                bundle.putInt("bulc_id", bulc_id);
                dialog = new BulcinasDzesanasDialogs();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "bulcinas dzesana");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void startSlideUpAnimation (){
        btnApskatitPieprasijumaVesturi.startAnimation(move_up_button_anim);
        btnIevaditPieprasijumu.startAnimation(move_up_button_anim);
        tvBulcNos.startAnimation(move_up_button_anim);
        tvNerealizetais.startAnimation(move_up_button_anim);
        tvPasizmaksa.startAnimation(move_up_button_anim);
        tvRealizacija.startAnimation(move_up_button_anim);
        tvPasizmaksaLabel.startAnimation(move_up_button_anim);
        tvRealizacijaLabel.startAnimation(move_up_button_anim);
        tvNerealizetaisLabel.startAnimation(move_up_button_anim);

    }
    public void startSlideDownAnimation (){
        ivAttels.startAnimation(move_down_button_anim);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out,R.anim.slide_in);
    }
}
