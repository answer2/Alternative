package dev.answer.alternative;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HookEntity{
    public Member origin;
    public Member hook;
    public Method backup;
    public Class<?>[] methodParams;
    public AlternativeFramework.Unhook Unhook;

    public HookEntity(Member origin, Member hook, Method backup, Class<?>[] methodParams,AlternativeFramework.Unhook Unhook){
        this.origin = origin;
        this.hook = hook;
        this.backup = backup;

        this.methodParams = methodParams;

        this.Unhook = Unhook;
    }


    public Object callOriginMethod(Object thisObject, Object... args) throws Throwable {
        if (Modifier.isStatic(origin.getModifiers())) {
            try {
                return backup.invoke(null, args);
            } catch (Throwable throwable) {
                if (throwable.getCause() != null) {
                    throw throwable.getCause();
                } else {
                    throw throwable;
                }
            }
        } else {
            try {
                return backup.invoke(thisObject, args);
            } catch (Throwable throwable) {
                if (throwable.getCause() != null) {
                    throw throwable.getCause();
                } else {
                    throw throwable;
                }
            }
        }
    }
}
