package dalvik.system;

public class VMRuntime {
    public static native VMRuntime getRuntime();
    
    public native boolean is64Bit();
        
}
