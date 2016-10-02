package com.rxmapple.mytools;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final int UNREAD_EVENT_ITEM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        initListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAbout();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu( menu );
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case UNREAD_EVENT_ITEM:
                startActivity(new Intent( this, UnreadEventTestActivity.class));
                break;
            default:
                break;
        }
    }

    private void initListView () {
        ListView list = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>( this,
                R.layout.simple_list_item, getResources().getStringArray( R.array.main_list ) );

        if (list != null ) {
            list.setAdapter(arrayAdapter);
            list.setOnItemClickListener(this);
        }
    }

    private void showAbout () {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setMessage(R.string.about_me_msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_app_icon)
                .show();
    }
}
