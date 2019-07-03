package bee.read.abanob.prog.com.hospitalmangment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.glide.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;
import java.util.Arrays;

import bee.read.abanob.prog.com.hospitalmangment.Fragment.AboutFragment;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.BloodBank;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.DecrementBlood;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.IncrementBlood;
import bee.read.abanob.prog.com.hospitalmangment.Fragment.Orders;
import cn.pedant.SweetAlert.SweetAlertDialog;
 import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class MainActivity extends AppCompatActivity
        implements
        ViewPagerEx.OnPageChangeListener ,DuoMenuView.OnMenuClickListener{
    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    private ArrayList<String> mTitles = new ArrayList<>();
    PrefManager prefManager;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Cairo-Regular.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_main);
        prefManager=new PrefManager(MainActivity.this);



        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle toolbar actions
        handleToolbar();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        // Show main fragment in container
        goToFragment(new IncrementBlood(), false);
        mMenuAdapter.setViewSelected(0, true);
        setTitle("Increment Blood");
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);

        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }





    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //  if (drawer.isDrawerOpen(GravityCompat.START)) {
        //     drawer.closeDrawer(GravityCompat.START);
        //  } else {
        super.onBackPressed();
        //  }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFooterClicked() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.sure))
                .setContentText(getResources().getString(R.string.logout))
                .setConfirmText(getResources().getString(R.string.dialog_ok))
                .setCancelText(getResources().getString(R.string.dialog_cancel))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        prefManager.LogedOut();
                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        })
                .show();
    }

    @Override
    public void onHeaderClicked() {

    }
    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.container, fragment).commit();
    }
    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title
        // setTitle(mTitles.get(position));

        // Set the right options selected
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {
            case 0:
                goToFragment(new IncrementBlood(), false);
                setTitle("Increment Blood");
                break;
            case 1:
                goToFragment(new DecrementBlood(), false);
                setTitle("Decrement Blood");

                break;
            case 2:
                goToFragment(new Orders(), false);
                setTitle("Orders");

                break;
            case 3:
                goToFragment(new BloodBank(), false);
                setTitle("Blood Bank");

                break;
            case 4:
                goToFragment(new AboutFragment(), false);
                setTitle("About");

                break;
            default:
                goToFragment(new IncrementBlood(), false);
                setTitle("Increment Blood");

                break;
        }

        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }
    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            TextView title= mDuoMenuView.getHeaderView().findViewById(R.id.duo_view_header_text_title);
            title.setText(prefManager.getHospital().getName());
            TextView subTitle= mDuoMenuView.getHeaderView().findViewById(R.id.duo_view_header_text_sub_title);
            subTitle.setText(prefManager.getHospital().getCreatedAt());

        }
    }
    private void startActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
