/**
 * @author Pedro Torres
 * @since 01/17/18
 */

package com.darpal.foodlabrinthnew;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

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
