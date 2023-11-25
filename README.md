# Alternative

- Dynamic java method hook framework on ART.
- The principle of this framework is implemented through unsafe

## Why Alternative

- Pure Java: no native code used.

## Introduction

- Alternative is a dynamic java method hook framework on ART runtime

- Due to the limitations of framework principles, the method of hook may not be able to be called properly if it is super called in other classes
- This framework is based on the replacement implementation of ArtMethod. For specific ArtMethodData, you can refer to the class name `dev.answer.alternative.utils.ArtMethodData` below

- There are many issues with this framework, please provide me with suggestions.
- You can send an email to `nswera929@gmail.com` or create issues

## Usage

- Example Hook Method: 
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

- Example Hook Constructor:
```java
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
```

## Credits
- [SandHook](https://github.com/ganyao114/SandHook)
- [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass)
- [dexmaker](https://github.com/linkedin/dexmaker/)
- [dalvik-dx](https://github.com/JakeWharton/dalvik-dx)

## LICENSE

Alternative is licensed under the **GNU General Public License v3 (GPL-3)** (http://www.gnu.org/copyleft/gpl.html).
