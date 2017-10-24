package test.other;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by HUANGYE2 on 9/25/2017.
 */
public class SubArray {

    public static void main(String[] args) {

        String[] arr = new String[5];
        arr[0] = "0";
        arr[1] = "1";
        arr[2] = "2";
        arr[3] = "3";
        arr[4] = "4";

        arr = Arrays.copyOfRange(arr, 0, 3);

        System.out.println(Arrays.asList(arr));
    }



}
