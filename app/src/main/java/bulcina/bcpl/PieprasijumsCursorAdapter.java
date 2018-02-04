package bulcina.bcpl;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PieprasijumsCursorAdapter extends ResourceCursorAdapter {

    SimpleDateFormat df;
    Date date;

    public PieprasijumsCursorAdapter(Context context, int layout, Cursor cursor, int flags){
        super(context, layout, cursor, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvPieprasijums_id = view.findViewById(R.id.piepr_vesture_row_tv_kolonna_pieprasijums_id);
        TextView tvDatums = view.findViewById(R.id.piepr_vesture_row_tv_kolonna_datums);
        TextView tvPieprasijums = view.findViewById(R.id.piepr_vesture_row_tv_kolonna_pieprasijums);
        TextView tvPrognoze = view.findViewById(R.id.piepr_vesture_row_tv_kolonna_prognoze);
        TextView tvDarbadiena = view.findViewById(R.id.piepr_vesture_row_tv_kolonna_darbadiena);
        //TextView tvBulcina_id = view.findViewById(R.id.piepr_vesture_row_tv_kolonna_bulcina_id);

        int pieprasijums_id_index = cursor.getColumnIndexOrThrow("_id");
        int datums_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.PIEPRASIJUMS_COLUMN_DATUMS);
        int pieprasijums_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.PIEPRASIJUMS_COLUMN_PIEPRASIJUMS);
        int prognoze_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.PIEPRASIJUMS_COLUMN_PROGNOZE);
        int darbadiena_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.PIEPRASIJUMS_COLUMN_DARBADIENA);
        //int bulcina_id_index = cursor.getColumnIndexOrThrow(BulcinaDatabaseHelper.BULCINA_COLUMN_ID);

        tvPieprasijums_id.setText(cursor.getString(pieprasijums_id_index));
        tvDatums.setText(timestampToLocalTime(cursor.getString(datums_index)));
        tvPieprasijums.setText(cursor.getString(pieprasijums_index));
        tvPrognoze.setText(String.valueOf(Math.round(cursor.getDouble(prognoze_index))));
        tvDarbadiena.setText(cursor.getString(darbadiena_index));
        //tvBulcina_id.setText(cursor.getString(bulcina_id_index));
    }

    public String timestampToLocalTime(String timestamp){
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = df.parse(timestamp);
        } catch (ParseException e) {

        }
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

}
