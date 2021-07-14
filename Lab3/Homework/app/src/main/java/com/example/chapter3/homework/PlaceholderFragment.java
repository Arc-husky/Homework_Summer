package com.example.chapter3.homework;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.chapter3.homework.placeholder.PlaceholderContent;

public class PlaceholderFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        return inflater.inflate(R.layout.fragment_placeholder, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
                ObjectAnimator fade_in = ObjectAnimator.ofFloat(getView().findViewById(R.id.list),
                        "alpha", 0.0f, 1.0f);
                ObjectAnimator fade_out = ObjectAnimator.ofFloat(getView().findViewById(R.id.animation),
                        "alpha", 1.0f, 0.0f);
                fade_in.setDuration(500);
                fade_in.setRepeatCount(0);
                fade_out.setDuration(500);
                fade_out.setRepeatCount(0);
                RecyclerView recyclerView = getView().findViewById(R.id.list);
                recyclerView.setAlpha(0);
                Context context = getView().getContext();
                recyclerView.setAdapter(new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS));
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(fade_out,fade_in);
                animatorSet.start();
            }
        }, 5000);
    }

}
