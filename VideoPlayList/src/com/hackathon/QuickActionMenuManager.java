package com.hackathon;

import android.view.View;
import android.widget.Toast;

public class QuickActionMenuManager {
    private View anchorView;
    private QuickActionMenu qam = null;

    public QuickActionMenuManager(View anchorView) {
        this.anchorView = anchorView;
    }

    public void initializeQuickActionMenu() {
        qam = new QuickActionMenu(anchorView);
        qam.addActionItem(new ActionItem("Search", null, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: start up search activity
                Toast.makeText(view.getContext(), "Search YouTube", Toast.LENGTH_SHORT).show();
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
        if (qam != null) {
            qam.dismiss();
            anchorView = null;
        }
    }
}
