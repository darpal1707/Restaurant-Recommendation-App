/**
 * @author Pedro Torres
 * @since 01/17/18
 */

package com.darpal.foodlabrinthnew;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.darpal.foodlabrinthnew.R;

public class ToolbarActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setToolbarTitle(@StringRes int title) {
        getSupportActionBar().setTitle(title);
    }

    protected void setToolbarUpNavigation() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

   /* protected void setToolbarCloseNavigation() {
        setToolbarUpNavigation();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
    }*/

    protected void setToolbarLogo(@DrawableRes int drawableRes) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(drawableRes);
    }
}
