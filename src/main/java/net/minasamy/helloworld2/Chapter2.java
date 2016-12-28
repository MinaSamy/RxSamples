package net.minasamy.helloworld2;


import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.*;
import rx.subscriptions.CompositeSubscription;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mina.Samy on 10/23/2016.
 */
public class Chapter2 {

    public static void main(String[] args) {
        testConnectableObservable();

    }


    private static void emitVoid() {
        /*Observable<Void> voidObservable = Observable.create(e -> {
            //e.onNext(null);
            e.onComplete();
        });

        voidObservable.subscribe(new Observer<Void>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Void value) {
                System.out.println("Item emitted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("Complete");
            }
        });*/


    }


    private static void emitNumbers() {
        Observable<Integer> numbersObservable = Observable.create(emitter -> {
            for (int i = 1; i < 5; i++) {
                if (i % 2 == 0) {
                    emitter.onError(new Exception("Error"));
                }
                emitter.onNext(i);
                emitter.onCompleted();
            }
        });

        /*numbersObservable.subscribe(
                (Integer number)->{ System.out.println("Received new value "+number);},
                (Throwable t)->{ System.out.println("Exception "+t.toString());},
                ()->{ System.out.println("Completed");}
        );*/

        numbersObservable.subscribe(
                System.out::println,
                System.out::println,
                System.out::println
        );
    }

    private static void testSubscriber() {


        Subscriber<Integer> subscriber = new Subscriber<Integer>() {


            @Override
            public void onNext(Integer integer) {
                System.out.println("Received " + integer);

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Complete");
            }
        };


        Observable<Integer> observable = Observable.create(emitter -> {
            for (int i = 0; i < 5; i++) {
                emitter.onNext(i);
            }
        });

        //observable.subscribeWith(subscriber);
        //Flowable.just(1, 2, 3, 4, 5, 6).subscribe(subscriber);

    }

    private static void testObservableJust() {
        Observable<Integer[]> observable = Observable.just(new Integer[]{1, 2, 3, 4, 5});
        observable.subscribe(result -> {
            for (int i = 0; i < result.length; i++) {
                System.out.println(result[i]);
            }
        });
    }

    private static void testObservableFrom() {
        Observable<Integer> observable = Observable.from(new Integer[]{1, 2, 3, 4, 5, 6});
        observable.subscribe(item -> {
            System.out.println(item);
        });
    }

    private static void testObservableRange() {
        Observable<Integer> observable = Observable.range(0, 5);
        observable.subscribe(item -> {
            System.out.println(item);
        });
    }

    private static void testEmptyObservable() {
        Observable<Integer> observable = Observable.empty();
        observable.subscribe(
                (Integer result) -> {
                    System.out.println(result);
                },
                (Throwable t) -> {
                    System.out.println("Exception " + t.toString());
                },
                () -> {
                    System.out.println("Completed");
                }
        );
    }


    private static void testErrorObservable() {
        Observable<Integer> observable = Observable.error(new Exception("My Exception"));
        observable.subscribe(
                (Integer result) -> {
                },
                (Throwable t) -> {
                    System.out.println(t.toString());
                }
        );

        observable.subscribe(
                (Integer result) -> {
                },
                (Throwable t) -> {
                    System.out.println(t.toString());
                }
        );
    }

    private static void testCache() {
        Observable integerObservable = Observable.create(subscriber -> {
            System.out.println("Observable created");
            subscriber.onNext(5);
            subscriber.onCompleted();
        }).cache();

        integerObservable.subscribe(result -> System.out.println(result));
        integerObservable.subscribe(result -> System.out.println(result));
        integerObservable.subscribe(result -> System.out.println(result));
    }

    private static void testInfiniteObservable() {
        Observable<Integer> integerObservable = Observable.create(subscriber -> {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    BigInteger number = BigInteger.ZERO;
                    while (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(number.intValue());
                        number = number.add(BigInteger.ONE);
                    }
                }

            };
            new Thread(r).start();
        });


        CompositeSubscription compositeDisposable = new CompositeSubscription();
        compositeDisposable.add(integerObservable.subscribe(result -> System.out.println(result)));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        compositeDisposable.clear();

    }

    private static void testDelayedSubscription() {
        Observable.create(subscriber -> {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    while ((!subscriber.isUnsubscribed())) {
                        subscriber.onNext("Hello");
                    }
                }
            };

            Thread thread = new Thread(r);
            thread.start();

        });
    }

    private static void testTimerObservable() {
        Observable
                .timer(1, TimeUnit.SECONDS)
                .subscribe((Long zero) -> System.out.println(zero));
    }

    private static void testIntervalObservable() {
        try {
            Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
            observable.subscribe(new Subscriber<Long>() {
                @Override
                public void onCompleted() {
                    System.out.println("Completed");
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Long aLong) {
                    System.out.println(aLong);
                }
            });

        } catch (Exception ex) {
            System.out.println("Exception " + ex.toString());
        }


    }

    private static void testSubject() {
        SerializedSubject subject = ReplaySubject.create().toSerialized();

        subject.doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(integer);
            }
        });

        subject.doOnCompleted(new Action0() {
            @Override
            public void call() {
                System.out.println("Done");
            }
        });

        //These are lost
        subject.onNext(100);
        subject.onNext(200);

        subject.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("Subject Completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("Received " + integer);
            }
        });


        for (int i = 0; i < 5; i++) {
            subject.onNext(i);

        }

        subject.onCompleted();


    }


    private static void testRefCount() {
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("Subscribed to");
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                    //System.out.println(i);
                }
            }
        });

        /*Subscription sub1=observable.subscribe();
        Subscription sub2=observable.subscribe();*/


        Observable lazy = observable.publish().refCount();


        Subscription sub1 = lazy.subscribe(new Action1() {
            @Override
            public void call(Object o) {
                System.out.println("Sub 1 received " + o);
            }
        });
        sub1.unsubscribe();
        Subscription sub2 = lazy.subscribe(new Action1() {
            @Override
            public void call(Object o) {
                System.out.println("Sub 2 received " + o);
            }
        });


        System.out.println("Done");

    }


    private static void testDoOnNext() {
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                    System.out.println("Emit "+i);
                }
                subscriber.onCompleted();
                System.out.println("Completed");
            }
        });
        observable.doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("Do on next "+integer);
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                System.out.println("Do on completed");
            }
        }).subscribe();



    }


    private static void testConnectableObservable(){
        Observable<Integer> integerObservable=Observable.create(subscriber -> {
            System.out.println("Subscribed To");
           for(int i=0;i<5;i++){
               subscriber.onNext(i);
           }
           subscriber.onCompleted();
        });

        ConnectableObservable<Integer> connectableObservable=integerObservable.publish();


        connectableObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("Received "+integer);
            }
        });


        connectableObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("Received2 "+integer);
            }
        });

        connectableObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("Received3 "+integer);
            }
        });

        connectableObservable.connect();
    }
}
