package bulcina.bcpl;


import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

public class PieprasijumaVesture extends AppCompatActivity{

    private ListView list;

    BulcinaDatabaseHelper db;
    PieprasijumsCursorAdapter adapter;
    Cursor cursor;
    Bundle bundle;
    DialogFragment dialog;
    int bulc_id;
    String datums = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieprasijuma_vesture);
        setTitle(R.string.title_pieprasijuma_vesture);
        db = BulcinaDatabaseHelper.getInstance(this);
        list = findViewById(R.id.piepr_list);
        bulc_id = getIntent().getExtras().getInt("bulc_id");

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                bundle = new Bundle();
                int piepr_id = (int) l;
                bundle.putInt("piepr_id", piepr_id);
                bundle.putInt("bulc_id", bulc_id);
                dialog = new PieprasijumaDzesanasDialogs();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "pieprasijuma dzesana");
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_pieprasijuma_vesture, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setQueryHint(getString(R.string.teksts_datums));
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mekletPecDatuma(newText);
                datums = newText;
                return false;
            }
        });
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add){
            bundle = new Bundle();
            bundle.putInt("bulc_id", bulc_id);
            dialog = new PieprasijumaIevadesDialogs();
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "pieprasijuma ievade");
        }
        else {
            if (id == R.id.action_delete) {
                bundle = new Bundle();
                bundle.putInt("bulc_id", bulc_id);
                dialog = new PieprasijumuVesturesDzesanasDialogs();
                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "visa pieprasijuma dzesana");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        mekletPecDatuma(datums);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mekletPecDatuma(datums);
    }

    void mekletPecDatuma(final String query) {
        cursor = db.mekletPecDatuma(bulc_id, query);
        adapter = new PieprasijumsCursorAdapter(this, R.layout.pieprasijuma_vesture_row,cursor,0);
        list.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out,
                R.anim.slide_in);
    }

}
