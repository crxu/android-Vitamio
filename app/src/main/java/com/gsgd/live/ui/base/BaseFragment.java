package com.gsgd.live.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    private CompositeDisposable mCompositeDisposable;
    protected Activity mContext;
    protected boolean isFirst = true;
    protected boolean isViewCreate = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        clearDisposable();
        isViewCreate = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDisposable();
        isViewCreate = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        doOnCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreate = true;
        initParams();
        initEvents();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();

        } else {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();

        } else {
            onInVisible();
        }
    }

    /**
     * 当界面可见时的操作
     */
    protected void onVisible() {
        if (isFirst) {
            lazyLoad();
            isFirst = false;
        }
    }

    /**
     * 当界面不可见时的操作
     */
    protected void onInVisible() {

    }

    /**
     * 数据懒加载
     */
    protected void lazyLoad() {

    }

    public void addDisposable(Disposable disposable) {
        if (null == disposable) {
            return;
        }
        if (null == mCompositeDisposable) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    public void clearDisposable() {
        if (null != mCompositeDisposable) {
            mCompositeDisposable.clear();
        }
    }

    protected void doOnCreate(Bundle savedInstanceState) {
    }

    protected abstract int getLayoutResId();

    protected void initParams() {
        if (getUserVisibleHint()) {
            onVisible();
        }
    }

    protected void initEvents() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmptyEvent(Void v) {
    }

    /**
     * 处理按键返回事件
     *
     * @return
     */
    public boolean onBackPressed() {
        return true;
    }

}
