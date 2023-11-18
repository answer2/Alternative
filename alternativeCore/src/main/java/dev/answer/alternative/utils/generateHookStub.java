package dev.answer.alternative.utils;
import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;
import dev.answer.alternative.MethodInfo;
import dev.answer.alternative.stub.HookStubManager;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dalvik.system.DexPathList;
import java.util.Arrays;
import dalvik.system.DexClassLoader;
import java.net.URL;
import java.net.URLClassLoader;
import dev.answer.alternative.AlternativeFramework;
import android.app.Application;
import java.lang.reflect.Field;
import com.android.dx.FieldId;
import com.android.dx.BinaryOp;

import com.android.dx.Code;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;
import java.lang.reflect.Member;
import java.lang.reflect.Array;
import java.lang.reflect.Executable;


public class generateHookStub {

    private generateHookStub() {

    }

    public static final Map<Class<?>, Class<?>> primitiveTypeMap = new HashMap<Class<?>, Class<?>>();

    static{
        primitiveTypeMap.put(int.class, Integer.class);
        primitiveTypeMap.put(byte.class, Byte.class);
        primitiveTypeMap.put(short.class, Short.class);
        primitiveTypeMap.put(long.class, Long.class);
        primitiveTypeMap.put(float.class, Float.class);
        primitiveTypeMap.put(double.class, Double.class);
        primitiveTypeMap.put(char.class, Character.class);
        primitiveTypeMap.put(boolean.class, Boolean.class);
    }

    
    public static Class<?> generateConstructor(Constructor method) throws Exception {
        DexMaker dexMaker = new DexMaker();

        Class superClazz = method.getDeclaringClass().getSuperclass();

        String Clazz = "L" + MethodUtils.getDeclaringClass(method.getDeclaringClass());

        TypeId<?> declaringType = TypeId.get(Clazz + "$stub;");
        dexMaker.declare(declaringType, method.getName() + "$stub.generated", Modifier.PUBLIC, superClazz == null ? TypeId.OBJECT: TypeId.get(superClazz));

        TypeId<?> HookStubManagerType = TypeId.get(HookStubManager.class);
        TypeId<?> ClassesType = TypeId.get(Class[].class);
        TypeId<?> ObjectsType = TypeId.get(Object[].class);

        MethodId<?,?> stubMethodId = (method.getParameterTypes() == null) ? declaringType.getConstructor()
            : declaringType.getConstructor(classesToTypes(method.getParameterTypes()));
        MethodId hookBridge_Static = HookStubManagerType.getMethod(TypeId.OBJECT, "hookBridge", TypeId.OBJECT, ClassesType, ObjectsType);

        Code code = dexMaker.declare(stubMethodId, method.getModifiers());

        Class[] classes = method.getParameterTypes();
        TypeId<?>[] argsTypes = classesToTypes(classes);
        List<MethodInfo> infoList = new ArrayList<>();
        int length = method.getParameterTypes() != null ? method.getParameterTypes().length : 0;


        Local<Integer> parameterTypeLength = code.newLocal(TypeId.INT);
        Local<Object> NulllLocal = code.newLocal(TypeId.OBJECT);
        // Local<Object> thisObjcet =   (Local<Object>) code.newLocal(TypeId.OBJECT);

        Local<?> thisRef = code.getThis(declaringType);

        Local<Class[]> ClassesLocal = code.newLocal(TypeId.get(Class[].class));
        Local<?> argsLocal = code.newLocal(TypeId.get(Object[].class));
        Local<?> local = code.newLocal(TypeId.OBJECT);

        for (int i = 0; classes.length > i;i++) {
            Local<Integer> indexLocal = code.newLocal(TypeId.INT);
            Local<Class> classLocal = code.newLocal(TypeId.get(Class.class));
            Local<Object> argLocal = (Local<Object>) code.getParameter(i, argsTypes[i]);
            Local<?> instance = primitiveTypeMap.containsKey(classes[i]) ? code.newLocal(TypeId.get(primitiveTypeMap.get(classes[i]))) : null;
            infoList.add(new MethodInfo(indexLocal, classLocal, argLocal, instance));
        }


        code.loadConstant(NulllLocal, null);
        code.loadConstant(parameterTypeLength, length);
        code.newArray(ClassesLocal, parameterTypeLength);
        code.newArray(argsLocal, parameterTypeLength);

        for (int i = 0; classes.length > i;i++) {
            MethodInfo info = infoList.get(i);
            Local<Integer> indexLocal = info.index;
            Local<?> classLocal = info.classLocal;
            Local<?> argLocal = info.argLocal;
            Local<?> instance = info.argLocal_new;
            if (instance != null) {
                MethodId method_ = TypeId.get(primitiveTypeMap.get(classes[i])).getConstructor(TypeId.get(classes[i]));
                code.newInstance(instance, method_ , argLocal);
                argLocal = instance;
            }
            
            code.loadConstant(indexLocal, Integer.valueOf(i));
            code.loadConstant((Local<Class>)classLocal, (Class)classes[i]);
            code.aput(ClassesLocal, indexLocal, classLocal);
            code.aput(argsLocal, indexLocal, argLocal);
        }

        code.invokeDirect(TypeId.OBJECT.getConstructor(), null, thisRef);

        // thisObjcet = (Local<Object>)code.getThis(declaringType);

        code.invokeStatic(hookBridge_Static, local, thisRef, ClassesLocal, argsLocal);


        code.returnVoid();

        
        byte[] bufs = dexMaker.generate();
        /*
        FileOutputStream foss = new java.io.FileOutputStream("/sdcard/Download/ddc.dex");
        foss.write(bufs);
        foss.close();*/

        generateHookStub instance = new generateHookStub();

        //System.out.println(instance.getClass().getClassLoader());

        Object dexfile = createDexFile(new ByteBuffer[]{ByteBuffer.wrap(bufs)}, instance.getClass().getClassLoader(), null);
        Class<?> clazz = loadClass(dexfile,  "" + method.getName() + "$stub" , instance.getClass().getClassLoader());

        return clazz;
    }

    


