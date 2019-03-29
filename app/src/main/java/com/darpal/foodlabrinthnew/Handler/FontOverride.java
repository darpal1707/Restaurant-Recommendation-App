package com.darpal.foodlabrinthnew.Handler;
import android.app.Application;
import com.darpal.foodlabrinthnew.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class FontOverride extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
