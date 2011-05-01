package com.hackathon;

//  The original popup window code came from www.londatiga.net.
//  I've modified it.

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class QuickActionMenu extends VPopupWindow {
    private final View root;
    private final ImageView arrowUp;   // arrow that points up to anchor view
    private final ImageView arrowDown; // arrow that points down to anchor view
    private final Animation trackAnimation;     // animation for track containins menu items
    private final LayoutInflater inflater;
    private final Context ctx;

    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_AUTO = 4;

    private int animationStyle;     // style for popup animation
    private boolean animateTrack;   // true if track should be animated in
    private ViewGroup track;        // container for menu items
    private ArrayList<ActionItem> actionList;       // menu items

    public QuickActionMenu(View anchor) {
        super(anchor);

        actionList = new ArrayList<ActionItem>();
        ctx = anchor.getContext();
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //  Background frame of popup. By default this appears in a HorizontalScrollViewer and it
        //  will scroll if there are more entries than there is room to display.
        root = (ViewGroup) inflater.inflate(R.layout.quickactionmenu, null);    // background frame of popup

        arrowDown = (ImageView) root.findViewById(R.id.quickaction_arrow_down);
        arrowUp = (ImageView) root.findViewById(R.id.quickaction_arrow_up);

        setContentView(root);

        trackAnimation = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.rail);

        //  This interpolation causes the actionitems to slide left past the left edge of the screen,
        //  then bounce back right to the final position.
        trackAnimation.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                //  Equation for graphing: 1.2 - ((x * 1.6) - 1.1) ^ 2
                final float inner = (v * 1.55f) - 1.1f;
                return 1.2f - inner * inner;
            }
        });

        track = (ViewGroup) root.findViewById(R.id.quickaction_tracks);
        animationStyle = ANIM_AUTO;
        animateTrack = true;
    }

    public void animateTrack(boolean animateTrack) {
        this.animateTrack = animateTrack;
    }

    public void setAnimationStyle(int animationStyle) {
        this.animationStyle = animationStyle;
    }

    public void addActionItem(ActionItem actionItem) {
        actionList.add(actionItem);
    }

    public void show() {
        preShow();

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootWidth = root.getMeasuredWidth();
        int rootHeight = root.getMeasuredHeight();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();

        int xPos = (screenWidth - rootWidth) / 2;
        int yPos = anchorRect.top - rootHeight;
        boolean onTop = true;

        //  display on bottom
        if (rootHeight > anchorRect.top) {
            yPos = anchorRect.bottom;
            onTop = false;
        }

        showArrow(((onTop) ? R.id.quickaction_arrow_down : R.id.quickaction_arrow_up), anchorRect.centerX());
        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        createActionList();
        showWindowAtLocation(this.anchor, Gravity.NO_GRAVITY,  xPos, yPos);
        if (animateTrack) track.startAnimation(trackAnimation);
    }

    private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
        int arrowPos = requestedX - arrowUp.getMeasuredWidth() / 2;

        switch(animationStyle) {
            case ANIM_GROW_FROM_LEFT:
                setWindowAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                break;

            case ANIM_GROW_FROM_RIGHT:
                setWindowAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                break;

            case ANIM_GROW_FROM_CENTER:
                setWindowAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                break;

            case ANIM_AUTO:
                if (arrowPos <= screenWidth / 4) {
                    setWindowAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                    setWindowAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                } else {
                    setWindowAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                }
                break;

        }
    }

    private void createActionList() {
        View view;
        String title;
        Drawable icon;
        View.OnClickListener listener;
        int index = 1;

        //  Can't remove all views because that would remove the views in the layout as well.

        for (int i = track.getChildCount() - 1; i > 0; i--) {
            //  Can I remove from the collection while interating it? Betcha no!
            //  Actually yes, just iterate backwards.
            View v = track.getChildAt(i);
            if (v != null) {
                //  Don't remove the 'grip' graphics on the border of the slider.
                if (v.getId() != R.id.quickaction_track_left_grip && v.getId() != R.id.quickaction_track_right_grip) {
                    track.removeViewAt(i);
                }
            }
        }

        for (ActionItem anActionItem : actionList) {
            title = anActionItem.getTitle();
            icon = anActionItem.getIcon();
            listener = anActionItem.getOnClickListener();
            view = getActionItem(title, icon, listener);
            view.setFocusable(true);
            view.setClickable(true);

            track.addView(view, index);
            index++;
        }
    }

    private View getActionItem(String title, Drawable icon, View.OnClickListener listener) {
        LinearLayout container = (LinearLayout) inflater.inflate(R.layout.quickaction_action_item, null);
        ImageView image = (ImageView) container.findViewById(R.id.quickaction_item_icon);
        TextView text = (TextView) container.findViewById(R.id.quickaction_item_title);

        if (icon != null) {
            image.setImageDrawable(icon);
        } else {
            image.setVisibility(View.GONE);
        }

        if (title != null) {
            text.setText(title);
        } else {
            text.setVisibility(View.GONE);
        }

        if (listener != null) {
            container.setOnClickListener(new ActionItemClickListener(listener));
        }
        return container;
    }

    private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.quickaction_arrow_up) ? arrowUp : arrowDown;
        final View hideArrow = (whichArrow == R.id.quickaction_arrow_up) ? arrowDown : arrowUp;
        final int arrowWidth = arrowUp.getMeasuredWidth();
        showArrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        param.leftMargin = requestedX - arrowWidth / 2;
        hideArrow.setVisibility(View.INVISIBLE);
    }

    private class ActionItemClickListener implements View.OnClickListener {

        View.OnClickListener itemOnClickListener;
        private ActionItemClickListener(View.OnClickListener itemOnClickListener) {
            this.itemOnClickListener = itemOnClickListener;
        }

        @Override
        public void onClick(View view) {
            QuickActionMenu.this.dismiss();
            if (itemOnClickListener != null) {
                itemOnClickListener.onClick(view);
            }
        }
    }



}
