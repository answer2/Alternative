# Alternative

Dynamic java method hook framework on ART.

## Why Alternative

- Pure Java: no native code used.

## Introduction

Alternative is a dynamic java method hook framework on ART runtime

There are many issues with this framework, please provide me with suggestions.
You can send an email to 'nswera929@gmail.com' or create issues

Note : The Constructor is currently unable to hook, which may be limited by the principles of this framework 

## Usage
Example 1: 
```java
AlternativeFramework.addStubAndHookMethod(Toast.class, "makeText", Context.class, CharSequence.class, int.class, 
new MethodHook(){
        @Override
        public void beforeMethod(MethodHookParam params) {
            Log.i(TAG, "Args: " + Arrays.toString(params.args));
            params.args[1]="Hello";
        }

        @Override
        public void afterMethod(MethodHookParam params) {
            Log.i(TAG, "After " + params.thisObject);
        }
});
```

## Credits
- [SandHook](https://github.com/ganyao114/SandHook)
- [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass)
- [dexmaker](https://github.com/linkedin/dexmaker/)
- [dalvik-dx](https://github.com/JakeWharton/dalvik-dx)

## LICENSE

Alternative is licensed under the **GNU General Public License v3 (GPL-3)** (http://www.gnu.org/copyleft/gpl.html).
