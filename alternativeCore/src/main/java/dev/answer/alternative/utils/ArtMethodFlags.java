package dev.answer.alternative.utils;

public class ArtMethodFlags{
    public static final int kAccPublic =       0x0001;  // class, field, method, ic
    public static final int kAccPrivate =      0x0002;  // field, method, ic
    public static final int kAccProtected =    0x0004;  // field, method, ic
    public static final int kAccStatic =       0x0008;  // field, method, ic
    public static final int kAccFinal =        0x0010;  // class, field, method, ic
    public static final int kAccSynchronized = 0x0020;  // method (only allowed on natives)
    public static final int kAccSuper =        0x0020;  // class (not used in dex)
    public static final int kAccVolatile =     0x0040;  // field
    public static final int kAccBridge =       0x0040;  // method (1.5)
    public static final int kAccTransient =    0x0080;  // field
    public static final int kAccVarargs =      0x0080;  // method (1.5)
    public static final int kAccNative =       0x0100;  // method
    public static final int kAccInterface =    0x0200;  // class, ic
    public static final int kAccAbstract =     0x0400;  // class, method, ic
    public static final int kAccStrict =       0x0800;  // method
    public static final int kAccSynthetic =    0x1000;  // class, field, method, ic
    public static final int kAccAnnotation =   0x2000;  // class, ic (1.5)
    public static final int kAccEnum =         0x4000;  // class, field, ic (1.5)

    public static final int kAccPublicApi =             0x10000000;  // field, method
    public static final int kAccCorePlatformApi =       0x20000000;  // field, method

    // Native method flags are set when linking the methods based on the presence of the
    // @dalvik.annotation.optimization.{Fast,Critical}Native annotations with build visibility.
    // Reuse the values of kAccSkipAccessChecks and kAccMiranda which are not used for native methods.
    public static final int kAccFastNative =            0x00080000;  // method (runtime; native only)
    public static final int kAccCriticalNative =        0x00200000;  // method (runtime; native only)

}
