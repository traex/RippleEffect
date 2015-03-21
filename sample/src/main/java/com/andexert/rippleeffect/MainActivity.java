package com.andexert.rippleeffect;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.library.RippleView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
{
    private final Boolean isRecyclerview = false;
    private ArrayList<String> sourcesArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isRecyclerview)
            setContentView(R.layout.activity_main_recycler);
        else
            setContentView(R.layout.activity_main_list);

        final RippleView rippleView = (RippleView) findViewById(R.id.rect);
        final TextView textView = (TextView) findViewById(R.id.rect_child);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);

        setSupportActionBar(toolbar);

        rippleView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Sample", "Click Rect !");
            }
        });
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Log.d("Sample", "Ripple completed");
            }
        });
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Sample", "Click rect child !");
            }
        });

        sourcesArrayList.add("Samsung");
        sourcesArrayList.add("Android");
        sourcesArrayList.add("Google");
        sourcesArrayList.add("Asus");
        sourcesArrayList.add("Apple");
        sourcesArrayList.add("Samsung");
        sourcesArrayList.add("Android");
        sourcesArrayList.add("Google");
        sourcesArrayList.add("Asus");
        sourcesArrayList.add("Apple");
        sourcesArrayList.add("Samsung");
        sourcesArrayList.add("Android");
        sourcesArrayList.add("Google");
        sourcesArrayList.add("Asus");
        sourcesArrayList.add("Apple");


        if (isRecyclerview)
        {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            CustomAdapter customAdapter = new CustomAdapter();
            customAdapter.updateList(sourcesArrayList);

            customAdapter.setOnTapListener(new OnTapListener()
            {
                @Override
                public void onTapView(int position)
                {
                    Log.e("MainActivity", "Tap item : " + position);
                }
            });
            recyclerView.setAdapter(customAdapter);
        }
        else
        {
            ListView listView = (ListView) findViewById(R.id.listview);
            CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(this);
            customListViewAdapter.updateList(sourcesArrayList);
            listView.setAdapter(customListViewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Log.e("MainActivity", "ListView tap item : " + position);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
