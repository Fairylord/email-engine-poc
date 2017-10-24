package test;

/**
 * Created by HUANGYE2 on 10/16/2017.
 */
public class ShuiXianHua {

    public static void main(String[] args) {
        find(0, 100000);
//
//        judge(370);


    }

    public static void find(int a, int b) {
        int start = a < b ? a : b;
        int end = a > b ? a : b;

        for(int i = start; i <= end; i++) {
            if(judge(i)) {
                System.out.println(i);
            }
        }
    }

    public static boolean judge(int num) {
        int sum = 0;
        int len = len(num);
        int remaining = num;
        int latestRemainder = 0;

        while (remaining != 0) {
            latestRemainder = remaining % 10;
            sum += pow(latestRemainder, len);
            remaining /= 10;
        }
        return sum == num;
    }

    public static int len(int num) {
        int remaining = num;
        int len = 0;

        do {
            len++;
            remaining /= 10;
        }
        while(remaining != 0);

        return len;
    }

    public static int pow(int base, int e) {
        int sum = 1;
        if(e > 0) {
            for(int i = 0; i < e; i++) {
                sum *= base;
            }
        }
        return sum;
    }
}
