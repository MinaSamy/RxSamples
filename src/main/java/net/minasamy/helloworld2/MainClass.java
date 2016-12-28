package net.minasamy.helloworld2;


import rx.Observable;
import rx.Observer;

/**
 * Created by Mina.Samy on 10/19/2016.
 */
public class MainClass {

    public static void main(String[] args) {
        /*Observable.create(emitter -> {
            for (int i = 0; i < 5; i++) {
                emitter.onNext(i);
            }
            emitter.onComplete();
        }).subscribe(number -> System.out.println(number));*/

        /*Observable<String> observable= Observable.create(emitter->{
            for(int i=0;i<5;i++){
                emitter.onNext(i);
            }

            emitter.onComplete();
        }).map(t->"Item "+t);*/


        //Observer<String> ob1=(Observer<String>)observable.subscribe(s -> System.out.println(s));





        /*Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onn
            }
        })*/

    }
}
