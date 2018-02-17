package bulcina.bcpl;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PieprasijumaDzesanasDialogs extends DialogFragment{

    private Button btnAtcelt;
    private Button btnDzest;
    private TextView tvZinojums;

    BulcinaDatabaseHelper db;
    Toast toast;
    Context context;
    View view;
    int piepr_id;
    int bulc_id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.dzesanas_dialogs, null);
        context = getActivity().getApplicationContext();
        piepr_id = getArguments().getInt("piepr_id");
        bulc_id = getArguments().getInt("bulc_id");

        btnAtcelt = view.findViewById(R.id.dzes_dialogs_btn_atcelt);
        btnDzest = view.findViewById(R.id.dzes_dialogs_btn_dzest);
        tvZinojums = view.findViewById(R.id.dzes_dialogs_tv_zinojums);

        dialog.setContentView(view);

        tvZinojums.setText(R.string.dialoga_teksts_pieprasijuma_dzesana);

        btnAtcelt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnDzest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dzestPieprasijumu(piepr_id, bulc_id);
                dismiss();
            }
        });

        return dialog;
    }
    private void dzestPieprasijumu(int pieprasijums_id, int bulcina_id){
        db = BulcinaDatabaseHelper.getInstance(context);
        String strToast;
        int d = Toast.LENGTH_SHORT;

        db.deletePieprasijums(pieprasijums_id);
        try{
            db.updatePasreizejoPrognozi(bulcina_id,0);
        }
        catch (CursorIndexOutOfBoundsException cioobe){

        }
        try{
            db.updatePasreizejoPrognozi(bulcina_id,1);
        }
        catch (CursorIndexOutOfBoundsException cioobe){

        }

        strToast = getString(R.string.pazinojums_pieprasijums_izdzests);
        toast = Toast.makeText(context,strToast,d);
        toast.show();
    }
}
