package dev.answer.alternative.utils;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodUtils {

    public static String getDeclaringClass(final Method method) {
        return method.getDeclaringClass().getName().replace(".", "/");
    }
    
    public static String getDeclaringClass(final Class<?> clazz) {
        return clazz.getName().replace(".", "/");
    }

    public static String getMethodName(final Method method) {
        return method.getName();
    }
    
    public static String getDesc(Class<?> clazz,String name, Class<?>... types) {
        return getDesc(clazz.getName(), name, types);
    }
    
    public static String getDesc(String clazz,String name, Class<?>... types) {
        String typeStr = types!= null ? Arrays.toString(types).replace("[","(").replace("]",")") : "()";
        final StringBuffer buf = new StringBuffer();
        buf.append(clazz);
        buf.append(".");
        buf.append(name);
        buf.append(typeStr);
        return buf.toString();
    }
    
    
    public static String getDesc(Class<?> clazz,final Method method) {
        final StringBuffer buf = new StringBuffer();
        buf.append(getDeclaringClass(clazz));
        buf.append("/");
        buf.append(method.getName());
        buf.append("(");
        final Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; ++i) {
            buf.append(getDesc(types[i]));
        }
        buf.append(")");
        buf.append(getDesc(method.getReturnType()));
        return buf.toString();
    }
    

    public static String getDesc(final Method method) {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        final Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; ++i) {
            buf.append(getDesc(types[i]));
        }
        buf.append(")");
        buf.append(getDesc(method.getReturnType()));
        return buf.toString();
    }

    private static String getDesc(final Class<?> returnType) {
        if (returnType.isPrimitive()) {
            return getPrimitiveLetter(returnType);
        }
        if (returnType.isArray()) {
            return "[" + getDesc(returnType.getComponentType());
        }
        return "L" + getType(returnType) + ";";
    }

    private static String getType(final Class<?> parameterType) {
        if (parameterType.isArray()) {
            return "[" + getDesc(parameterType.getComponentType());
        }
        if (!parameterType.isPrimitive()) {
            final String clsName = parameterType.getName();
            return clsName.replaceAll("\\.", "/");
        }
        return getPrimitiveLetter(parameterType);
    }

    private static String getPrimitiveLetter(final Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        }
        if (Void.TYPE.equals(type)) {
            return "V";
        }
        if (Boolean.TYPE.equals(type)) {
            return "Z";
        }
        if (Character.TYPE.equals(type)) {
            return "C";
        }
        if (Byte.TYPE.equals(type)) {
            return "B";
        }
        if (Short.TYPE.equals(type)) {
            return "S";
        }
        if (Float.TYPE.equals(type)) {
            return "F";
        }
        if (Long.TYPE.equals(type)) {
            return "J";
        }
        if (Double.TYPE.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }
}
