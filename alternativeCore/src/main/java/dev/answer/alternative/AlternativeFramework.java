package dev.answer.alternative;

import android.os.Build;
import dev.answer.alternative.callback.MethodHook;
import dev.answer.alternative.config.AlternativeConfig;
import dev.answer.alternative.config.BlackList;
import dev.answer.alternative.stub.HookStubManager;
import dev.answer.alternative.stub.StubClass;
import dev.answer.alternative.stub.StubMethod;
import dev.answer.alternative.utils.AlternativeException;
import dev.answer.alternative.utils.ArtMethodData;
import dev.answer.alternative.utils.ArtMethodFlags;
import dev.answer.alternative.utils.HookLog;
import dev.answer.alternative.utils.MethodUtils;
import dev.answer.alternative.utils.generateHookStub;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import sun.misc.Unsafe;
import java.lang.reflect.Constructor;
import dalvik.system.PathClassLoader;
import dalvik.system.BaseDexClassLoader;
import java.util.List;
import java.util.ArrayList;
import android.view.View;
import dev.answer.alternative.utils.HiddenApiBypass;


public class AlternativeFramework {

    static final Map<Member, HookEntity> globalHookEntityMap = new ConcurrentHashMap<>();


    private static Unsafe unsafe;
    private static int addressSize;

    private static long methodPageSize;
    private static long methodOffset;
    private static long classOffset;
    private static long artMethodOffset;
    private static long methodsOffset;
    private static long sFieldOffset;
    private static long iFieldOffset;

    public static Field nativePeerField;
    
    public static Class<?> VMRuntimeClass  =null;

