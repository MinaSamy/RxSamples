package net.minasamy.helloworld2;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mina.Samy on 12/15/2016.
 */
public class Chapter7 {

    public static void main(String[]args){
        testFlatMapIterable();
    }


    private static void testUnhandledExceptions(){
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1/0);
            }
        })
                .flatMap(Observable::just)
                .onErrorReturn(new Func1<Throwable, Integer>() {
                    @Override
                    public Integer call(Throwable throwable) {
                        return -1;
                    }
                })
        .subscribe(System.out::println,System.out::println);
    }

    private static void testOnResumeNext(){
        Observable.range(0,5)
                .map(i->i/i)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Integer>>() {
                    @Override
                    public Observable<? extends Integer> call(Throwable throwable) {
                        return Observable.just(-1);
                    }
                })
                .subscribe(System.out::println);
    }

    private static void testRetry(){
        Observable.fromCallable(new Func0<String>() {
            @Override
            public String call() {
                if(Math.random()<0.1){
                    return "OK";
                }
                throw new RuntimeException("Transient");
            }
        })
        .timeout(1, TimeUnit.SECONDS)
        .doOnError((t)->System.out.println("Do on error"))
        .retry(1)
        .subscribe(System.out::println,System.out::println);
    }

    private static void testSched(){
        TestScheduler scheduler=Schedulers.test();

        Observable<String>fast=Observable.interval(10,TimeUnit.MILLISECONDS,scheduler)
                .map(i->"F"+i)
                .take(3);

        Observable<String>slow=Observable.interval(50,TimeUnit.MILLISECONDS,scheduler)
                .map(i->"S"+i);

        Observable<String>stream=Observable.concat(fast,slow);
        stream.subscribe(System.out::println);
        System.out.println("Subscribed");

        try {
            //TimeUnit.SECONDS.sleep(1);
            System.out.println("After one second");
            scheduler.advanceTimeBy(25,TimeUnit.MILLISECONDS);

            //TimeUnit.SECONDS.sleep(1);
            System.out.println("After one more second");
            scheduler.advanceTimeBy(75,TimeUnit.MILLISECONDS);

            //TimeUnit.SECONDS.sleep(1);
            System.out.println("and more second...");
            scheduler.advanceTimeBy(200,TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void testFlatMapIterable(){
        List<Integer> numbers= Arrays.asList(-1,-2,-3);
       /* Observable.range(1,3).concatMapIterable(new Func1<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> call(Integer integer) {
                return numbers;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(integer);
            }
        });*/

       Observable.range(1,3).concatMap(new Func1<Integer, Observable<Integer>>() {
           @Override
           public Observable<Integer> call(Integer integer) {
               return Observable.just(integer);
           }
       }).subscribe(System.out::println);
    }
}
