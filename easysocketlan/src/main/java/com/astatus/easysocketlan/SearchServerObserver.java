package com.astatus.easysocketlan;

import com.astatus.easysocketlan.SearchServerObservable;
import com.astatus.easysocketlan.listener.ILanSearchServerListener;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/10/22.
 */

public class SearchServerObserver implements Observer<com.astatus.easysocketlan.SearchServerObservable.SearchState> {

    private ILanSearchServerListener mListener;

    SearchServerObserver(ILanSearchServerListener listener){
        mListener = listener;
    }
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(SearchServerObservable.SearchState searchState) {

        switch (searchState){
            case SS_START:
                mListener.onSearchStart();
                break;
            case SS_SEARCH:
                mListener.onSearching();
                break;
        }
    }

    @Override
    public void onError(Throwable e) {
        mListener.onSearchError(e.getMessage());
    }

    @Override
    public void onComplete() {
        mListener.onSearchEnd();
    }
}
