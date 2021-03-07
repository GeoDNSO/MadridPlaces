package com.example.App.utilities;

import android.view.View;
import android.widget.TextView;

public class ViewListenerUtilities {


    public static void makeTextViewExpandable(TextView textView, Boolean isCollapsed){
        textView.setOnClickListener(new View.OnClickListener() {
            private boolean aisCollapsed = isCollapsed;
            @Override
            public void onClick(View v) {
                if (aisCollapsed) {
                    textView.setMaxLines(Integer.MAX_VALUE);
                } else {
                    textView.setMaxLines(TextViewExpandableUtil.linesLimitAtPlaceDesc);
                }
                aisCollapsed = !aisCollapsed;
            }
        });
    }
}
