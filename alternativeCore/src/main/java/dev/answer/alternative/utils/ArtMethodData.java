package dev.answer.alternative.utils;

import dev.answer.alternative.utils.HookLog;
import java.lang.reflect.Field;

// If used, try not to confuse this class as much as possible
public class ArtMethodData{
    
    private long[] curricularAddress = null;
    
    public long address;//class
    public long address1;
    public long address2;
    public long address3;
    public long address4;//flags access_flags_
    public long address5;
    public long address6;
    public long address7;
    //在 dex file 中的位置
    public long address8;// dex_code_item_offset_ 
    public long address9;
    public long address10;
    public long address11;
    //在 dex 中 method_id 的 index，通过它获取名称等信息
    public long address12;// dex_method_index_ 
    public long address13;
    public long address14;
    public long address15;
    // static/direct method -> declaringClass.directMethods
    // virtual method -> vtable
    // interface method -> ifTable
    public long address16;// method_index_
    public long address17;
    public long address18;
    public long address19;
    // 调用一次加一，超过阈值可能会被编译成本地方法
    public long address20;// hotness_count_
    public long address21;
    public long address22;
    public long address23;
    public long address24;
    public long address25;
    public long address26;
    public long address27;
    public long address28;
    public long address29;
    public long address30;
    public long address31;
    public long address32;
    public long address33;
    public long address34;
    public long address35;
    public long address36;
    public long address37;
    public long address38;
    public long address39;
    public long address40;
    
    public ArtMethodData(){}
    
    public void addValue(int count, long value){
        try{
            Field f = getClass().getDeclaredField("address"+ (count==0 ?"" : count));
            f.set(this, value);
        }catch(Exception e){
            HookLog.e("ArtMethodData", e);
        }
    }

    @Override
    public String toString() {
        return "ArtMethodData{" +
            "address(class)=" + address +
            ", address1=" + address1 +
            ", address2=" + address2 +
            ", address3=" + address3 +
            ", address4(flags)=" + address4 +
            ", address5=" + address5 +
            ", address6=" + address6 +
            ", address7=" + address7 +
            ", address8=" + address8 +
            ", address9=" + address9 +
            ", address10=" + address10 +
            ", address11=" + address11 +
            ", address12=" + address12 +
            ", address13=" + address13 +
            ", address14=" + address14 +
            ", address15=" + address15 +
            ", address16=" + address16 +
            ", address17=" + address17 +
            ", address18=" + address18 +
            ", address19=" + address19 +
            ", address20=" + address20 +
            ", address21=" + address21 +
            ", address22=" + address22 +
            ", address23=" + address23 +
            ", address24=" + address24 +
            ", address25=" + address25 +
            ", address26=" + address26 +
            ", address27=" + address27 +
            ", address28=" + address28 +
            ", address29=" + address29 +
            ", address30=" + address30 +
            ", address31=" + address31 +
            ", address32=" + address32 +
            ", address33=" + address33 +
            ", address34=" + address34 +
            ", address35=" + address35 +
            ", address36=" + address36 +
            ", address37=" + address37 +
            ", address38=" + address38 +
            ", address39=" + address39 +
            ", address40=" + address40 +
            '}';
    }
    
    public long[] toArray() {
        long[] adressses = new long[] {
            address,
            address1,
            address2,
            address3,
            address4,
            address5,
            address6,
            address7,
            address8,
            address9,
            address10,
            address11,
            address12,
            address13,
            address14,
            address15,
            address16,
            address17,
            address18,
            address19,
            address20,
            address21,
            address22,
            address23,
            address24,
            address25,
            address26,
            address27,
            address28,
            address29,
            address30,
            address31,
            address32,
            address33,
            address34,
            address35,
            address36,
            address37,
            address38,
            address39,
            address40
        };
        
        if(curricularAddress == null) 
            curricularAddress = adressses;
        
        return curricularAddress;
    }
    
}
