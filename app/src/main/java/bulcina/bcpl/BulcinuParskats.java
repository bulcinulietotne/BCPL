package bulcina.bcpl;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by Davis on 2/12/2018.
 */

public class BulcinuParskats extends AppCompatActivity {

    private ListView list;
    BulcinaDatabaseHelper db;
    Cursor cursor;
    BulcinaParskatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulcinu_parskats);
        setTitle(R.string.title_bulcinu_parskats);
        db = BulcinaDatabaseHelper.getInstance(this);
        list = findViewById(R.id.bulc_parskats_list);

    }
    @Override
    public void onResume(){
        super.onResume();
        cursor = db.getAllBulcinas();
        adapter = new BulcinaParskatsAdapter(this, R.layout.bulcinu_parskats_row ,cursor,0);
        list.setAdapter(adapter);
    }
}
