package bulcina.bcpl;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class Help extends AppCompatActivity{

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTitle(R.string.title_help);

        String[] jautajumi = {
                getString(R.string.help_jauajums_1),
                getString(R.string.help_jauajums_2),
                getString(R.string.help_jauajums_3),
                getString(R.string.help_jauajums_4),
                getString(R.string.help_jauajums_5),
                getString(R.string.help_jauajums_6),
                getString(R.string.help_jauajums_7),
                getString(R.string.help_jauajums_8),
                getString(R.string.help_jauajums_9)
        };
        String[] atbildes = {
                getString(R.string.help_atbilde_1),
                getString(R.string.help_atbilde_2),
                getString(R.string.help_atbilde_3),
                getString(R.string.help_atbilde_4),
                getString(R.string.help_atbilde_5),
                getString(R.string.help_atbilde_6),
                getString(R.string.help_atbilde_7),
                getString(R.string.help_atbilde_8),
                getString(R.string.help_atbilde_9)
        };

        list = findViewById(R.id.help_list);
        list.setAdapter(new HelpAdapter(jautajumi, atbildes, this));
    }
}
