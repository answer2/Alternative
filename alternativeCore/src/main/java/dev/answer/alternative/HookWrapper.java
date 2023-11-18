package dev.answer.alternative;
import dev.answer.alternative.callback.MethodHook;
import java.util.Set;
import java.util.HashSet;

public class HookWrapper {

    public MethodHook listener;

    public HookEntity entity;

    public int paramNumber;
    
    private Set<MethodHook> callbacks = new HashSet<>();

    public HookWrapper(MethodHook listener, int count, HookEntity entity) {
        this.listener = listener;
        this.entity = entity;
        this.paramNumber = count;
    }
    
    public void addCallback(MethodHook methodHook) {
        synchronized (this) {
            this.callbacks.add(methodHook);
        }
    }

    public void removeCallback(MethodHook methodHook) {
        synchronized (this) {
            this.callbacks.remove(methodHook);
        }
    }

    public boolean emptyCallbacks() {
        boolean isEmpty;
        synchronized (this) {
            isEmpty = this.callbacks.isEmpty();
        }
        return isEmpty;
    }

    public MethodHook[] getCallbacks() {
        MethodHook[] methodHookArr;
        synchronized (this) {
            methodHookArr = (MethodHook[]) this.callbacks.toArray(new MethodHook[this.callbacks.size()]);
        }
        return methodHookArr;
    }
    

}
