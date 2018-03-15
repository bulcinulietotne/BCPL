package bulcina.bcpl;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HelpAdapter  extends BaseAdapter{

    String[] jautajumi;
    String[] atbildes;
    Context context;
    LayoutInflater inflater;

    public HelpAdapter(String[] jautajumi, String[] atbildes, Context context){
        super();
        this.jautajumi = jautajumi;
        this.atbildes = atbildes;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return jautajumi.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container){

        convertView = inflater.inflate(R.layout.help_row, null);

        TextView tvJautajums = convertView.findViewById(R.id.help_row_tv_jautajums);
        TextView tvAtbilde = convertView.findViewById(R.id.help_row_tv_atbilde);

        tvJautajums.setText(jautajumi[position]);
        tvAtbilde.setText(atbildes[position]);

        return convertView;
    }
}
