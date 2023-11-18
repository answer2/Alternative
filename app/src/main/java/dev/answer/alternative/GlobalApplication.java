package dev.answer.alternative;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.textview.MaterialTextView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlobalApplication extends Application {

  private static Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.getInstance().registerGlobal(this);
    //CrashHandler.getInstance().registerPart(this);
  }

  public static void write(InputStream input, OutputStream output) throws IOException {
    byte[] buf = new byte[1024 * 8];
    int len;
    while ((len = input.read(buf)) != -1) {
      output.write(buf, 0, len);
    }
  }

  public static void write(File file, byte[] data) throws IOException {
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) parent.mkdirs();

    ByteArrayInputStream input = new ByteArrayInputStream(data);
    FileOutputStream output = new FileOutputStream(file);
    try {
      write(input, output);
    } finally {
      closeIO(input, output);
    }
  }

  public static String toString(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    write(input, output);
    try {
      return output.toString("UTF-8");
    } finally {
      closeIO(input, output);
    }
  }

  public static void closeIO(Closeable... closeables) {
    for (Closeable closeable : closeables) {
      try {
        if (closeable != null) closeable.close();
      } catch (IOException ignored) {
      }
    }
  }

  public static class CrashHandler {

    public static final UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER =
        Thread.getDefaultUncaughtExceptionHandler();

    private static CrashHandler sInstance;

    private PartCrashHandler mPartCrashHandler;

    public static CrashHandler getInstance() {
      if (sInstance == null) {
        sInstance = new CrashHandler();
      }
      return sInstance;
    }

    public void registerGlobal(Context context) {
      registerGlobal(context, null);
    }

    public void registerGlobal(Context context, String crashDir) {
      Thread.setDefaultUncaughtExceptionHandler(
          new UncaughtExceptionHandlerImpl(context.getApplicationContext(), crashDir));
    }

    public void unregister() {
      Thread.setDefaultUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_HANDLER);
    }

    public void registerPart(Context context) {
      unregisterPart(context);
      mPartCrashHandler = new PartCrashHandler(context.getApplicationContext());
      MAIN_HANDLER.post(mPartCrashHandler);
    }

    public void unregisterPart(Context context) {
      if (mPartCrashHandler != null) {
        mPartCrashHandler.isRunning.set(false);
        MAIN_HANDLER.removeCallbacks(mPartCrashHandler);
        mPartCrashHandler = null;
      }
    }

    private static class PartCrashHandler implements Runnable {

      private final Context mContext;

      public AtomicBoolean isRunning = new AtomicBoolean(true);

      public PartCrashHandler(Context context) {
        this.mContext = context;
      }

      @Override
      public void run() {
        Looper myLooper = Looper.myLooper();
        if (myLooper == null) {
          Looper.prepare();
          myLooper = Looper.myLooper();
        }

        while (isRunning.get()) {
          try {
            Looper.loop();
          } catch (final Throwable e) {
            e.printStackTrace();
            if (isRunning.get()) {
              MAIN_HANDLER.post(
                  () -> {
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
                  });
            } else {
              if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
              } else {
                throw new RuntimeException(e);
              }
            }
          }
        }

        // 在循环结束后退出 Looper
        if (myLooper != null) {
          myLooper.quit();
        }
      }
    }

    private static class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {

      private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

      private final Context mContext;

      private final File mCrashDir;

      public UncaughtExceptionHandlerImpl(Context context, String crashDir) {
        this.mContext = context;
        this.mCrashDir =
            TextUtils.isEmpty(crashDir)
                ? new File(mContext.getExternalCacheDir(), "crash")
                : new File(crashDir);
      }

      @Override
      public void uncaughtException(Thread thread, Throwable throwable) {
        try {

          String log = buildLog(throwable);
          writeLog(log);

          Log.d("crash", "启动....");

          try {
            Intent intent = new Intent(mContext, CrashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, log);
            mContext.startActivity(intent);
          } catch (Throwable e) {
            e.printStackTrace();
            writeLog(e.toString());
          }

          throwable.printStackTrace();
          android.os.Process.killProcess(android.os.Process.myPid());
          System.exit(0);

        } catch (Throwable e) {
          if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null)
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(thread, throwable);
        }
      }

      private String buildLog(Throwable throwable) {
        String time = DATE_FORMAT.format(new Date());

        String versionName = "unknown";
        long versionCode = 0;
        try {
          PackageInfo packageInfo =
              mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
          versionName = packageInfo.versionName;
          versionCode =
              Build.VERSION.SDK_INT >= 28
                  ? packageInfo.getLongVersionCode()
                  : packageInfo.versionCode;
        } catch (Throwable ignored) {
        }

        LinkedHashMap<String, String> head = new LinkedHashMap<String, String>();
        head.put("Time Of Crash", time);
        head.put("Device", String.format("%s, %s", Build.MANUFACTURER, Build.MODEL));
        head.put(
            "Android Version",
            String.format("%s (%d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT));
        head.put("App Version", String.format("%s (%d)", versionName, versionCode));
        head.put("Kernel", getKernel());
        head.put(
            "Support Abis",
            Build.VERSION.SDK_INT >= 21 && Build.SUPPORTED_ABIS != null
                ? Arrays.toString(Build.SUPPORTED_ABIS)
                : "unknown");
        head.put("Fingerprint", Build.FINGERPRINT);

        StringBuilder builder = new StringBuilder();

        for (String key : head.keySet()) {
          if (builder.length() != 0) builder.append("\n");
          builder.append(key);
          builder.append(" :    ");
          builder.append(head.get(key));
        }

        builder.append("\n\n");
        builder.append(Log.getStackTraceString(throwable));

        return builder.toString();
      }

      private void writeLog(String log) {
        String time = DATE_FORMAT.format(new Date());
        File file = new File(mCrashDir, "crash_" + time + ".txt");
        try {
          write(file, log.getBytes("UTF-8"));
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }

      private static String getKernel() {
        try {
          return GlobalApplication.toString(new FileInputStream("/proc/version")).trim();
        } catch (Throwable e) {
          return e.getMessage();
        }
      }
    }
  }

  public static final class CrashActivity extends AppCompatActivity {

    private String mLog = "我不知道啊！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // 设置为沉浸式状态栏
      WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
      ThemeUtils.statusBarColor(this, Color.TRANSPARENT, ThemeUtils.isSystemInDarkTheme(this));

      mLog = getIntent().getStringExtra(Intent.EXTRA_TEXT);

      LinearLayout parentLayout = new LinearLayout(this);
      parentLayout.setOrientation(LinearLayout.VERTICAL);

      NestedScrollView contentView = new NestedScrollView(this);
      contentView.setFillViewport(true);

      LinearLayout horizontalScrollViewContainer = new LinearLayout(this);
      HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);

      MaterialTextView textView = new MaterialTextView(this);
      int padding = dp2px(16);
      textView.setPadding(padding, padding, padding, padding);
      if (mLog != null) {
        textView.setText(mLog);
      } else {
        textView.setText("我不道啊(●—●)");
      }
      textView.setTextIsSelectable(true);
      textView.setTypeface(Typeface.DEFAULT);
      textView.setLinksClickable(true);

      horizontalScrollView.addView(textView);
      horizontalScrollViewContainer.addView(horizontalScrollView);
      contentView.addView(horizontalScrollViewContainer);

      setTitle("App Crash");

      parentLayout.addView(contentView);

      setContentView(parentLayout);
    }

    private void restart() {
      Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
      if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
      finish();
      android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(0);
    }

    private static int dp2px(float dpValue) {
      final float scale = Resources.getSystem().getDisplayMetrics().density;
      return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, android.R.id.copy, 0, android.R.string.copy)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
      return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
        case android.R.id.copy:
          ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
          cm.setPrimaryClip(ClipData.newPlainText(getPackageName(), mLog));
          return true;
      }
      return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
      restart();
    }
  }
}
