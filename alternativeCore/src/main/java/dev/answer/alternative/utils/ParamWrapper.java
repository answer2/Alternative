package dev.answer.alternative.utils;
import dalvik.system.VMRuntime;
import dev.answer.alternative.AlternativeFramework;

public class ParamWrapper {
    
    public static Object addressToObject(Class objectType, long address) {
        if (VMRuntime.getRuntime().is64Bit()) {
            return addressToObject64(objectType, address);
        } else {
            return addressToObject32(objectType, (int) address);
        }
    }

    public static Object addressToObject64(Class objectType, long address) {
        if (objectType == null)
            return null;
        if (objectType.isPrimitive()) {
            if (objectType == int.class) {
                return (int)address;
            } else if (objectType == long.class) {
                return address;
            } else if (objectType == short.class) {
                return (short)address;
            } else if (objectType == byte.class) {
                return (byte)address;
            } else if (objectType == char.class) {
                return (char)address;
            } else if (objectType == boolean.class) {
                return address != 0;
            } else {
                throw new RuntimeException("unknown type: " + objectType.toString());
            }
        } else {

            return AlternativeFramework.getObjectByAddress(address);
        }
    }

    public static Object addressToObject32(Class objectType, long address) {
        if (objectType == null)
            return null;
        if (objectType.isPrimitive()) {
            if (objectType == int.class) {
                return address;
            } else if (objectType == short.class) {
                return (short)address;
            } else if (objectType == byte.class) {
                return (byte)address;
            } else if (objectType == char.class) {
                return (char)address;
            } else if (objectType == boolean.class) {
                return address != 0;
            } else {
                throw new RuntimeException("unknown type: " + objectType.toString());
            }
        } else {

            return AlternativeFramework.getObjectByAddress(address);
        }
    }
    

    public static long objectToAddress(Class objectType, Object object) {
        if (VMRuntime.getRuntime().is64Bit()) {
            return objectToAddress64(objectType, object);
        } else {
            return objectToAddress32(objectType, object);
        }
    }

    public static int objectToAddress32(Class objectType, Object object) {
        if (object == null)
            return 0;
        if (objectType.isPrimitive()) {
            if (objectType == int.class) {
                return (int) object;
            } else if (objectType == short.class) {
                return (short)object;
            } else if (objectType == byte.class) {
                return (byte)object;
            } else if (objectType == char.class) {
                return (char) object;
            } else if (objectType == boolean.class) {
                return Boolean.TRUE.equals(object) ? 1 : 0;
            } else {
                throw new RuntimeException("unknown type: " + objectType.toString());
            }
        } else {
            return (int) AlternativeFramework.getObjectAddress(object);
        }
    }

    public static long objectToAddress64(Class objectType, Object object) {
        if (object == null)
            return 0;
        if (objectType.isPrimitive()) {
            if (objectType == int.class) {
                return (int)object;
            } else if (objectType == long.class) {
                return (long) object;
            } else if (objectType == short.class) {
                return (short)object;
            } else if (objectType == byte.class) {
                return (byte)object;
            } else if (objectType == char.class) {
                return (char) object;
            } else if (objectType == boolean.class) {
                return Boolean.TRUE.equals(object) ? 1 : 0;
            } else {
                throw new RuntimeException("unknown type: " + objectType.toString());
            }
        } else {
            return AlternativeFramework.getObjectAddress(object);
        }
    }
}
