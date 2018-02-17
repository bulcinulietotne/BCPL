package bulcina.bcpl;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BulcinasDzesanasDialogs extends DialogFragment{

    private Button btnAtcelt;
    private Button btnDzest;
    private TextView tvZinojums;

    BulcinaDatabaseHelper db;
    Toast toast;
    View view;
    Context context;
    int bulc_id;
    boolean parbaude;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.dzesanas_dialogs, null);
        context = getActivity().getApplicationContext();
        bulc_id = getArguments().getInt("bulc_id");

        dialog.setContentView(view);

        btnAtcelt = view.findViewById(R.id.dzes_dialogs_btn_atcelt);
        btnDzest = view.findViewById(R.id.dzes_dialogs_btn_dzest);
        tvZinojums = view.findViewById(R.id.dzes_dialogs_tv_zinojums);

        tvZinojums.setText(R.string.dialoga_teksts_bulcinas_dzesana);

        btnAtcelt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnDzest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!parbaude){
                    tvZinojums.setText(R.string.dialoga_teksts_bulcinas_dzesana_apstiprinajums);
                    parbaude = true;
                }
                else {
                    dzestBulcinu(bulc_id);
                    dismiss();
                }
            }
        });

        return dialog;
    }

    private void dzestBulcinu(int bulcina_id){
        db = BulcinaDatabaseHelper.getInstance(context);
        String strToast;
        int d = Toast.LENGTH_SHORT;

        db.deleteBulcina(bulcina_id);
        getActivity().finish();

        strToast = getString(R.string.pazinojums_bulcina_izdzesta);
        toast = Toast.makeText(context,strToast,d);
        toast.show();
    }

}
