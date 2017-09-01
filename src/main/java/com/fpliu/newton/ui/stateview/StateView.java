package com.fpliu.newton.ui.stateview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
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

    private EffectTextView errorTV;

    private EffectTextView actionBtn;

    private TextView progressTV;

    private View progressPanel;

    private View errorPanel;

    private ImageView errorIv;

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

        LayoutInflater.from(context).inflate(R.layout.stateview, this);

        progressPanel = findViewById(R.id.stateview_progress_panel);
        progressTV = (TextView) findViewById(R.id.stateview_progress_text);
        progressView = (ImageView) findViewById(R.id.stateview_progress);

        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(1200);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        errorPanel = findViewById(R.id.stateview_error_panel);

        errorTV = (EffectTextView) findViewById(R.id.stateview_error_text);
        errorTV.setTextColor(Color.parseColor("#FF8B02"));
        errorTV.setEffectType(EffectFactory.TYPE_TYPER);

        actionBtn = (EffectTextView) findViewById(R.id.stateview_action_btn);
        actionBtn.setTextColor(Color.parseColor("#82cd72"));
        actionBtn.setEffectType(EffectFactory.TYPE_STROKE);

        errorIv = (ImageView) findViewById(R.id.stateview_error_image);
    }

    /**
     *
     * @param typeface {@link Typeface}
     */
    public void setTypeface(Typeface typeface) {
        if (typeface != null) {
            errorTV.setTypeface(typeface);
            actionBtn.setTypeface(typeface);
        }
    }

    /**
     *
     * @param errorTextColor {@link Color}
     */
    public void setErrorTextColor(int errorTextColor) {
        errorTV.setTextColor(errorTextColor);
    }

    /**
     *
     * @param actionTextColor {@link Color}
     */
    public void setActionTextColor(int actionTextColor) {
        actionBtn.setTextColor(actionTextColor);
    }

    /**
     *
     * @param effectType EffectFactory.TYPE_XX
     */
    public void setErrorEffectType(int effectType) {
        errorTV.setEffectType(effectType);
    }

    /**
     *
     * @param effectType EffectFactory.TYPE_XX
     */
    public void setActionEffectType(int effectType) {
        actionBtn.setEffectType(effectType);
    }

    public void setButtonVisibility(boolean isVisibility) {
        if (isVisibility) {
            actionBtn.setVisibility(VISIBLE);
        } else {
            actionBtn.setVisibility(GONE);
        }
    }

    public void showErrorBecauseNoNetworking() {
        showErrorTextWithAction("没有网络连接，请设置", "设置", () -> startNetSettingActivity(getContext()));
    }

    public void showErrorTextOnly(CharSequence text) {
        showErrorTextWithAction(text, "", null);
    }

    public void showErrorTextWithAction(CharSequence message, final String actionText, final Runnable action) {
        state = STATE_ERROR;

        rotateAnimation.cancel();
        progressView.clearAnimation();
        progressPanel.setVisibility(GONE);

        errorPanel.setVisibility(VISIBLE);
        errorIv.setVisibility(GONE);
        errorTV.setVisibility(VISIBLE);
        errorTV.setText(message);
        errorTV.animateText(message);

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

    public void showErrorImageOnly(int imageResId) {
        showErrorImageWithAction(imageResId, "", null);
    }

    public void showErrorImageWithAction(int imageResId, final String actionText, final Runnable action) {
        state = STATE_ERROR;

        rotateAnimation.cancel();
        progressView.clearAnimation();
        progressPanel.setVisibility(GONE);

        errorPanel.setVisibility(VISIBLE);
        errorTV.setVisibility(GONE);
        errorIv.setVisibility(VISIBLE);
        errorIv.setImageResource(imageResId);

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

    /**
     * 打开网络设置
     *
     * @param context 上下文
     */
    private static boolean startNetSettingActivity(Context context) {
        if (context == null) {
            return false;
        }

        Intent intent;
        if (Build.VERSION.SDK_INT < 14) {
            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent(Settings.ACTION_SETTINGS);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("StateView", "startNetSettingActivity()", e);
            return false;
        }
    }
}
