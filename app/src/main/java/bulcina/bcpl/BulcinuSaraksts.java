package bulcina.bcpl;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class BulcinuSaraksts extends AppCompatActivity {

    private ListView list;

    BulcinaDatabaseHelper db;
    Cursor cursor;
    BulcinaCursorAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulcinu_saraksts);
        setTitle(R.string.title_bulcinu_saraksts);
        db = BulcinaDatabaseHelper.getInstance(this);
        list = findViewById(R.id.bulc_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(), BulcinaDetails.class);
                int bulc_id = (int) id;
                intent.putExtra("bulc_id", bulc_id);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_bulcinu_saraksts, menu);
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();
        cursor = db.getAllBulcinas();
        adapter = new BulcinaCursorAdapter(this, R.layout.bulcinu_saraksts_row ,cursor,0);
        list.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, JaunaBulcina.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
