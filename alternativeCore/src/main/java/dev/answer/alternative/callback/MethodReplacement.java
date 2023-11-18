package dev.answer.alternative.callback;
import dev.answer.alternative.MethodHookParam;

public abstract class MethodReplacement extends MethodHook {
    
    public static final MethodReplacement DO_NOTHING = new MethodReplacement(){
        @Override
        protected Object replaceCall(MethodHookParam params) throws Throwable {
            return null;
        }
    };

    @Override
    public void beforeMethod(MethodHookParam param) {
        super.beforeMethod(param);
        try {
            param.setResult(replaceCall(param));
        } catch (Throwable th) {
            param.setThrowable(th);
        }
    }

    @Override
    public void afterMethod(MethodHookParam param) {
        super.afterMethod(param);
    }
    
    protected abstract Object replaceCall(MethodHookParam params) throws Throwable;
    
    public static MethodReplacement returnConstant(final Object obj) {
        return new MethodReplacement(){
            @Override
            protected Object replaceCall(MethodHookParam params) throws Throwable {
                return obj;
            }
        };
    }
    
}
