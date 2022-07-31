package com.kt.rxjavademo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Disposable mDisposable;
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.txt_text);

        User user = new User(1,"User 1");
        Observable<String> observable = user.getNamedeferObservable();
        Observer<String> observer = getObserver();

        user.setName("User 2");
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observer<String> getObserver(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.e("Test1","onSubscribe");
                mDisposable = d;
            }
            @Override
            public void onNext(@NonNull String string) {
                Log.e("Test1","onNext : "+string);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("Test1","onError");
            }
            @Override
            public void onComplete() {
                Log.e("Test1","onComplete");
            }
        };
    }
    private Observer<Serializable> getObserverUsers(){
        return new Observer<Serializable>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.e("Test1","onSubscribe");
                mDisposable = d;
            }
            @Override
            public void onNext(@NonNull Serializable serializable) {
                if (serializable instanceof User[]){
                    User[] user = (User[]) serializable;
                    for (User user1: user){
                        Log.e("Test1","onNext user[] :"+user1.toString());
                    }
                }else if (serializable instanceof String){
                    Log.e("Test1","onNext String :"+serializable.toString());
                }else if (serializable instanceof User){
                    User user = (User) serializable;
                    Log.e("Test1","onNext User :"+user.toString());
                }else if (serializable instanceof List){
                    List<User> list = (List<User>) serializable;
                    for (User user1: list){
                        Log.e("Test1","onNext list :"+user1.toString());
                    }
                }
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("Test1","onError");
            }
            @Override
            public void onComplete() {
                Log.e("Test1","onComplete");
            }
        };
    }

    private Observable<Integer> getObservable(){
        return Observable.range(0,3).repeat(2);
    }

    private Observable getObservableUsers(){
        List<User> listUser = getListUsers();

        User user1 = new User(1,"User 1");
        User user2 = new User(2,"User 2");
        User user3 = new User(3,"User 3");
        User[] users = new User[]{user1,user2,user3};
        String user4 = "User 4";
        User user5 = new User(5,"User 5");

        return Observable.just(users,user4,user5,listUser);

        //cách 1
        //return Observable.fromArray(user1,user2,user3);
        //cách 2
        //return Observable.fromArray(users);

//        return Observable.create(new ObservableOnSubscribe<User>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<User> emitter) throws Throwable {
//                Log.e("Test1","Thread observable:"+Thread.currentThread().getName());
//                if (listUser == null || listUser.isEmpty()){
//                    emitter.onError(new Exception());
//                }
//                for (User user: listUser){
//                    if (!emitter.isDisposed()){
//                        emitter.onNext(user);
//                    }
//                }
//                if (!emitter.isDisposed()){
//                    emitter.onComplete();
//                }
//            }
//        });
    }

    private List<User> getListUsers(){
        List<User> list = new ArrayList<>();
        list.add(new User(1,"User 1"));
        list.add(new User(2,"User 2"));
        list.add(new User(3,"User 3"));
        list.add(new User(4,"User 4"));
        list.add(new User(5,"User 5"));
        list.add(new User(6,"User 6"));
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null){
            mDisposable.dispose();
        }
    }
}