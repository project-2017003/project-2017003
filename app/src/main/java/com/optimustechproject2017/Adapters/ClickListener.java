package com.optimustechproject2017.Adapters;

import android.view.View;

/**
 * Created by Hemanth Kandula on 11-012-2017.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
