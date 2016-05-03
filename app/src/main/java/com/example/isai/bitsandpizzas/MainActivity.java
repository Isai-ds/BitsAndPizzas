package com.example.isai.bitsandpizzas;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

public class MainActivity extends Activity {
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ShareActionProvider shareActionProvider;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position",currentPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        //Populate the list
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null){
            selectItem(0);
        }else{
            currentPosition = savedInstanceState.getInt("position");
            selectItem(currentPosition);
        }

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer){
            @Override
            public void onDrawerClosed (View v){
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened (View v){
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener(){
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getFragmentManager().findFragmentByTag("visible_fragment");
                if (fragment instanceof TopFragment) currentPosition=0;
                if (fragment instanceof PizzaFragment) currentPosition=1;
                if (fragment instanceof PastaFragment) currentPosition=2;
                if (fragment instanceof StoresFragment) currentPosition=3;

                setActionBarTitle(currentPosition);
                drawerList.setItemChecked(currentPosition,true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider)menuItem.getActionProvider();
        setIntent("This is a example text");

        return super.onCreateOptionsMenu(menu);
    }

    private void setIntent (String message){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,message);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        if (drawerToggle.onOptionsItemSelected(item)) return true;

        switch (item.getItemId()) {
            case R.id.action_create_order:
                Intent intent = new Intent(this,OrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void selectItem (int position){
        Fragment fragment;
        currentPosition = position;
        switch (position){
            case 1:
                fragment = new PizzaFragment();
            break;
            case 2:
                fragment = new PastaFragment();
            break;
            case 3:
                fragment = new StoresFragment();
            break;
            default:
                fragment = new TopFragment();
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,fragment,"visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        setActionBarTitle(position);

        //Close drawer
        drawerLayout.closeDrawer(drawerList);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void setActionBarTitle (int position){
        String title;
        if (position == 0) title = getResources().getString(R.string.app_name);
        else title = titles[position];

        getActionBar().setTitle(title);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
}
