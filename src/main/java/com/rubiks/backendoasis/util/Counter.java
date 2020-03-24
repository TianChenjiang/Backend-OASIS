package com.rubiks.backendoasis.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Counter {
   public static int getCount(List<String> listParm) {
       Set<String> repeated = new HashSet<>();
       int count = 0;
       for (String str : listParm) {
           if (repeated.add(str)) { //不是重复元素
               count++;
           }
       }
       return count;
   }
}
