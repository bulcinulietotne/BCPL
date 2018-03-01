package bulcina.bcpl;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Davis on 2/23/2018.
 */

public class BulcinaParskatsAdapter extends ResourceCursorAdapter {

    BulcinaDatabaseHelper db;
    double pelna;
    double apjoms;
    double precizitate;

    public BulcinaParskatsAdapter(Context context, int layout, Cursor cursor, int flags){
        super(context, layout, cursor, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView ivAttels = view.findViewById(R.id.bulc_parskats_row_image);
        TextView tvBulcNos = view.findViewById(R.id.bulc_parskats_row_tv_bulc_nos);
        TextView tvPardotaisApjoms = view.findViewById(R.id.bulc_parskats_row_tv_pardotais_apjoms);
        TextView tvPelna = view.findViewById(R.id.bulc_parskats_row_tv_pelna);
        TextView tvPrecizitate = view.findViewById(R.id.bulc_parskats_row_tv_prognozes_precizitate);

        int bulc_id= cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        int bulcNos_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_NOSAUKUMS);

        db = BulcinaDatabaseHelper.getInstance(context);

       pelna = db.getBulcinasPelnu(bulc_id);
       apjoms = db.getBulcinasApjoms(bulc_id);
       precizitate = db.getPrognozesPrecizitati(bulc_id);


        try
        {
            Cursor c = db.getBulcinaAttels(bulc_id);

            c.moveToFirst();
            byte[] attels = c.getBlob(0);
            if (attels != null){
                Bitmap bmpAttels = ImageUtility.decodeSampledBitmapFromByteArray(attels,90,68);
                ivAttels.setImageBitmap(bmpAttels);
            }
            c.close();
        }
        catch (Exception e){
            Log.e("BCPL", "Kļūda attēla parādīšanā bulciņai: " + cursor.getString(bulcNos_index),e);
        }

        tvBulcNos.setText(cursor.getString(bulcNos_index));


        String s_pelna = String.format(Locale.US,"%.2f €",pelna);
        String s_precizitate = String.format(Locale.US,"%.2f %%",precizitate);

        tvPelna.setText(s_pelna);
        tvPardotaisApjoms.setText(toString().valueOf(apjoms));
        tvPrecizitate.setText(toString().valueOf(s_precizitate));


    }
}
