package dev.answer.alternative.stub;
import dev.answer.alternative.HookEntity;
import dev.answer.alternative.HookWrapper;
import dev.answer.alternative.MethodHookParam;
import dev.answer.alternative.callback.MethodHook;
import dev.answer.alternative.utils.ParamWrapper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import dev.answer.alternative.callback.MethodReplacement;
import dev.answer.alternative.AlternativeFramework;
import dev.answer.alternative.utils.MethodUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import dev.answer.alternative.utils.HookLog;

public class HookStubManager {

    private final static int maxStub = 600;

    private volatile static int hook_stub_count = 0;

    public final static ArrayList<HookWrapper> hookWrapperList;

    public final static Map<String,HookWrapper> hookWrapper_add_map;


    static{
        hookWrapperList = new ArrayList<>();
        hookWrapper_add_map = new ConcurrentHashMap<>();
    }

    public static synchronized Method getStubMethod() {
        while (hook_stub_count <= maxStub) {
            try {
                return HookStubMethods.class.getDeclaredMethod("hook_method_stub_" + hook_stub_count ++, ParametersToClasses(101));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static synchronized int getHookMethodIndex() {
        return hook_stub_count;
    }

    public static Object hookBridge_Static(Class<?>[] paramsTypes, Object... args) {
        final HookEntity entity;
        final MethodHook listener;

        String desc = MethodUtils.getDesc(AlternativeFramework.getNowClass(), AlternativeFramework.getNowMethodName(), paramsTypes);
        if (!hookWrapper_add_map.containsKey(desc)) { HookLog.w("Warning : Unable to find key"); return null;}

        HookWrapper hookWrapper = hookWrapper_add_map.get(desc);
        entity = hookWrapper.entity;
        listener = hookWrapper.listener;

        Object thisObject = null;

        MethodHookParam param = new MethodHookParam(args, thisObject, entity.Unhook);
        try {
            param.method = entity.backup;
            listener.beforeMethod(param);

            if (!(listener instanceof MethodReplacement) && !param.returnEarly) param.setResult(entity.callOriginMethod(thisObject, args));

            listener.afterMethod(param);

        } catch (Throwable error) {
            param.setThrowable(error);
            listener.afterMethod(param);
            if (param.getThrowable() != null) {
                try {
                    error.printStackTrace();
                    throw error.getCause();
                } catch (Throwable e) {
                }
            }
        }

        return param.getResult();
    }

    public static Object hookBridge(Object this_, Class<?>[] paramsTypes, Object... args) {
        
        final HookEntity entity;
        final MethodHook listener;

        String desc =  MethodUtils.getDesc(AlternativeFramework.getNowClass(), AlternativeFramework.getNowMethodName(), paramsTypes);
        if (!hookWrapper_add_map.containsKey(desc)) { HookLog.w("Warning : Unable to find key"); return null;}

        HookWrapper hookWrapper = hookWrapper_add_map.get(desc);
        entity = hookWrapper.entity;
        listener = hookWrapper.listener;

        Object thisObject = this_;

        MethodHookParam param = new MethodHookParam(args, thisObject, entity.Unhook);
        try {
            param.method = entity.backup;
            listener.beforeMethod(param);

            if (!(listener instanceof MethodReplacement) && !param.returnEarly) param.setResult(entity.callOriginMethod(thisObject, args));

            listener.afterMethod(param);

        } catch (Throwable error) {
            param.setThrowable(error);
            listener.afterMethod(param);
            if (param.getThrowable() != null) {
                try {
                    error.printStackTrace();
                    throw error.getCause();
                } catch (Throwable e) {
                }
            }
        }

        return param.getResult();
    }

    public static long hookBridge(
        int index,
        Object this_,
        int arg0,
        int arg1,
        int arg2,
        int arg3,
        int arg4,
        int arg5,
        int arg6,
        int arg7,
        int arg8,
        int arg9,
        int arg10,
        int arg11,
        int arg12,
        int arg13,
        int arg14,
        int arg15,
        int arg16,
        int arg17,
        int arg18,
        int arg19,
        int arg20,
        int arg21,
        int arg22,
        int arg23,
        int arg24,
        int arg25,
        int arg26,
        int arg27,
        int arg28,
        int arg29,
        int arg30,
        int arg31,
        int arg32,
        int arg33,
        int arg34,
        int arg35,
        int arg36,
        int arg37,
        int arg38,
        int arg39,
        int arg40,
        int arg41,
        int arg42,
        int arg43,
        int arg44,
        int arg45,
        int arg46,
        int arg47,
        int arg48,
        int arg49,
        int arg50,
        int arg51,
        int arg52,
        int arg53,
        int arg54,
        int arg55,
        int arg56,
        int arg57,
        int arg58,
        int arg59,
        int arg60,
        int arg61,
        int arg62,
        int arg63,
        int arg64,
        int arg65,
        int arg66,
        int arg67,
        int arg68,
        int arg69,
        int arg70,
        int arg71,
        int arg72,
        int arg73,
        int arg74,
        int arg75,
        int arg76,
        int arg77,
        int arg78,
        int arg79,
        int arg80,
        int arg81,
        int arg82,
        int arg83,
        int arg84,
        int arg85,
        int arg86,
        int arg87,
        int arg88,
        int arg89,
        int arg90,
        int arg91,
        int arg92,
        int arg93,
        int arg94,
        int arg95,
        int arg96,
        int arg97,
        int arg98,
        int arg99,
        int arg100) {

        final int paramNumber;
        final HookEntity entity;
        final MethodHook listener;

        Object[] stub_args = new Object[101];

        Object thisObject = null;
        Object[] args = new Object[100];

        HookWrapper hookWrapper = hookWrapperList.get(index);
        paramNumber = hookWrapper.paramNumber;
        entity = hookWrapper.entity;
        listener = hookWrapper.listener;


        Object[] stub_args_old =
            new Object[] {
            arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20, arg21, arg22, arg23, arg24, arg25, arg26, arg27, arg28, arg29, arg30, arg31, arg32, arg33, arg34, arg35, arg36, arg37, arg38, arg39, arg40, arg41, arg42, arg43, arg44, arg45, arg46, arg47, arg48, arg49, arg50, arg51, arg52, arg53, arg54, arg55, arg56, arg57, arg58, arg59, arg60, arg61, arg62, arg63, arg64, arg65, arg66, arg67, arg68, arg69, arg70, arg71, arg72, arg73, arg74, arg75, arg76, arg77, arg78, arg79, arg80, arg81, arg82, arg83, arg84, arg85, arg86, arg87, arg88, arg89, arg90, arg91, arg92,
            arg93, arg94, arg95, arg96, arg97, arg98, arg99, arg100
        };

        if (paramNumber != -1) {
            for (int i = 0; stub_args_old.length > i; i++) {
                Object arg = stub_args_old[stub_args_old.length - 1 - i];
                if (i < entity.methodParams.length && arg != null) {
                    stub_args[stub_args.length - 1 - i] = ParamWrapper.addressToObject(entity.methodParams[i], (int)arg);
                }
            }

            args = Arrays.copyOfRange(stub_args, stub_args.length - paramNumber, stub_args.length);
            thisObject = ParamWrapper.addressToObject(Object.class, (int) stub_args_old[stub_args_old.length - paramNumber - 1]);
        } else {
            args = new Object[]{};
        }

        if (thisObject == null) thisObject = this_;

        MethodHookParam param = new MethodHookParam(args, thisObject, entity.Unhook);
        try {
            param.method = entity.backup;
            listener.beforeMethod(param);

            if (!(listener instanceof MethodReplacement) && !param.returnEarly) param.setResult(entity.callOriginMethod(thisObject, args));

            listener.afterMethod(param);

        } catch (Throwable error) {
            param.setThrowable(error);
            listener.afterMethod(param);
            if (param.getThrowable() != null) {
                try {
                    error.printStackTrace();
                    throw error.getCause();
                } catch (Throwable e) {
                }
            }
        }

        return entity.backup.getReturnType() != void.class ? ParamWrapper.objectToAddress(entity.backup.getReturnType(), param.getResult()) : 0;
    }

    private static Class<?>[] ParametersToClasses(int count) {
        Class<?>[] newClasses = new Class<?>[count];
        for (int i=0; count > i;i++) {
            newClasses[i] = int.class;
        }
        return newClasses;
    }

}
