
package dev.answer.alternative;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import dev.answer.alternative.callback.MethodHook;
import dev.answer.alternative.databinding.ActivityMainBinding;
import java.util.Arrays;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    
    public static final String TAG = "MainActivity-Log";
    
    public MainActivity(){
        
    }

    public MainActivity(String message){
        Log.d(TAG, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        // Initial Hook
        init();
        super.onCreate(savedInstanceState);
        

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
        setContentView(binding.getRoot());
        
        Toast.makeText(this,"你好",Toast.LENGTH_LONG).show();
        
        new MainActivity("Hello");
        
    }
    
    public static void init(){
    
        AlternativeFramework.addStubAndHookConstructor(MainActivity.class, String.class,  new MethodHook(){
                    @Override
                    public void beforeMethod(MethodHookParam params) {
                        Log.d(TAG, "This before");
                    }
                    
                    @Override
                    public void afterMethod(MethodHookParam params) {
                        Log.d(TAG, "This after");
                    }
                });
    
        AlternativeFramework.addStubAndHookMethod(Toast.class, "makeText", Context.class, CharSequence.class, int.class, 
            new MethodHook(){
                    @Override
                    public void beforeMethod(MethodHookParam params) {
                      System.out.println(Arrays.toString(params.args));
                      params.args[1]="Hello";
                    }

                    @Override
                    public void afterMethod(MethodHookParam params) {

                    }
            });
        
        AlternativeFramework.addStubAndHookMethod(Activity.class, "setContentView", View.class, 
            new MethodHook(){
                    @Override
                    public void beforeMethod(MethodHookParam params) {
                        System.out.println(Arrays.toString(params.args));
                    
                    }

                    @Override
                    public void afterMethod(MethodHookParam params) {
                    
                    }
            });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
