package dev.answer.alternative;
import com.android.dx.Local;

public class MethodInfo {
   public Local<Integer> index;
   public Local<?> classLocal;
   public Local<?> argLocal;
    public Local<?> argLocal_new;

    public MethodInfo(Local<Integer> index, Local<?> classLocal, Local<?> argLocal, Local<?> argLocal_new) {
        this.index = index;
        this.classLocal = classLocal;
        this.argLocal = argLocal;
        this.argLocal_new = argLocal_new;
    }

    public MethodInfo(Local<Integer> index, Local<?> classLocal, Local<?> argLocal) {
        this.index = index;
        this.classLocal = classLocal;
        this.argLocal = argLocal;
    }
}
