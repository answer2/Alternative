package dev.answer.alternative.config;

import java.lang.reflect.Member;
import java.util.HashSet;
import java.util.Set;

// Thanks SandHook

public class BlackList{
    public static Set<String> methodBlackList = new HashSet<>();
    public static Set<Class> classBlackList = new HashSet<>();
    public static Set<String> classHandBlackList = new HashSet<>();
    
    static {
        
        //这个方法不是不让用，是框架，目前有些问题无法解决，只能ban，后续会推出一种用户自己创建函数，框架提供部分东西以便用来Hook这些
        classHandBlackList.add("android.");
        
        methodBlackList.add("java.lang.reflect.Method.invoke");
        methodBlackList.add("java.lang.reflect.AccessibleObject.setAccessible");
        
    }
    
    public final static String member2string(Member origin){
        return origin.getDeclaringClass().getName() + "." + origin.getName();
    }

    public final static boolean canNotHook(Member origin) {
        if(AlternativeConfig.ignoreBlacklist) return false;
        
        if (classBlackList.contains(origin.getDeclaringClass())) {
            return true;
        }

        String name = member2string(origin);
        if (methodBlackList.contains(name)) {
            return true;
        }

        // 检查 origin 的声明类是否在 classHandBlackList 中
        if (classHandBlackList.contains(origin.getDeclaringClass().getName()))
            return true;
        

        return false;
    }
    
    public static boolean isAndroidPackage(Member origin){
        return isClassInPackage(origin.getDeclaringClass(), "android.") || isClassInPackage(origin.getDeclaringClass(), "java.");
    }

    // 判断一个类是否属于某个包或其子包
    private static boolean isClassInPackage(Class<?> clazz, String packageName) {
        Package pkg = clazz.getPackage();
        return pkg != null && pkg.getName().startsWith(packageName);
    }
}
