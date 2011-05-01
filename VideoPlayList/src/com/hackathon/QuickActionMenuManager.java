package com.hackathon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class QuickActionMenuManager {
    private View anchorView;
    private QuickActionMenu qam = null;
    VPopupWindow vPopupWindow = null;

    public QuickActionMenuManager(View anchorView) {
        this.anchorView = anchorView;
    }

    public void initializeQuickActionMenu(final OnViewClickListenerWrapper searchGoCommandListener, final OnViewClickListenerWrapper searchCancelCommandListener) {
        qam = new QuickActionMenu(anchorView);
        qam.addActionItem(new ActionItem("Search", null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                LayoutInflater inflater = (LayoutInflater) anchorView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                vPopupWindow = new VPopupWindow(anchorView);
                View searchRoot = (ViewGroup) inflater.inflate(R.layout.search_layout, null);
                vPopupWindow.setContentView(searchRoot);
                searchRoot.findViewById(R.id.youtube_search_go).setOnClickListener(searchGoCommandListener);
                searchRoot.findViewById(R.id.youtube_search_cancel).setOnClickListener(searchCancelCommandListener);


                vPopupWindow.setWindowAnimationStyle(QuickActionMenu.ANIM_AUTO);
                vPopupWindow.showLikeQuickAction(0, 50);

            }
        }));
        qam.addActionItem(new ActionItem("Refresh", null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: refresh playlist
                Toast.makeText(view.getContext(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        }));
        qam.setAnimationStyle(QuickActionMenu.ANIM_AUTO);
        anchorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qam.show();
            }
        });
    }

    public void destroyQuickActionMenu() {
        //  Necessary to avoid leaking windows on configuration changes
        if (vPopupWindow != null) {
            vPopupWindow.dismiss();
        }
        if (qam != null) {
            qam.dismiss();
//            anchorView = null;
        }
    }
}
