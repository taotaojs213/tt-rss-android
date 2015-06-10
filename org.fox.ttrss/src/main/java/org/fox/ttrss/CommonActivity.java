package org.fox.ttrss;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import org.fox.ttrss.util.DatabaseHelper;

public class CommonActivity extends ActionBarActivity {
	private final String TAG = this.getClass().getSimpleName();
	
	public final static String FRAG_HEADLINES = "headlines";
	public final static String FRAG_ARTICLE = "article";
	public final static String FRAG_FEEDS = "feeds";
	public final static String FRAG_CATS = "cats";

	public final static String THEME_DARK = "THEME_DARK";
	public final static String THEME_LIGHT = "THEME_LIGHT";
	//public final static String THEME_SEPIA = "THEME_SEPIA";
    //public final static String THEME_AMBER = "THEME_AMBER";
	public final static String THEME_DEFAULT = CommonActivity.THEME_LIGHT;

    public static final int EXCERPT_MAX_LENGTH = 256;
    public static final int EXCERPT_MAX_QUERY_LENGTH = 2048;

	private DatabaseHelper m_databaseHelper;

	//private SQLiteDatabase m_readableDb;
	//private SQLiteDatabase m_writableDb;

	private boolean m_smallScreenMode = true;
	private String m_theme;

	protected SharedPreferences m_prefs;

	/* protected void enableHttpCaching() {
	   // enable resource caching
	   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
            	File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
            	long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            	HttpResponseCache.install(httpCacheDir, httpCacheSize);
            } catch (IOException e) {
            	e.printStackTrace();
            }        
        }
	} */

	protected void setSmallScreen(boolean smallScreen) {
		Log.d(TAG, "m_smallScreenMode=" + smallScreen);
		m_smallScreenMode = smallScreen;
	}

	public DatabaseHelper getDatabaseHelper() {
		return m_databaseHelper;
	}

	public SQLiteDatabase getDatabase() {
		return m_databaseHelper.getWritableDatabase();
	}

	public boolean getUnreadOnly() {
		return m_prefs.getBoolean("show_unread_only", true);
	}

    // not the same as isSmallScreen() which is mostly about layout being loaded
    public boolean isTablet() {
        return getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }

	public void setUnreadOnly(boolean unread) {
		SharedPreferences.Editor editor = m_prefs.edit();
		editor.putBoolean("show_unread_only", unread);
		editor.apply();
	}

	public void toast(int msgId) {
		Toast toast = Toast.makeText(CommonActivity.this, msgId, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void toast(String msg) {
		Toast toast = Toast.makeText(CommonActivity.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onResume() {
		super.onResume();
	
		if (!m_theme.equals(m_prefs.getString("theme", CommonActivity.THEME_DEFAULT))) {

			Log.d(TAG, "theme changed, restarting");
			
			finish();
			startActivity(getIntent());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		m_databaseHelper = DatabaseHelper.getInstance(this);

		m_prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

        if (savedInstanceState != null) {
			m_theme = savedInstanceState.getString("theme");
		} else {
			m_theme = m_prefs.getString("theme", CommonActivity.THEME_DEFAULT);
		}

		super.onCreate(savedInstanceState);
	}

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

	@Override
	public void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);
		
		out.putString("theme", m_theme);
	}
	
	public boolean isSmallScreen() {
		return m_smallScreenMode;
	}

	@SuppressWarnings("deprecation")
	public boolean isPortrait() {
		Display display = getWindowManager().getDefaultDisplay(); 
		
	    int width = display.getWidth();
	    int height = display.getHeight();
		
	    return width < height;
	}

	@SuppressLint({ "NewApi", "ServiceCast" })
	@SuppressWarnings("deprecation")
	public void copyToClipboard(String str) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {				
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(str);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(str);
		}		

		Toast toast = Toast.makeText(this, R.string.text_copied_to_clipboard, Toast.LENGTH_SHORT);
		toast.show();
	}

	/* public boolean isDarkTheme() {
		String theme = m_prefs.getString("theme", THEME_DEFAULT);
		
		return theme.equals(THEME_DARK); || theme.equals(THEME_AMBER);
	} */
	
	protected void setAppTheme(SharedPreferences prefs) {
		String theme = prefs.getString("theme", CommonActivity.THEME_DEFAULT);
		
		if (theme.equals(THEME_DARK)) {
            setTheme(R.style.DarkTheme);
        /* } else if (theme.equals(THEME_AMBER)) {
            setTheme(R.style.AmberTheme);
		} else if (theme.equals(THEME_SEPIA)) {
			setTheme(R.style.SepiaTheme); */
		} else {
			setTheme(R.style.LightTheme);
		}
	}

    /* protected void setDarkAppTheme(SharedPreferences prefs) {
        String theme = prefs.getString("theme", CommonActivity.THEME_DEFAULT);

        if (theme.equals(THEME_AMBER)) {
            setTheme(R.style.AmberTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }
    } */

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	protected int getScreenWidthInPixel() {
	    Display display = getWindowManager().getDefaultDisplay();

	    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
	        Point size = new Point();
	        display.getSize(size);
	        int width = size.x;
	        return width;       
	    } else {
	        return display.getWidth();
	    }
	}
}
