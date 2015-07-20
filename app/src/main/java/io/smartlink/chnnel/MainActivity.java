package io.smartlink.chnnel;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;

public class MainActivity extends AppCompatActivity  implements MeteorCallback {

    DrawerLayout drawer;
    TabLayout tab;
    ViewPager vpPager;

    ListFragment fragment;

    private Meteor mMeteor;
    private JSONArray channels = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView nv = (NavigationView) findViewById(R.id.navigation_view);
        nv.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isCheckable()) {
                    menuItem.setChecked(true);
                }
                Toast.makeText(getApplicationContext(),
                        menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                return true;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        tab = (TabLayout) findViewById(R.id.tabs);
        tab.setupWithViewPager(vpPager);

        Meteor.setLoggingEnabled(true);

        mMeteor = new Meteor(this, "ws://chnnel.meteor.com/websocket");
        mMeteor.setCallback(this);
    }

    public JSONArray getChannels() {
        return channels;
    }

    @Override
    public void onConnect(boolean signedInAutomatically) {
        System.out.println("Connected");
        System.out.println("Is logged in: "+mMeteor.isLoggedIn());
        System.out.println("User ID: "+mMeteor.getUserId());

        String subscriptionId = mMeteor.subscribe("channels");
    }

    @Override
    public void onDisconnect(int code, String reason) {
        System.out.println("Disconnected");
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String newValuesJson) {
        System.out.println("Data added to <" + collectionName + "> in document <" + documentID + ">");
        System.out.println("    Added: " + newValuesJson);

        try {
            JSONObject ob = new JSONObject(newValuesJson);
            channels.put(ob);

            System.out.print(" CHANNEL Data is changed : " + ob);


            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();

        } catch (Throwable t) {
            System.out.println(" Error: Json - " + newValuesJson);
        }


    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        System.out.println("Data changed in <"+collectionName+"> in document <"+documentID+">");
        System.out.println("    Updated: "+updatedValuesJson);
        System.out.println("    Removed: "+removedValuesJson);
    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
        System.out.println("Data removed from <"+collectionName+"> in document <"+documentID+">");
    }

    @Override
    public void onException(Exception e) { }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_tab_fixed_fill:
                tab.setTabMode(TabLayout.MODE_FIXED);
                tab.setTabGravity(TabLayout.GRAVITY_FILL);
                return true;
            case R.id.action_tab_fixed_center:
                tab.setTabMode(TabLayout.MODE_FIXED);
                tab.setTabGravity(TabLayout.GRAVITY_CENTER);
                return true;
            case R.id.action_tab_scrollable:
                tab.setTabMode(TabLayout.MODE_SCROLLABLE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "List";
                case 1:
                    return "Widgets";
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    fragment = new ListFragment();
                    return fragment;
                case 1:
                    return new WidgetFragment();
            }
            throw new IndexOutOfBoundsException();
        }
    }
}
