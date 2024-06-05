package com.example.mankomania.screens;

import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.Objects;

public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private OnSwipeListener onSwipeListener;

    public SwipeGestureListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffY = e2.getY() - Objects.requireNonNull(e1).getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) < Math.abs(diffY) && Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD && diffY<0) {
            onSwipeUp();
        }
        return true;
    }

    private void onSwipeUp() {
        onSwipeListener.onSwipeUp();
    }

    public interface OnSwipeListener {
        void onSwipeUp();
    }
}
