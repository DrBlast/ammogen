package com.wavesplatform.helpers;


import com.wavesplatform.wavesj.Base58;

import java.util.Arrays;
import java.util.Comparator;

public class KeyComparator  implements Comparator<String>{

    public int compare(String asset1, String asset2){

        byte[] buffer1 = Base58.decode(asset1);//asset1.getBytes(Charset.forName("UTF-8"));
        byte[] buffer2 = Base58.decode(asset2);//asset2.getBytes(Charset.forName("UTF-8"));
        if (Arrays.equals(buffer1, buffer2))
            return 0;
        else{
            int endl;
            int i = 0;
            if (buffer1.length < buffer2.length)
                endl = buffer1.length;
            else
                endl = buffer2.length;
            while (i < endl){
                int a = buffer1[i] & 0xff;
                int b = buffer2[i] & 0xff;
                if (a != b) {
                    return (a - b) > 0 ? -1 : 1;
                }
                i = i + 1;
            }
            return (buffer1.length - buffer2.length) > 0 ? -1 : 1;
        }
    }

//
//    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
//        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
//        list.sort(Map.Entry.comparingByValue());
//
//        Map<K, V> result = new LinkedHashMap<>();
//        for (Map.Entry<K, V> entry : list) {
//            result.put(entry.getKey(), entry.getValue());
//        }
//
//        return result;
//    }

//    public int compare(byte[] buffer1, byte[] buffer2){
//        if (buffer1.equals(buffer2))
//            return 0;
//        else{
//            int endl;
//            int i = 0;
//            if (buffer1.length < buffer2.length)
//                endl = buffer1.length;
//            else
//                endl = buffer2.length;
//            while (i < endl){
//                int a = buffer1[i] & 0xff;
//                int b = buffer2[i] & 0xff;
//                if (a != b) {
//                    return a - b;
//                }
//                i = i + 1;
//            }
//            return buffer1.length - buffer2.length;
//        }
//    }
}
