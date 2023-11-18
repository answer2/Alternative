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

    public static final int kPublic = 0x0001;
    public static final int kPrivate = 0x0002;
    public static final int kProtected = 0x0004;
    public static final int kStatic = 0x0008;
    public static final int kFinal = 0x0010;
    public static final int kSynchronized = 0x0020;
    public static final int kNative = 0x0100;
    public static final int kConstructor = 0x00010000;
    public static final int kDeclaredSynchronized = 0x00020000;
    public static final int kSkipAccessChecks = 0x00080000;
    public static final int kMiranda = 0x00200000;
    public static final int kFastNative = 0x00080000;
    public static final int kCriticalNative = 0x00200000;
    public static final int kDontInline_M = 0x00400000;
    public static final int kCompileDontBother_N = 0x01000000;
    public static final int kCompileDontBother_O_MR1 = 0x02000000;
    public static final int kPreCompiled_R = 0x00200000;

    // This value from commit https://android-review.googlesource.com/c/platform/art/+/1646010
    // We skipped commit https://android-review.googlesource.com/c/platform/art/+/1645449
    public static final int kPreCompiled_S = 0x00800000;
    public static final int kSingleImplementation = 0x08000000;
    public static final int kPublicApi = 0x10000000;
    public static final int kCorePlatformApi = 0x20000000;
    public static final int kFastInterpreterToInterpreterInvoke = 0x40000000;
    
}
