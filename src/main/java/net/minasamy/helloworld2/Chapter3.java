package net.minasamy.helloworld2;

import net.minasamy.helloworld2.model.*;
import org.apache.commons.lang3.tuple.Pair;
import rx.Observable;
import rx.Subscriber;
import rx.functions.*;
import rx.observables.GroupedObservable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mina.Samy on 10/30/2016.
 */
public class Chapter3 {

    public static void main(String[] args) {
        testGroupBy();

    }

    private static void testFilter() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 1000; i++) {
                    subscriber.onNext(i);
                    ;
                }
                subscriber.onCompleted();
            }
        }).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return (integer % 2 != 0);
            }
        }).filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return (integer % 3 == 0);
            }
        })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("Received " + integer);
                    }
                });
    }

    private static void testMap() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 10; i++) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        }).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return "Emit Item " + integer;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("Received " + s);
            }
        });
    }

    private static void testFlatMap() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(i);
                    System.out.println("Emit number " + i);
                }
            }
        }).flatMap(new Func1<Integer, Observable<String>>() {
            @Override
            public Observable<String> call(Integer integer) {
                return Observable.just(integer).map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        System.out.println("Flat Map " + integer);
                        return "Emit Item " + integer;
                    }
                });
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("Received " + s);
            }
        });
    }

    private static void testCustomersFlatMap1() {
        Observable<Customer> customersObservable = Observable.from(getCustomers());
        customersObservable.flatMap(new Func1<Customer, Observable<Order>>() {
            @Override
            public Observable<Order> call(Customer customer) {
                return Observable.from(customer.orders);
            }
        }).subscribe(new Action1<Order>() {
            @Override
            public void call(Order order) {
                System.out.println("Received order " + order);
            }
        });
    }

    private static void testCustomersFlatMap2() {
        Observable<Customer> customerObservable = Observable.from(getCustomers());
        customerObservable.flatMapIterable(new Func1<Customer, Iterable<Order>>() {
            @Override
            public Iterable<Order> call(Customer customer) {
                return customer.orders;
            }
        }).subscribe(new Action1<Order>() {
            @Override
            public void call(Order order) {
                System.out.println("Received order " + order);
            }
        });
    }


    private static List<Customer> getCustomers() {
        return new ArrayList<Customer>();
    }


    private static void testDelay() {
        Observable.from(new Integer[]{1, 2, 3, 4, 5, 6}).delay(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println(integer);
                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void testFlatMapOrdering() {
        Observable.just(5L, 1L)
                .flatMap(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.just(aLong).delay(aLong, TimeUnit.SECONDS);
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("Received " + aLong);
            }
        });

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testConcatMap() {
        Observable.just(5L, 1L)
                .concatMap(new Func1<Long, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Long aLong) {
                        return Observable.just(aLong).delay(aLong, TimeUnit.SECONDS);
                    }
                }).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("Received " + aLong);
            }
        });

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testFlatMapMaxObservables() {
        Integer[] numbers = new Integer[10000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i;
        }
        Observable.from(numbers).flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                return Observable.just(integer);
            }
        }, 5).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("Received " + integer);
            }
        });
    }


    private static void testMerge() {
        //Observable observable1= Observable.interval(500,TimeUnit.MILLISECONDS).just("Item 1","Item 2","Item 3","Item 4","Item 5");
        Observable observable1 = Observable.interval(500, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<Long, Observable<String>>() {
                    @Override
                    public Observable<String> call(Long aLong) {
                        return Observable.just("Item" + aLong);
                    }
                });
        Observable observable2 = Observable.interval(600, TimeUnit.MILLISECONDS).take(10);


        Observable all = Observable.merge(observable1, observable2);
        all.subscribe(new Action1() {
            @Override
            public void call(Object o) {
                System.out.println(o);
            }
        });

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testZip() {
        Integer[] numbers = new Integer[10];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1;
        }

        String[] items = new String[10];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Item" + i + 1;
        }

        Observable obs1 = Observable.from(numbers).concatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer integer) {
                return Observable.just(integer).delay(300, TimeUnit.MILLISECONDS);
            }
        });
        Observable obs2 = Observable.from(items);

        Observable.zip(obs1, obs2, new Func2<Integer, String, CompositeModel>() {

            @Override
            public CompositeModel call(Integer integer, String s) {
                CompositeModel model = new CompositeModel() {
                    {
                        name = s;
                        id = integer;
                    }
                };
                return model;
            }
        }).subscribe(new Action1<CompositeModel>() {
            @Override
            public void call(CompositeModel o) {
                System.out.println("Model " + o.id + " " + o.name);
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testZip2() {
        Observable<LocalDate> nextTenDays = Observable.range(1, 10).map(new Func1<Integer, LocalDate>() {
            @Override
            public LocalDate call(Integer integer) {
                return LocalDate.now().plusDays(integer);
            }
        });

        /*Observable<LocalDate>nextTenDays=Observable.just(1,2,3,4,5,6,7,8,9,10).map(new Func1<Integer, LocalDate>() {
            @Override
            public LocalDate call(Integer integer) {
                return LocalDate.now().plusDays(integer);
            }
        });*/

        Observable<Vacation> possibleVacations = Observable.just(City.WARSAW, City.PARIS, City.LONDON)
                .flatMap(new Func1<City, Observable<Vacation>>() {
                    @Override
                    public Observable<Vacation> call(City city) {
                        return nextTenDays.map(new Func1<LocalDate, Vacation>() {
                            @Override
                            public Vacation call(LocalDate localDate) {
                                Vacation vacation = new Vacation();
                                vacation.when = localDate;
                                vacation.where = city;
                                return vacation;
                            }
                        });
                    }
                });

        possibleVacations.subscribe(new Action1<Vacation>() {
            @Override
            public void call(Vacation vacation) {
                System.out.println(vacation.where.name + "***" + vacation.when.toString());
            }
        });
    }

    private static void testCombineLatest() {
        Observable<Long> red = Observable.interval(200, TimeUnit.MILLISECONDS);
        Observable<Long> green = Observable.interval(100, TimeUnit.MILLISECONDS);

        Observable.combineLatest(red, green, new Func2<Long, Long, String>() {
            @Override
            public String call(Long aLong, Long aLong2) {
                return String.valueOf(aLong) + "-" + String.valueOf(aLong2);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testWithLatestFrom() {
        Observable<Long> red = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Long> green = Observable.interval(200, TimeUnit.MILLISECONDS);

        red.withLatestFrom(green, new Func2<Long, Long, String>() {
            @Override
            public String call(Long aLong, Long aLong2) {
                return String.valueOf(aLong) + "-" + String.valueOf(aLong2);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Emits items from the first stream that emits or terminates and discards items from other streams
     */
    private static void testAmb() {
        Observable obs1 = Observable.interval(500, 3000, TimeUnit.MILLISECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return "Observable 1 " + aLong;
            }
        });
        Observable obs2 = Observable.interval(300, 3000, TimeUnit.MILLISECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return "Observable 2 " + aLong;
            }
        });

        Observable.amb(obs1, obs2).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                System.out.println(o);
            }
        });

        try {
            Thread.sleep(55000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Accumulates emitted items
     */
    private static void testScan() {
        //.map((number)->{return "Item "+number;})
        Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .scan(new Func2<Long, Long, Long>() {
                    @Override
                    public Long call(Long total, Long item) {
                        System.out.println("Emitted " + item);
                        return total + item;
                    }
                })
                .subscribe(item -> System.out.println(item));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void testReduce() {
        Observable.range(0, 3).reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).subscribe(result -> System.out.println(result));
    }

    private static void testCollect() {
        Observable.range(10, 20).collect(new Func0<List<Integer>>() {
            @Override
            public List<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new Action2<List<Integer>, Integer>() {
            @Override
            public void call(List<Integer> integers, Integer integer) {
                integers.add(integer);
            }
        }).subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                System.out.println(integers);
            }
        });
    }


    private static void testSingle() {
        Observable.just(1).single().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(integer);
            }
        });
    }

    private static void testDistinct() {
        Observable.just(1, 2, 3, 1, 2, 3).distinct()
                .subscribe(i -> System.out.println(i));
    }

    private static void testDistinctUntilChanged() {
        Observable.just(1, 2, 1, 1, 3, 4)
                .distinctUntilChanged()
                .subscribe(i -> System.out.println(i));
    }


    private static void testDistinctUntilChanged2() {
        Vacation v1 = new Vacation() {{
            where = City.LONDON;
            when = LocalDate.now();
        }};
        Vacation v2 = new Vacation() {{
            where = City.PARIS;
            when = LocalDate.now();
        }};
        Vacation v3 = new Vacation() {{
            where = City.PARIS;
            when = LocalDate.of(1986,4,5);
        }};

        Vacation v4 = new Vacation() {{
            where = City.WARSAW;
            when = LocalDate.of(1986,4,5);
        }};

        Observable.just(v1,v2,v3,v4).distinctUntilChanged(new Func1<Vacation, City>() {
            @Override
            public City call(Vacation vacation) {
                return vacation.where;
            }
        }).subscribe(v->System.out.println(v.where.name));
    }

    private static void testTake(){
        Observable.range(1,10).take(5).subscribe(i->System.out.println(i));
    }

    private static void testSkip(){
        Observable.range(1,10).skip(5).subscribe(i->System.out.println(i));
    }

    private static void testTakeFirst(){
        Observable.range(1,10).takeFirst(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer%3==0;
            }
        }).subscribe(i->System.out.println(i));
    }

    private static void testAll(){
        Observable.just(2,4,6,8,10).all(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer%2==0;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                System.out.println(aBoolean);
            }
        });
    }

    private static void testConcat(){
        Observable obs= Observable.range(1,10);
        Observable.concat(obs.take(3),obs.takeLast(3))
                .subscribe(i->System.out.println(i));
    }

    private static void testGroupBy(){
        Vacation v1 = new Vacation() {{
            where = City.LONDON;
            when = LocalDate.now();
        }};
        Vacation v2 = new Vacation() {{
            where = City.PARIS;
            when = LocalDate.now();
        }};
        Vacation v3 = new Vacation() {{
            where = City.PARIS;
            when = LocalDate.of(1986,4,5);
        }};

        Vacation v4 = new Vacation() {{
            where = City.WARSAW;
            when = LocalDate.of(1986,4,5);
        }};

        Observable.just(v1,v2,v3,v4).groupBy(new Func1<Vacation, City>() {
            @Override
            public City call(Vacation vacation) {
                return vacation.where;
            }
        }).subscribe(new Action1<GroupedObservable<City, Vacation>>() {
            @Override
            public void call(GroupedObservable<City, Vacation> cityVacationGroupedObservable) {
                System.out.println(cityVacationGroupedObservable.getKey().name);
                System.out.println(cityVacationGroupedObservable.toList()
                        .count().toSingle().subscribe(i->System.out.println(i)));
                /*cityVacationGroupedObservable.toList().subscribe(new Action1<List<Vacation>>() {
                    @Override
                    public void call(List<Vacation> vacations) {
                        System.out.println(vacations);
                    }
                });*/
                System.out.println("***************************");
            }
        });
    }


}
