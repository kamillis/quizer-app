package pl.kamillis.quizy;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import pl.kamillis.quizy.fragments.QuizCreatorFragment;
import pl.kamillis.quizy.fragments.QuizFragment;
import pl.kamillis.quizy.fragments.QuizzesListFragment;
import pl.kamillis.quizy.fragments.TagsListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        QuizFragment.QuizListener,
        QuizzesListFragment.QuizzesListListener,
        TagsListFragment.TagsListListener,
        QuizCreatorFragment.QuizCreatorListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set drawer
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Set navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set main fragment
        if (savedInstanceState == null) {
            setMainFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_list_quiz:
                replaceFragment(new QuizzesListFragment());
                break;
            case R.id.nav_list_tag:
                replaceFragment(new TagsListFragment());
                break;
            case R.id.nav_add_quiz:
                replaceFragment(new QuizCreatorFragment());
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onQuizCreated(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        Fragment fragment = new QuizFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    @Override
    public void onQuizSelected(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        Fragment fragment = new QuizFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    @Override
    public void onTagSelected(String name) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", name);
        Fragment fragment = new QuizzesListFragment();
        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    private void setMainFragment() {
        Fragment fragment = new QuizzesListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
