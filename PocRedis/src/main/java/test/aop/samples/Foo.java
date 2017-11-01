package test.aop.samples;

import test.aop.annotation.RetryOnFailure;

import java.util.concurrent.TimeUnit;

/**
 * Created by HUANGYE2 on 10/31/2017.
 */
public class Foo {

    public static int counter = 0;

    @RetryOnFailure(attempts = 5, delay = 500, unit = TimeUnit.MILLISECONDS)
    public int doSomething(int i) throws IllegalAccessException {
        System.out.println("Call");

        if (counter < 3) {
            counter += 1;
            throw new IllegalAccessException("Hey");
        }

        return 50;
    }


    public static void main(String[] args) {
        Foo foo = new Foo();
        try {
            foo.doSomething(10);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
