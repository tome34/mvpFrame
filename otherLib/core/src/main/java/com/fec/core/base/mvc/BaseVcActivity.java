package com.fec.core.base.mvc;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.fec.core.R;
import com.fec.core.base.BaseApplication;
import com.fec.core.base.mvp.inter.IView;
import com.fec.core.bean.BaseEventbusBean;
import com.fec.core.util.StatuBarCompat;
import com.fec.core.util.ToastUtils;
import com.fec.core.widget.HUDFactory;
import com.kaopiz.kprogresshud.KProgressHUD;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import com.orhanobut.logger.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @Created by TOME .
 * @时间 2018/6/26 17:40
 * @描述 ${MVC模式的Base Activity}
 */

public abstract class BaseVcActivity extends AppCompatActivity implements IView {

    private Unbinder unBinder;
    protected boolean regEvent;
    public BaseVcActivity mActivity ;
    public KProgressHUD kProgressHUD;
    protected boolean isDestory = false;
    //管理事件流订阅的生命周期CompositeDisposable
    private CompositeDisposable compositeDisposable;

    public IView mView = this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unBinder = ButterKnife.bind(this);
        //加入activity管理
        BaseApplication.getAppContext().getActivityControl().addActivity(this);
        //沉浸式状态栏
        //setImmeriveStatuBar();
        mActivity = this ;

        initTitle();
        initView();
        if (regEvent){
            EventBus.getDefault().register(this);

        }
    }

    /**
     * rxjava管理订阅者
     */
    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEventbusBean event) {
        onEvent(event);
    }

    protected void onEvent(BaseEventbusBean event) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showHUD(String msg) {
        if (isDestory){
            return;
        }
        if (TextUtils.isEmpty(msg)){
            msg = getString(R.string.loading);
        }
        kProgressHUD = HUDFactory.getInstance().creatHUD(this);
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(TextUtils.isEmpty(msg) ? getString(R.string.loading) : msg)
               // .setLabel(null)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f).show();
    }

    @Override
    public void dismissHUD() {
        if (null != kProgressHUD && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

    /**
     * 提示网络请求错误信息
     * @param msg
     * @param code
     */
    @Override
    public void showError(String msg, String code) {
        String mCode ="-1";
        if (mCode.equals(code)){
            ToastUtils.showShort(mActivity, msg);
        }

    }

    /**
     * 空界面显示
     */
    @Override
    public void showNormal() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showEmptyView() {

    }

    @Override
    public void showError() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i("当前运行的activity:" + getClass().getName());
    }

    /**
     * 销毁
     * @param v
     */
    public void back(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除订阅关系
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }

        if (unBinder != null) {
            unBinder.unbind();
        }
        if (regEvent) {
            EventBus.getDefault().unregister(this);
        }
        isDestory = true;
        dismissHUD();
        //移除类
        BaseApplication.getAppContext().getActivityControl().removeActivity(this);

    }

    /**
     * 沉浸式状态栏
     */
    protected void setImmeriveStatuBar() {
        StatuBarCompat.setImmersiveStatusBar(true, Color.WHITE, this);

    }

    /**
     * 获取当前Activity的UI布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化标题
     */
    protected abstract void initTitle();

    /**
     * 初始化数据
     */
    protected abstract void initView();



}
