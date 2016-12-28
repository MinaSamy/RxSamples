package net.minasamy.helloworld2;

import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Mina.Samy on 11/29/2016.
 */
public class Chapter5 {

    public static void main(String[]args){
        //startSimpleServer();
        /*try {
            HttpTcpNettyServer.startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //CurrencyConverterServer.startServer();
        testCompletableFuture();

    }

    private static void startSimpleServer(){
        try {
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        requestServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println("startSimpleServer InterruptedException"+e.toString());
                    }
                }
            }).start();*/
            SimpleServer.startServer();
        } catch (IOException e) {
            System.out.println("startSimpleServer IOException"+e.toString());
        }
    }


    private static void requestServer() throws IOException, InterruptedException {
        URL url=new URL("http://localhost:8000/");
        HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("keep-alive","no");
        for(int i=0;i<5;i++){

            InputStream inputStream= urlConnection.getInputStream();
            Thread.sleep(300);
            inputStream.close();
            Thread.sleep(500);
            urlConnection.disconnect();
        }
    }


    private static void testCompletableFuture() {
        CompletableFuture<Void>future= CompletableFuture.supplyAsync(new Supplier<Void>() {
            @Override
            public Void get() {
                doWork();
                for(int i=0;i<10;i++){
                    if(i==9){
                        int y=i/0;
                    }
                }
                return null;
            }
        })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        System.out.println("Exception "+throwable.getMessage());
                        return null;
                    }
                });;



        future.thenApplyAsync(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid)  {

                System.out.println("Finish 2");
                return null;
            }
        });

        future.thenApplyAsync(new Function<Void, Void>() {
            @Override
            public Void apply(Void aVoid) {

                System.out.println("Finish 1");
                return null;
            }
        });




        System.out.println("Here");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }


    public static  String doWork2(String message1,String message2){
        System.out.println("Start doing "+message1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished doing "+message2);
        return "";
    }

    private static void testCompletableFuture2(){
        CompletableFuture<String>future=CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "Hello";
            }
        });


        CompletableFuture<String>future2=CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "There";
            }
        });

        future.thenCombine(future2,Chapter5::doWork2);
    }




    private static void applyMethod() throws Exception {
        System.out.println("Finish 1");
        for(int i=0;i<10;i++){
            if(i==5){
                throw new Exception("Exception 1");
            }
        }
    }

    private static void doWork(){
        System.out.println("Start doing work");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finished doing work");
    }

}