    public static Class<?> generateMethod(Method method) throws Exception {
        DexMaker dexMaker = new DexMaker();

        TypeId<?> declaringType = TypeId.get("Ldev/answer/alternative/framework/" + method.getName() + "$stub;");
        dexMaker.declare(declaringType, method.getName() + "$stub.generated", Modifier.PUBLIC, TypeId.OBJECT);

        TypeId<?> HookStubManagerType = TypeId.get(HookStubManager.class);
        TypeId<?> ClassesType = TypeId.get(Class[].class);
        TypeId<?> ObjectsType = TypeId.get(Object[].class);

        MethodId<?,?> stubMethodId = (method.getParameterTypes() == null) ? declaringType.getMethod(TypeId.get(method.getReturnType()), method.getName())
            : declaringType.getMethod(TypeId.get(method.getReturnType()), method.getName(), classesToTypes(method.getParameterTypes()));

        MethodId hookBridge_Static = HookStubManagerType.getMethod(TypeId.OBJECT, "hookBridge", TypeId.OBJECT, ClassesType, ObjectsType);

        int methodModifers = method.getModifiers();

        if (Modifier.isAbstract(methodModifers)) methodModifers &= ~ Modifier.ABSTRACT;

        Code code = dexMaker.declare(stubMethodId, methodModifers);

        Class[] classes = method.getParameterTypes();
        TypeId<?>[] argsTypes = classesToTypes(classes);

        List<MethodInfo> infoList = new ArrayList<>();
        int length = method.getParameterTypes() != null ? method.getParameterTypes().length : 0;


        Local<Integer> parameterTypeLength = code.newLocal(TypeId.INT);
        Local<Object> NulllLocal = code.newLocal(TypeId.OBJECT);
        Local<Object> thisObjcetLocal = code.newLocal(TypeId.OBJECT);
        Local<Class[]> ClassesLocal = code.newLocal(TypeId.get(Class[].class));
        Local<?> argsLocal = code.newLocal(TypeId.get(Object[].class));


        for (int i = 0; classes.length > i;i++) {
            Local<Integer> indexLocal = code.newLocal(TypeId.INT);
            Local<Class> classLocal = code.newLocal(TypeId.get(Class.class));
            Local<Object> argLocal = (Local<Object>) code.getParameter(i, argsTypes[i]);
            Local<?> instance = primitiveTypeMap.containsKey(classes[i]) ? code.newLocal(TypeId.get(primitiveTypeMap.get(classes[i]))) : null;
            infoList.add(new MethodInfo(indexLocal, classLocal, argLocal, instance));
        }

        Local<?> local = code.newLocal(TypeId.OBJECT);
        Local<?> resultLocal = code.newLocal(TypeId.get(method.getReturnType()));

        code.loadConstant(NulllLocal, null);
        code.loadConstant(parameterTypeLength, length);
        code.newArray(ClassesLocal, parameterTypeLength);
        code.newArray(argsLocal, parameterTypeLength);

        for (int i = 0; classes.length > i;i++) {
            MethodInfo info = infoList.get(i);

            Local<Integer> indexLocal = info.index;
            Local<?> classLocal = info.classLocal;
            Local<?> argLocal = info.argLocal;
            Local<?> instance = info.argLocal_new;
            if (instance != null) {
                MethodId method_ = TypeId.get(primitiveTypeMap.get(classes[i])).getConstructor(TypeId.get(classes[i]));
                code.newInstance(instance, method_ , argLocal);
                argLocal = instance;
            }

            code.loadConstant(indexLocal, Integer.valueOf(i));
            code.loadConstant((Local<Class>)classLocal, classes[i]);

            code.aput(ClassesLocal, indexLocal, classLocal);
            code.aput(argsLocal, indexLocal, argLocal);
        }


        if (Modifier.isStatic(method.getModifiers()) || method.getParameterTypes() == null) {
            code.loadConstant(thisObjcetLocal, null);
        } else {
            thisObjcetLocal = (Local<Object>)code.getThis(declaringType);
        }


        if (Modifier.isStatic(method.getModifiers())) {
            code.invokeStatic(hookBridge_Static, local, NulllLocal, ClassesLocal, argsLocal);
        } else {
            code.invokeStatic(hookBridge_Static, local, thisObjcetLocal, ClassesLocal, argsLocal);
        }

        if (method.getReturnType() == void.class) {
            code.returnVoid();
        } else {
            code.cast_(local, resultLocal); //魔改的方法
            code.returnValue(resultLocal);
        }


        byte[] bufs = dexMaker.generate();

        /*
         FileOutputStream foss = new java.io.FileOutputStream("/sdcard/Download/dd.dex");
         foss.write(bufs);
         foss.close();*/

        generateHookStub instance = new generateHookStub();

        Object dexfile = createDexFile(new ByteBuffer[]{ByteBuffer.wrap(bufs)}, instance.getClass().getClassLoader(), null);
        Class<?> clazz_ = loadClass(dexfile,  "dev.answer.alternative.framework." + method.getName() + "$stub" , instance.getClass().getClassLoader());

        return clazz_;
    }

