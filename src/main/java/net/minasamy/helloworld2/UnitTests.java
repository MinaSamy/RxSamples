package net.minasamy.helloworld2;

import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;

import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mina.Samy on 12/18/2016.
 */
public class UnitTests {





    @Test
    public void shouldApplyConcantMapInOrder(){
        List<String>list= Observable.range(1,3)
                .concatMap(x->Observable.just(x,-x))
                .map(Object::toString)
                .toList()
                .toBlocking()
                .single();
        Assert.assertThat(list,contains("1","-1","2","-2","3","-3"));
    }
}
