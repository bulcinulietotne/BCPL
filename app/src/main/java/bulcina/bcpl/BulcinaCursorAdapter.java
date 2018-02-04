package bulcina.bcpl;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.Calendar;


public class BulcinaCursorAdapter extends ResourceCursorAdapter {

    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    String prognDiena;
    BulcinaDatabaseHelper db;


    public BulcinaCursorAdapter(Context context, int layout, Cursor cursor, int flags){
        super(context, layout, cursor, flags);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView ivAttels = view.findViewById(R.id.bulc_saraksts_row_image);
        TextView tvBulcNos = view.findViewById(R.id.bulc_saraksts_row_tv_bulc_nos);
        TextView tvPieprProgn = view.findViewById(R.id.bulc_saraksts_row_tv_piepr_prog);

        if (day == calendar.SATURDAY || day == calendar.SUNDAY){
            prognDiena = BulcinaDatabaseHelper.BULCINA_COLUMN_PROGNOZE_BRIVDIENA;
        }
        else {
            prognDiena = BulcinaDatabaseHelper.BULCINA_COLUMN_PROGNOZE_DARBADIENA;
        }

        int bulc_id= cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        //int attels_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_ATTELS);
        int bulcNos_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_NOSAUKUMS);
        int pieprProgn_index = cursor.getColumnIndexOrThrow(prognDiena);

        Double prognoze = cursor.getDouble(pieprProgn_index);

        String strPrognoze;

        if (prognoze == 0){
            strPrognoze = "Nav pietiekamu datu";
        }
        else if (prognoze<0){
            strPrognoze = "Nav ieteicams cept";
        }
        else {
            strPrognoze = "Cepam: " + String.valueOf(Math.round(prognoze));
        }

        try
        {
            db = BulcinaDatabaseHelper.getInstance(context);
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
        tvPieprProgn.setText(strPrognoze);

    }

}
