package dev.answer.alternative.config;
import android.os.Build;

public class AlternativeConfig{
    
    public static final int SDK_INT = Build.VERSION.SDK_INT;
    
    public volatile static boolean canUse = true;
    public volatile static boolean DEBUG = false;
    //BlackList
    public volatile static boolean ignoreBlacklist = false;
    
    public volatile static ClassLoader initClassLoader;
    
    
}
