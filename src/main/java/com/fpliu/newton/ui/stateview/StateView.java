package com.fpliu.newton.ui.stateview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fpliu.newton.ui.effecttextview.EffectFactory;
import com.fpliu.newton.ui.effecttextview.EffectTextView;

/**
 * 显示状态的视图
 *
 * @author 792793182@qq.com 2014-10-14
 */
public class StateView extends RelativeLayout {

    public static final int STATE_PROGRESS = 1;

    public static final int STATE_ERROR = 2;

    private int state = STATE_PROGRESS;

    private ImageView progressView;

    private ImageView errorIV;

    private EffectTextView errorTV;

    private EffectTextView actionBtn;

    private TextView progressTV;

    private View progressPanel;

    private View errorPanel;

    private RotateAnimation rotateAnimation;

    public StateView(Context context) {
        this(context, null);
    }

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        setClickable(true);

        setBackgroundColor(Color.WHITE);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        View view = LayoutInflater.from(context).inflate(R.layout.stateview, this, false);
        addView(view, lp);

        progressPanel = findViewById(R.id.state_view_progress_panel);
        progressTV = (TextView) findViewById(R.id.state_view_progress_text);
        progressView = (ImageView) findViewById(R.id.state_view_progress);

        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(1200);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        errorPanel = findViewById(R.id.state_view_error_panel);

        errorIV = (ImageView) findViewById(R.id.state_view_error_image);

        errorTV = (EffectTextView) findViewById(R.id.state_view_error_text);

        actionBtn = (EffectTextView) findViewById(R.id.state_view_action_btn);
        actionBtn.setEffectType(EffectFactory.TYPE_STROKE);
    }

    /**
     * @param typeface {@link Typeface}
     */
    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            errorTV.setTypeface(typeface);
            actionBtn.setTypeface(typeface);
        }
    }

    /**
     * @param errorTextColor {@link Color}
     */
    public void setErrorTextColor(int errorTextColor) {
        errorTV.setTextColor(errorTextColor);
    }

    /**
     * @param actionTextColor {@link Color}
     */
    public void setActionTextColor(int actionTextColor) {
        actionBtn.setTextColor(actionTextColor);
    }

    /**
     * @param effectType EffectFactory.TYPE_XX
     */
    public void setErrorEffectType(int effectType) {
        errorTV.setEffectType(effectType);
    }

    /**
     * @param effectType EffectFactory.TYPE_XX
     */
    public void setActionEffectType(int effectType) {
        actionBtn.setEffectType(effectType);
    }

    public void setActionButtonVisibility(boolean isVisibility) {
        if (isVisibility) {
            actionBtn.setVisibility(VISIBLE);
        } else {
            actionBtn.setVisibility(GONE);
        }
    }

    public void showErrorImage(int imageResId) {
        showErrorImageAndTextWithAction(imageResId, null, null, null);
    }

    public void showErrorText(CharSequence text) {
        showErrorImageAndTextWithAction(0, text, null, null);
    }

    public void showErrorImageAndText(int imageResId, CharSequence text) {
        showErrorImageAndTextWithAction(imageResId, text, null, null);
    }

    public void showErrorImageWithAction(int imageResId, final String actionText, final Runnable action) {
        showErrorImageAndTextWithAction(imageResId, null, actionText, action);
    }

    public void showErrorTextWithAction(CharSequence message, final String actionText, final Runnable action) {
        showErrorImageAndTextWithAction(0, message, actionText, action);
    }

    public void showErrorImageAndTextWithAction(int imageResId, CharSequence message, final String actionText, final Runnable action) {
        state = STATE_ERROR;

        rotateAnimation.cancel();
        progressView.clearAnimation();
        progressPanel.setVisibility(GONE);

        if (imageResId == 0) {
            if (TextUtils.isEmpty(message)) {
                errorPanel.setVisibility(GONE);
            } else {
                errorPanel.setVisibility(VISIBLE);
                errorIV.setVisibility(GONE);
                errorTV.setVisibility(VISIBLE);
                errorTV.setText(message);
                if (message.length() >= 10) {
                    errorTV.noEffect();
                } else {
                    errorTV.setEffectType(EffectFactory.TYPE_TYPER);
                    errorTV.animateText(message);
                }
            }
        } else {
            errorPanel.setVisibility(VISIBLE);
            errorIV.setVisibility(VISIBLE);
            errorIV.setImageResource(imageResId);
            if (TextUtils.isEmpty(message)) {
                errorTV.setVisibility(GONE);
            } else {
                errorTV.setVisibility(VISIBLE);
                errorTV.setText(message);
                if (message.length() >= 10) {
                    errorTV.noEffect();
                } else {
                    errorTV.setEffectType(EffectFactory.TYPE_TYPER);
                    errorTV.animateText(message);
                }
            }
        }

        if (!TextUtils.isEmpty(actionText) && action != null) {
            actionBtn.setOnClickListener(v -> action.run());
            actionBtn.setVisibility(VISIBLE);
            postDelayed(() -> {
                if (actionBtn != null) {
                    actionBtn.setText(actionText);
                    actionBtn.animateText(actionText);
                }
            }, 1000);
        } else {
            actionBtn.setVisibility(GONE);
        }
    }

    public void showProgress(CharSequence text) {
        state = STATE_PROGRESS;

        errorPanel.setVisibility(GONE);
        progressPanel.setVisibility(VISIBLE);

        progressView.setAnimation(rotateAnimation);
        rotateAnimation.startNow();

        progressTV.setText(text);
    }

    public int getState() {
        return state;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (rotateAnimation != null) {
            if (visibility == GONE) {
                rotateAnimation.cancel();
            } else if (visibility == VISIBLE) {
                rotateAnimation.startNow();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        removeAllViews();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.childrenStates = new SparseArray();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).saveHierarchyState(ss.childrenStates);
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).restoreHierarchyState(ss.childrenStates);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    static class SavedState extends BaseSavedState {
        SparseArray childrenStates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(childrenStates);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
