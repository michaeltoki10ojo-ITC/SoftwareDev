package com.example.poster1019;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_PAGE = "current_page";

    private static final String PREFS_NAME = "movie_poster_settings";
    private static final String KEY_DARK_MODE = "dark_mode";

    //Tags to find each page's Fragment again in the FragmentManager.
    private static final String TAG_PAGE_MAIN = "page_main";
    private static final String TAG_PAGE_FAVORITES = "page_favorites";
    private static final String TAG_PAGE_FEATURED = "page_featured";
    private static final String TAG_PAGE_CONTACT = "page_contact";

    //drawer the side menu inside it, and the bottom bar.
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;

    private int currentPageId = R.id.nav_main;

    private PosterRepository posterRepository;

    private final PosterRepository.FavoritesListener favoritesBadgeListener =
            this::updateFavoritesBadge;

    //Updates the number shown on the Favorite List icon.
    private void updateFavoritesBadge() {

        int count = posterRepository.getFavoriteCount();

        BadgeDrawable badge =
                bottomNavigation.getOrCreateBadge(R.id.nav_favorites);

        badge.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorBadge));

        badge.setBadgeTextColor(
                ContextCompat.getColor(this, R.color.colorTextIcons));

        badge.setNumber(count);
        badge.setVisible(count > 0);
    }

    /**
     * Sets up the app pages, navigation, favorites badge, and dark mode setting.
     *
     * @param savedInstanceState saved app information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE);

        boolean darkMode =
                preferences.getBoolean(
                        KEY_DARK_MODE,
                        false);

        if (darkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        //find by id
        drawerLayout =
                findViewById(R.id.drawerLayout);

        navigationView =
                findViewById(R.id.navigationView);

        bottomNavigation =
                findViewById(R.id.bottomNavigation);

        MaterialToolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(
                drawerToggle);

        drawerToggle.syncState();

        //Set up the Dark Mode switch in the drawer header.
        View drawerHeader =
                navigationView.getHeaderView(0);

        SwitchMaterial switchDarkMode =
                drawerHeader.findViewById(
                        R.id.switchDarkMode);

        switchDarkMode.setChecked(
                darkMode);

        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    KEY_DARK_MODE,
                                    isChecked)
                            .apply();

                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES);

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO);
                    }
                });

        //The drawer and the bottom bar both call showPage().
        navigationView.setNavigationItemSelectedListener(
                item -> {

                    showPage(
                            item.getItemId());

                    return true;
                });

        bottomNavigation.setOnItemSelectedListener(
                item -> {

                    showPage(
                            item.getItemId());

                    return true;
                });

        //Window insets: status bar, navigation bar and camera cut-out.
        final AppBarLayout appBarLayout =
                findViewById(R.id.appBarLayout);

        final View fragmentContainer =
                findViewById(R.id.fragmentContainer);

        final int drawerHeaderPaddingTop =
                drawerHeader.getPaddingTop();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets bars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout());

                    appBarLayout.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            0);

                    fragmentContainer.setPadding(
                            bars.left,
                            0,
                            bars.right,
                            0);

                    drawerHeader.setPadding(
                            drawerHeader.getPaddingLeft(),
                            drawerHeaderPaddingTop + bars.top,
                            drawerHeader.getPaddingRight(),
                            drawerHeader.getPaddingBottom());

                    return insets;
                });

        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.fragmentContainer,
                            new PostersFragment(),
                            TAG_PAGE_MAIN)
                    .add(
                            R.id.fragmentContainer,
                            new FavoritesFragment(),
                            TAG_PAGE_FAVORITES)
                    .add(
                            R.id.fragmentContainer,
                            new FeaturedFragment(),
                            TAG_PAGE_FEATURED)
                    .add(
                            R.id.fragmentContainer,
                            new ContactFragment(),
                            TAG_PAGE_CONTACT)
                    .commitNow();

        } else {

            currentPageId =
                    savedInstanceState.getInt(
                            KEY_CURRENT_PAGE,
                            R.id.nav_main);
        }

        showPage(currentPageId);

        //Favorites badge: show current count and follow every change.
        posterRepository =
                PosterRepository.getInstance(this);

        posterRepository.addFavoritesListener(
                favoritesBadgeListener);

        updateFavoritesBadge();
    }

    /**
     * Removes the favorites listener when the activity closes.
     */
    @Override
    protected void onDestroy() {

        posterRepository.removeFavoritesListener(
                favoritesBadgeListener);

        super.onDestroy();
    }

    /**
     * Saves the current page when the screen rotates.
     *
     * @param outState stores the current page
     */
    @Override
    protected void onSaveInstanceState(
            @NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putInt(
                KEY_CURRENT_PAGE,
                currentPageId);
    }

    /**
     * Shows the selected page and hides the others.
     *
     * @param pageId selected navigation page
     */
    private void showPage(int pageId) {

        currentPageId = pageId;

        FragmentManager fragmentManager =
                getSupportFragmentManager();

        FragmentTransaction transaction =
                fragmentManager.beginTransaction();

        String visibleTag =
                tagForPage(pageId);

        for (String tag :
                new String[]{
                        TAG_PAGE_MAIN,
                        TAG_PAGE_FAVORITES,
                        TAG_PAGE_FEATURED,
                        TAG_PAGE_CONTACT}) {

            Fragment page =
                    fragmentManager.findFragmentByTag(tag);

            if (page == null) {
                continue;
            }

            if (tag.equals(visibleTag)) {

                transaction.show(page);

            } else {

                transaction.hide(page);
            }
        }

        transaction.commit();

        if (getSupportActionBar() != null) {

            getSupportActionBar()
                    .setTitle(
                            titleForPage(pageId));
        }

        navigationView.setCheckedItem(
                pageId);

        bottomNavigation
                .getMenu()
                .findItem(pageId)
                .setChecked(true);

        drawerLayout.closeDrawer(
                GravityCompat.START);
    }

    //Toolbar title for each page.
    private int titleForPage(int pageId) {

        if (pageId == R.id.nav_favorites) {

            return R.string.title_favorites;

        } else if (pageId == R.id.nav_featured) {

            return R.string.title_featured;

        } else if (pageId == R.id.nav_contact) {

            return R.string.title_contact;
        }

        return R.string.posters;
    }

    //Which Fragment tag belongs to each page.
    private String tagForPage(int pageId) {

        if (pageId == R.id.nav_favorites) {

            return TAG_PAGE_FAVORITES;

        } else if (pageId == R.id.nav_featured) {

            return TAG_PAGE_FEATURED;

        } else if (pageId == R.id.nav_contact) {

            return TAG_PAGE_CONTACT;
        }

        return TAG_PAGE_MAIN;
    }
}