package test.pool;

import java.util.Random;

/**
 * Created by HUANGYE2 on 10/24/2017.
 */
public class Worker {

    private boolean alive;

    private String name;

    private int workedCount;

    public Worker(String name, int key) {
        this.name = name;
//        this.alive = true;
//        this.alive = false;
//        this.alive = new Random().nextBoolean();
        this.alive = key == 2;
        this.workedCount = 0;
        System.out.println("[" + Thread.currentThread().getName() + "][" + this.name + "]: Worker created!");
    }

    public void work(int item) {
        System.out.println("[" + Thread.currentThread().getName() + "][" + this.name + "]: take item " + item);
        try {
            Thread.sleep(3000L);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[" + Thread.currentThread().getName() + "][" + this.name + "]: finish item " + item);
//        this.alive = false;
    }

    public void die() {
        System.out.println("[" + Thread.currentThread().getName() + "][" + this.name + "]: go die! ");
    }

    public boolean isAlive() {
        System.out.println("[" + Thread.currentThread().getName() + "][" + this.name + "]: check alive: " + this.alive);
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWorkedCount() {
        return workedCount;
    }

    public void setWorkedCount(int workedCount) {
        this.workedCount = workedCount;
    }
}
