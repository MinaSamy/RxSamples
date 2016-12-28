package net.minasamy.helloworld2;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mina.Samy on 11/24/2016.
 */
public class Chapter4 {
    public static void main(String[] args) {
        testPolling();
    }


    private static void testDefer() {
        Observable deferObs = Observable.defer(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                for (int j = 0; j < Integer.MAX_VALUE; j++) {
                }
            }
            Observable obs = Observable.from(new Integer[]{1, 2, 3, 4, 5, 6});
            obs = obs.doOnSubscribe(() -> System.out.println("Inner observable subscribed to"));
            return obs;
        });


        deferObs = deferObs.doOnSubscribe(() -> System.out.println("Defer observable subscribed to"));
        deferObs.subscribe(i -> System.out.println("Receive " + i));
        System.out.println("non blocking");

        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    private static void testConcat() {
        Observable.from(new Integer[]{1, 2, 3}).concatWith(Observable.just(4, 5, 6))
                .subscribe(i -> System.out.println(i));
    }


    private static Integer method1(){
        try {
            System.out.println("method1");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static Integer method2(){
        try {
            System.out.println("method2");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 2;
    }

    private static int sum(Integer n1,Integer n2){
        return n1+n2;
    }

    private static void testConcurrency(){
        Observable<Integer> obs1= Observable.defer(()->Observable.just(method1()));
        Observable<Integer> obs2=Observable.defer(()->Observable.just(method2()));

        obs1.zipWith(obs2,(i1,i2)->sum(i1,i2))
        .subscribe(result->System.out.println("Result "+result));

    }

    private static void testTimeout(){

        Observable.just(method1()).subscribeOn(Schedulers.io())
                .timeout(500, TimeUnit.MILLISECONDS).subscribe(i->System.out.println("Received "+i));
    }

    private static void testPolling(){
        Observable.just(1,1,2,2,3,4).interval(500,TimeUnit.MILLISECONDS)
                .flatMapIterable(new Func1<Long, Iterable<Long>>() {
                    @Override
                    public Iterable<Long> call(Long aLong) {
                        ArrayList<Long>items=new ArrayList<Long>();
                        items.add(aLong);
                        return items;
                    }
                }).distinct().subscribe(item->System.out.println(item));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