    public static Application getApplication()  {
        try {
            //获取全局Context对象,任何时候，任何地方，任何逻辑都可以获取
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Field field = clazz.getDeclaredField("sCurrentActivityThread");
            field.setAccessible(true);
            //得到ActivityThread的对象，虽然是隐藏的，但已经指向了内存的堆地址
            Object object = field.get(null);
            Method method = clazz.getDeclaredMethod("getApplication");
            method.setAccessible(true);
            Application application = (Application) method.invoke(object);
            return application;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    private static TypeId<?>[] classesToTypes(Class<?>[] classes) {
        TypeId<?>[] types = new TypeId[classes.length];
        int count = 0;
        for (Class<?> clazz : classes) {
            types[count] = TypeId.get(clazz);
            count++;
        }
        return types;
    }
/*
    public static Object createDexFile(ByteBuffer[] bufs, ClassLoader loader, Object elements) throws Exception {
        Class<?> dexFileClass = Class.forName("dalvik.system.DexFile");
        Constructor init = HiddenApiBypass.getDeclaredConstructor(dexFileClass, ByteBuffer[].class, ClassLoader.class, DexPathList.Element[].class);
        Object result = init.newInstance(bufs, loader, elements);
        return result;
    }*/
    
    private static Constructor curricularInit = null;

    public static Object createDexFile(ByteBuffer[] bufs, ClassLoader loader, Object elements) throws Exception {
        Object result = null;
        Class<?> dexFileClass = Class.forName("dalvik.system.DexFile");
        if (curricularInit == null) {
            List<Executable> methods = HiddenApiBypass.getDeclaredMethods(dexFileClass);
            for (Member method : methods) {
                if (!(method instanceof Constructor)) continue;
                Constructor method_ = (Constructor) method;
                Class[] types = method_.getParameterTypes();
                if (method_.getParameterTypes().length > 2 && types[0] == ByteBuffer[].class && types[1] == ClassLoader.class) {
                    curricularInit = method_;
                    result = curricularInit.newInstance(bufs, loader, elements);;
                }
            }
        } else {
            result = curricularInit.newInstance(bufs, loader, elements);
        }

        return result;
    }

    public static Class<?> loadClass(Object dexfile, String name, ClassLoader loader) throws Exception {
        Class<?> dexFileClass = Class.forName("dalvik.system.DexFile");
        Method method = HiddenApiBypass.getDeclaredMethod(dexFileClass, "loadClass", String.class, ClassLoader.class);
        return (Class<?>) method.invoke(dexfile, name, loader);
    }

}
