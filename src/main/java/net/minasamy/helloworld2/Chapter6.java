package net.minasamy.helloworld2;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mina.Samy on 12/6/2016.
 */
public class Chapter6 {

    public static void main(String[]args){
        testBackPressure();
    }


    private static void testBuffer(){
        Observable.range(1,8)
                .buffer(3)
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        System.out.println(integers.toString());
                    }
                });
    }

    private static void testWindow(){
        Observable.range(1,20)
                .window(4)
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        System.out.println(integerObservable.toSingle());
                    }
                });
    }

    private static void testDebounce(){
        ConnectableObservable<Long>upstream=Observable.interval(99, TimeUnit.MILLISECONDS)
                .publish();

        upstream.debounce(100,TimeUnit.MILLISECONDS)
                .timeout(1,TimeUnit.SECONDS,upstream.take(1))
        .subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("Received "+aLong);
            }
        });

        upstream.connect();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testRequest(){
        Observable.range(1,10).subscribe(new Subscriber<Integer>() {

            @Override
            public void onStart() {
                super.onStart();
                request(3);
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {

                System.out.println(integer);
                if(integer%3==0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    request(3);
                }
            }
        });
    }

    private static void testBackPressure(){
        Observable.range(1,10000)
                .onBackpressureBuffer(100)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);

                    }
                });
    }
}
