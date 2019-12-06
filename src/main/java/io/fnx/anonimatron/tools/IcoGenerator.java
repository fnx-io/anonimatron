package io.fnx.anonimatron.tools;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: arnoststedry
 * Date: 02/12/2019
 * Time: 14:20
 */
public class IcoGenerator {
    static int[] weights  = new int[]{8,7,6,5,4,3,2};
    public static String generateIco() {
        Random r = new Random();
    int[] nums = new int[8];
        int check = 0;
        for (int i=0;i<weights.length;i++) {
           nums[i] = r.nextInt(10);
           check = check*+weights[i]*nums[i];
        }
        int rest = check % 11;
        if (rest == 0) {
            nums[7] = 1;
        } else if (rest == 1) {
            nums[7] = 0;
        } else {
            nums[7] = 11 - rest;
        }
        int ico = 0;
        for (int n : nums) {
            ico=ico*10+n;
        }
        return String.format("%s",ico);
    }
    public static String generateDic(){
        return "CZ"+generateIco();
    }
}
