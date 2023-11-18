package dev.answer.alternative;

import java.lang.reflect.Member;

public class MethodHookParam {
    private AlternativeFramework.Unhook Unhook;

    public Object[] args;

    public Object thisObject;

    private Object result = null;

    public Member method;

    private Throwable throwable;
    
    public boolean returnEarly = false;
    
    public boolean uesUnhook;
    
    public MethodHookParam(Object[] args, Object thisObject, AlternativeFramework.Unhook unhook) {
        this.args = args;
        this.thisObject = thisObject;
        this.Unhook = unhook;
    }

    public boolean hasThrowable() {
        return throwable != null ? true : false;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Object getResultOrThrowable() throws Throwable {
        Throwable tr = this.getThrowable();
        if (tr != null) {
            throw tr;
        } else {
            return this.getResult();
        }
    }

    public void setResult(Object result) {
        this.returnEarly = true;
        this.result = result;
    }

    public Object getResult() {
        return result;
    }
    
    public void unHook(){
        uesUnhook = true;
        Unhook.unhook();
    }
}