    static{
        try {
            Unsafe u = unsafe = (Unsafe) Unsafe.class.getMethod("getUnsafe").invoke(null);

            if (u == null) {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                try {
                    Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    u = unsafe =  (Unsafe) theUnsafe.get(null);
                } catch (Exception e) {
                    try {
                        final Field theUnsafe = unsafeClass.getDeclaredField("THE_ONE");
                        theUnsafe.setAccessible(true);
                        u = unsafe = (Unsafe) theUnsafe.get(null);
                    } catch (Exception e2) {
                        AlternativeConfig.canUse = false;
                        HookLog.w("Unsafe not found T_T, Sorry, your device will not be able to use this framework");
                        throw new AlternativeException("Unsafe couldn't ues, please retry");
                    }
                }
            }

            //Compiler class have two methods, so it is treated as sutb
            Method[] stubs = Compiler.class.getDeclaredMethods();
            Method first = stubs[0];
            Method second = stubs[1];

            //Class
            Class<?> executable = StubClass.Executable.class;
            Class<?> methodHandle = StubClass.MethodHandle.class;
            Class<?> clazz = StubClass.Class.class;

            //get Address
            methodOffset = getFieldOffset(executable, "artMethod");
            classOffset = getFieldOffset(executable, "declaringClass");
            artMethodOffset = getFieldOffset(methodHandle, "artFieldOrMethod");
            methodsOffset = getFieldOffset(clazz, "methods");
            sFieldOffset = getFieldOffset(clazz, "sFields");
            iFieldOffset = getFieldOffset(clazz, "iFields");

            //get value
            addressSize = unsafe.addressSize();
            methodPageSize = unsafe.getLong(second, artMethodOffset) - unsafe.getLong(first, artMethodOffset);
            
            VMRuntimeClass = Class.forName("dalvik.system.VMRuntime");
            
            //Android 9 Permission Restrictions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                long methods_VMRuntime = unsafe.getLong(VMRuntimeClass, methodsOffset);
                //method size
                long count = addressSize == 8 ? u.getLong(methods_VMRuntime) : u.getInt(methods_VMRuntime);
                methods_VMRuntime += addressSize;
                for (long i = 0; count > i; i++) {
                    long method = i * methodPageSize + methods_VMRuntime;
                    unsafe.putLong(first, artMethodOffset, method);
                    String name = first.getName();
                    if (!"setHiddenApiExemptions".equals(name)) continue;
                    first.invoke(getRuntime(), new Object[] {new String[] {"L","Landroid/","Ljava/lang/","Ldalvik/system/"}});
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookConstructor(Class<?> declaringClass, Object... parame) {
        try {
            if (!AlternativeConfig.canUse) throw new AlternativeException("Sorry, your device will not be able to use this framework");

            Class<?>[] parameterTypes = null;
            MethodHook listener = (MethodHook) parame[parame.length - 1];
            if (parame.length > 1)
                parameterTypes = Arrays.copyOf(parame, parame.length - 1, Class[].class);

            Member origin = declaringClass.getDeclaredConstructor(parameterTypes);
            Method hook = HookStubManager.getStubMethod();
            Method back = StubMethod.getStubMethod();

            hookBase(HookMode.CONSTRUCTOR, origin, hook, back, parameterTypes, listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookMethod(Class<?> declaringClass, String methodName, Object... parame) {
        try {
            if (!AlternativeConfig.canUse) throw new AlternativeException("Sorry, your device will not be able to use this framework");

            Class<?>[] parameterTypes = null;
            MethodHook listener = (MethodHook) parame[parame.length - 1];
            if (parame.length > 1) 
                parameterTypes = Arrays.copyOf(parame, parame.length - 1, Class[].class);

            Method origin = declaringClass.getDeclaredMethod(methodName, parameterTypes);

            if (!BlackList.isAndroidPackage(origin)) {
                Method hook = HookStubManager.getStubMethod();
                Method back = StubMethod.getStubMethod();
                hookBase(HookMode.METHOD, origin, hook, back, parameterTypes, listener);
            } else {

                Class<?> stubClass = generateHookStub.generateMethod(origin);
                Method hook = stubClass.getDeclaredMethod(methodName, parameterTypes);
                Method back = StubMethod.getStubMethod();

                hookBase(HookMode.ADDSTUB, origin, hook, back, parameterTypes, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookBase(int mode, Member origin, Member hook, Method back, Class<?>[] parameterTypes, MethodHook listener) {
        try {
            if (!AlternativeConfig.canUse) throw new AlternativeException("Sorry, your device will not be able to use this framework");

            if (BlackList.canNotHook(origin) && mode != HookMode.ADDSTUB) {
                HookLog.w("Warning : Hook has been listenercepted this time because it hit the blacklist, Alternatively, use addStubAndHookMethod");
                return;
            } else if (hook != null && back != null) {
                long origin_offset = getArtMethod(origin);
                long hook_offset = getArtMethod(hook);
                long back_offset = getArtMethod(back);

                setMethodAccessible(origin_offset);
                setMethodAccessible(back_offset);
                deopt(origin_offset);
                deopt(hook_offset);

                byte[] backupBytes = new byte[(int)methodPageSize];
                unsafe.copyMemoryToPrimitiveArray(origin_offset, backupBytes, 0, methodPageSize);
                unsafe.copyMemory(origin_offset, back_offset, methodPageSize);
                unsafe.copyMemory(hook_offset, origin_offset, methodPageSize);

                Unhook unhook = new Unhook(origin_offset, hook_offset, back_offset, backupBytes);
                HookEntity entity = new HookEntity(origin, hook, back, parameterTypes, unhook);
                HookWrapper wrapper = new HookWrapper(listener, parameterTypes == null ? -1 : parameterTypes.length , entity);

                if (mode != HookMode.ADDSTUB) {
                    HookStubManager.hookWrapperList.add(wrapper);
                } else if (mode == HookMode.ADDSTUB) {
                    HookStubManager.hookWrapper_add_map.put(MethodUtils.getDesc(hook.getDeclaringClass(), hook.getName(), parameterTypes), wrapper);
                }
            } else {
                HookLog.w("Warning : Hook has been listenercepted this time because The stub is already full");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addStubAndHookConstructor(Class<?> declaringClass, Object... parame) {
        try {
            Class<?>[] parameterTypes = null;
            MethodHook listener = (MethodHook) parame[parame.length - 1];
            if (parame.length > 1) 
                parameterTypes = Arrays.copyOf(parame, parame.length - 1, Class[].class);
            Constructor origin = declaringClass.getDeclaredConstructor(parameterTypes);
            Member hook = generateHookStub.generateConstructor(origin);
            Method back = StubMethod.getStubMethod();
            
            int flags = getMemberFlags(hook);
            flags |= ArtMethodFlags.kConstructor;
            setMemberFlags(hook, flags);
           
            hookBase(HookMode.ADDSTUB, origin, hook, back, parameterTypes, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void addStubAndHookMethod(Class<?> declaringClass, String methodName, Object... parame) {
        try {
            Class<?>[] parameterTypes = null;
            MethodHook listener = (MethodHook) parame[parame.length - 1];
            if (parame.length > 1) 
                parameterTypes = Arrays.copyOf(parame, parame.length - 1, Class[].class);
            Method origin = declaringClass.getDeclaredMethod(methodName, parameterTypes);
            Class<?> stubClass = generateHookStub.generateMethod(origin);
            Method hook = stubClass.getDeclaredMethod(methodName, parameterTypes);
            Method back = StubMethod.getStubMethod();
            hookBase(HookMode.ADDSTUB, origin, hook, back, parameterTypes, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void addStubAndHookMethod(Class<?> StubClass, Class<?> declaringClass, String methodName, Object... parame) {
        try {
            Class<?>[] parameterTypes = null;
            MethodHook listener = (MethodHook) parame[parame.length - 1];
            if (parame.length > 1) 
                parameterTypes = Arrays.copyOf(parame, parame.length - 1, Class[].class);
            Method origin = declaringClass.getDeclaredMethod(methodName, parameterTypes);
            Method hook = StubClass.getDeclaredMethod(methodName, parameterTypes);
            Method back = StubMethod.getStubMethod();
            hookBase(HookMode.ADDSTUB, origin, hook, back, parameterTypes, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addStubAndHookMethod(Class<?> StubClass, String stubName, Class<?>[] stubTypes, Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, MethodHook listener) {
        try {
            Method origin = declaringClass.getDeclaredMethod(methodName, parameterTypes);
            Method hook = StubClass.getDeclaredMethod(stubName, stubTypes);
            Method back = StubMethod.getStubMethod();
            hookBase(HookMode.ADDSTUB, origin, hook, back, parameterTypes, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addStubAndHookMethod(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, Method hook, MethodHook listener) {
        try {
            Method origin = declaringClass.getDeclaredMethod(methodName, parameterTypes);
            Method back = StubMethod.getStubMethod();
            hookBase(HookMode.ADDSTUB, origin, hook, back, parameterTypes, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addStubAndHookMethod(Class<?> StubClass, Class<?>[] stubTypes, Class<?> declaringClass, String methodName, Class<?>[] parameterTypes, MethodHook listener) {
        addStubAndHookMethod(StubClass, methodName, stubTypes, declaringClass, methodName, parameterTypes, listener);
    }

    /* ArtMethod */
    public static long getArtMethod(Member method) {
        return unsafe.getLongVolatile(method, methodOffset);
    }

    public static Object callOriginMethod(Member method, Object thisObject, Object... args) throws Throwable {
        return globalHookEntityMap.get(method).callOriginMethod(thisObject, args);
    }

    public static long getArtMethod_(Member method) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Class<?> executableClass = Class.forName("java.lang.reflect.Executable");
                Field artIdField = executableClass.getDeclaredField("artMethod");
                artIdField.setAccessible(true);

                Executable executable = (Executable) method;
                long artMethodId =(long) artIdField.get(executable);
                return Long.valueOf(artMethodId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return getArtMethod(method);
        }
        return 0;
    }

    public static String getNowMethodName() {
        return Thread.currentThread().getStackTrace()[4].getMethodName();
    }

    public static String getNowClass() {
        return Thread.currentThread().getStackTrace()[4].getClassName();
    }

    /* Flags */
    private static void deopt(long ptr) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return;
        long addr = ptr + 4;
        int acc = unsafe.getInt(addr);
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.O) {
            acc &= ~0x8000000;
            if (sdk >= Build.VERSION_CODES.O_MR1) {
                acc |= 0x2800000;
                if (sdk >= Build.VERSION_CODES.Q) {
                    acc &= ~0x40000000;
                }
            }
        } else {
            acc |= 0x1000000;
        }

        unsafe.putInt(addr, acc);
    }

    public static void setMethodAccessible(long method) throws Exception {
        long address= method + 4 /*offset*/;
        int value= unsafe.getInt(address);
        value |= ArtMethodFlags.kAccPublicApi;
        value |= ArtMethodFlags.kAccPrivate;
        unsafe.putInt(address, value);
    }


    /* object / address */
    private static Class objectArrayClass = Object[].class;

    public static long getObjectAddress(Object obj) {
        try {
            Object[] array = new Object[]{obj};
            if (unsafe.arrayIndexScale(objectArrayClass) == 8) {
                return unsafe.getLong(array, unsafe.arrayBaseOffset(objectArrayClass));
            } else {
                return 0xffffffffL & unsafe.getInt(array, unsafe.arrayBaseOffset(objectArrayClass));
            }
        } catch (Exception e) {
            HookLog.e("get object address error" , e);
            return -1;
        }
    }

    public static Object getObjectByAddress(long address) {
        try {
            Object[] array = new Object[]{""};
            if (unsafe.arrayIndexScale(objectArrayClass) == 8) {
                unsafe.putLong(array, unsafe.arrayBaseOffset(objectArrayClass), address);
            } else {
                unsafe.putInt(array, unsafe.arrayBaseOffset(objectArrayClass), (int) address);
            }
            return array[0];
        } catch (Exception e) {
            HookLog.e("get object by address error: " , e);
            return null;
        }
    }


    /* util */
    public static long fieldOffset(Field field) {
        if (field == null || unsafe == null) {
            return -1L;
        }
        return unsafe.objectFieldOffset(field);
    }

    public static Field field(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public static long getFieldOffset(Class<?> clazz, String name) {
        return fieldOffset(field(clazz, name));
    }

    public static boolean is64Bit() {
        try{
        Method method = HiddenApiBypass.getDeclaredMethod(VMRuntimeClass, "is64Bit");
        return (boolean)method.invoke(getRuntime());
        }catch(Exception e){
            e.printStackTrace();
            return true;
        }
    }


    public static Object getRuntime() throws Exception {
        Method method = HiddenApiBypass.getDeclaredMethod(VMRuntimeClass, "getRuntime");
        return method.invoke(null);
    }

    /* field */
    
    // set
    
    public static void setFieldValue(Object object, Class<?> clazz, String field,  Object value) {
        long fieldOffset = AlternativeFramework.getFieldOffset(clazz, field);
        unsafe.putObject(object, fieldOffset, value);
    }

    public static void setFieldValue(Object object, String clazz, String field,  Object value) {
        try {
            setFieldValue(object, Class.forName(clazz), field, value);
        } catch (Exception e) {
            HookLog.e("AlternativeCore", e);
        }
    }

    public static void setFieldValue(Object object, String field,  Object value) {
        setFieldValue(object, object.getClass(), field, value);
    }
    
    // get
    
    public static Object getFieldValue(Object object, Class<?> clazz, String field) {
        long fieldOffset = AlternativeFramework.getFieldOffset(clazz, field);
        return unsafe.getObject(object, fieldOffset);
    }
    
    public static Object getFieldValue(Object object, String clazz, String field) {
        try {
           return getFieldValue(object, Class.forName(clazz), field);
        } catch (Exception e) {
            HookLog.e("AlternativeCore", e);
        }
        return null;
    }

    public static Object getFieldValue(Object object, String field) {
        return getFieldValue(object, object.getClass(), field);
    }
    
    public static int getMemberFlags(Member method){
        return unsafe.getInt(getArtMethod(method)+4);
    }
    
    public static void setMemberFlags(Member method, int flags){
        unsafe.putInt(getArtMethod(method)+4, flags);
    }
    
    // Method to ArtMethod Data

    public static ArtMethodData getArtMethodData(Method m) {
        ArtMethodData data = new ArtMethodData();
        for (int i= 0; i < methodPageSize;i++) {
            data.addValue(i, unsafe.getLong(getArtMethod(m) + i));
        }

        return data;
    }
    
    public static ArtMethodData getArtMethodData_Int(Method m) {
        ArtMethodData data = new ArtMethodData();
        for (int i= 0; i < methodPageSize;i++) {
            data.addValue(i, unsafe.getLong(getArtMethod(m) + i));
        }

        return data;
    }
    
    /* unHook */

    public static class Unhook {
        private final long originPtr;
        private final long hookPtr;
        private final long backupPtr;
        private final byte[] backupBytes;

        private Unhook(long originPtr, long hookPtr, long backupPtr, byte[] backupBytes) {
            this.originPtr = originPtr;
            this.hookPtr = hookPtr;
            this.backupPtr = backupPtr;
            this.backupBytes = backupBytes;
        }

        public void unhook() {
            unsafe.copyMemory(originPtr, hookPtr, methodPageSize);
            unsafe.copyMemory(backupPtr, originPtr, methodPageSize);
            unsafe.copyMemoryFromPrimitiveArray(backupBytes, 0, backupPtr, methodPageSize);
        }
    }
}
