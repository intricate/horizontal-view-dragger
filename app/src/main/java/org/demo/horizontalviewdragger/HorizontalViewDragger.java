package org.demo.horizontalviewdragger;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: Intricate
 * Date: 6/11/2016
 * Description: An extension of ViewGroup that encapsulates horizontal dragging behaviour.
 */
public class HorizontalViewDragger extends ViewGroup {

    private ViewDragHelper viewDragHelper;
    private View draggableView;

    private OnDragCompleteListener onDragCompleteListener;
    private int leftDragCompleteBound, rightDragCompleteBound;
    private int dragCompleteState;

    private int leftDragBound, rightDragBound;
    private boolean leftDragEnabled, rightDragEnabled;
    private int dragBorder;
    private float dragSlop = 3.0f;

    private float actionDownX;

    public static class Direction{
        public static int NONE = 0,
                LEFT = 1,
                RIGHT = 2;
    }

    public interface OnDragCompleteListener{
        void onDragCompleted(int direction);
    }

    private class ViewDragHelperCallback extends ViewDragHelper.Callback{
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return (child == draggableView);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            dragBorder = left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int settleX = 0;
            if (dragBorder >= rightDragCompleteBound){
                settleX = rightDragCompleteBound;
                dragCompleteState = Direction.RIGHT;
            }else if (dragBorder <= leftDragCompleteBound){
                settleX = leftDragCompleteBound;
                dragCompleteState = Direction.LEFT;
            }else{
                settleX = releasedChild.getPaddingLeft();
                dragCompleteState = Direction.NONE;
            }

            if (dragCompleteState != Direction.NONE){
                if (onDragCompleteListener != null){
                    onDragCompleteListener.onDragCompleted(dragCompleteState);
                }
            }

            if(viewDragHelper.settleCapturedViewAt(settleX, 0)){
                ViewCompat.postInvalidateOnAnimation(HorizontalViewDragger.this);
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dy) {
            if (isLeftDragEnabled()){
                leftDragBound = getPaddingLeft() - getWidth();
                leftDragCompleteBound = getPaddingLeft() - getWidth()/2;
            }
            if (isRightDragEnabled()){
                rightDragBound = getWidth();
                rightDragCompleteBound = getWidth()/2;
            }
            return Math.min(Math.max(left, leftDragBound), rightDragBound);
        }

        @Override
        public int clampViewPositionVertical(View child, int left, int dy) {
            return 0;
        }
    }

    public HorizontalViewDragger(Context context) {
        super(context, null);
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());
    }
    public HorizontalViewDragger(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (viewDragHelper.isViewUnder(draggableView, (int)event.getX(), (int)event.getY())){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    actionDownX = event.getX();
                    viewDragHelper.processTouchEvent(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = Math.abs(event.getX() - actionDownX);
                    if (deltaX > dragSlop) {
                        viewDragHelper.processTouchEvent(event);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    viewDragHelper.processTouchEvent(event);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    viewDragHelper.processTouchEvent(event);
                    break;
            }
            return true;
        }else{
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return (viewDragHelper.isViewUnder(draggableView, (int)event.getX(), (int)event.getY()) &&
                viewDragHelper.shouldInterceptTouchEvent(event));
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) {
                return;
            }
            child.layout(child.getPaddingLeft(), child.getTop(), child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    // http://flavienlaurent.com/blog/2013/08/28/each-navigation-drawer-hides-a-viewdraghelper/
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec),
                maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    public void settleViewAtStart(){
        if (!isInLayout()) {
            requestLayout();
        }
    }

    public void setDraggableView(View draggableView){ this.draggableView = draggableView; }
    public void setOnDragCompleteListener(OnDragCompleteListener onDragCompleteListener){ this.onDragCompleteListener = onDragCompleteListener; }
    public void setLeftDragCompleteBound(int leftDragCompleteBound){ this.leftDragCompleteBound = leftDragCompleteBound; }
    public void setRightDragCompleteBound(int rightDragCompleteBound){ this.rightDragCompleteBound = rightDragCompleteBound; }
    public void setLeftDragEnabled(boolean leftDragEnabled){ this.leftDragEnabled = leftDragEnabled; }
    public void setRightDragEnabled(boolean rightDragEnabled){ this.rightDragEnabled = rightDragEnabled; }

    public View getDraggableView(){ return draggableView; }
    public OnDragCompleteListener getOnDragCompleteListener(){ return onDragCompleteListener; }
    public int getDragCompleteState(){ return dragCompleteState; }
    public int getLeftDragCompleteBound(){ return leftDragCompleteBound; }
    public int getRightDragCompleteBound(){ return rightDragCompleteBound; }
    public boolean isLeftDragEnabled(){ return leftDragEnabled; }
    public boolean isRightDragEnabled(){ return rightDragEnabled; }
}
