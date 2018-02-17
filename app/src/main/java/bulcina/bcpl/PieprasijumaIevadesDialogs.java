package bulcina.bcpl;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class PieprasijumaIevadesDialogs extends DialogFragment{

    private RadioButton rbDarbadiena;
    private RadioButton rbBrivdiena;
    private EditText etPieprasijums;
    private Button btnAtcelt;
    private Button btnSaglabat;

    BulcinaDatabaseHelper db;
    Toast toast;
    Context context;
    View view;
    int bulc_id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.pieprasijuma_ievades_dialogs, null);
        context = getActivity().getApplicationContext();
        bulc_id = getArguments().getInt("bulc_id");

        dialog.setContentView(view);
        dialog.setTitle(R.string.title_pieprasijuma_ievade);

        rbDarbadiena = view.findViewById(R.id.piepr_ievade_dialogs_rbtn_darb);
        rbBrivdiena = view.findViewById(R.id.piepr_ievade_dialogs_rbtn_briv);
        etPieprasijums = view.findViewById(R.id.piepr_ievade_dialogs_et_piepr);
        btnAtcelt = view.findViewById(R.id.piepr_ievade_dialogs_btn_atcelt);
        btnSaglabat = view.findViewById(R.id.piepr_ievade_dialogs_btn_saglabat);

        rbDarbadiena.setChecked(true);

        rbDarbadiena.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(rbBrivdiena.isChecked()){
                    rbDarbadiena.setChecked(true);
                    rbBrivdiena.setChecked(false);
                }
            }
        });

        rbBrivdiena.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(rbDarbadiena.isChecked()){
                    rbBrivdiena.setChecked(true);
                    rbDarbadiena.setChecked(false);
                }
            }
        });

        btnAtcelt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSaglabat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saglabatPieprasijumu();
            }
        });

          return dialog;
    }
    private void saglabatPieprasijumu(){
        String strPieprasijums = etPieprasijums.getText().toString();

        db = BulcinaDatabaseHelper.getInstance(context);

        String strToast;
        int d = Toast.LENGTH_SHORT;

        if(TextUtils.isEmpty(strPieprasijums)){
            etPieprasijums.setError(getString(R.string.kluda_nav_pieprasijuma));
        }
        else {
            try {
                int pieprasijums = Integer.parseInt(strPieprasijums);
                int darbadiena;
                if (rbDarbadiena.isChecked()){
                    darbadiena = 1;
                }
                else{
                    darbadiena = 0;
                }

                db.addPieprasijums(pieprasijums, darbadiena, bulc_id);
                strToast = getString(R.string.pazinojums_pieprasijums_ievade);
                toast = Toast.makeText(context,strToast,d);
                toast.show();
                dismiss();
            }
            catch (NumberFormatException nfs){
                strToast = getString(R.string.kluda_nepareiza_nauda);
                toast = Toast.makeText(context,strToast,d);
                toast.show();
            }
        }

    }
}
