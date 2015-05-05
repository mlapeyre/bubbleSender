package com.bubbes.bubblesender.contacts;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bubbes.bubblesender.R;
import com.bubbes.bubblesender.TimedPhoneEntry;
import com.bubbes.bubblesender.utils.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

public class TimedPhoneEntryAdapter extends PhoneEntryAdapter<TimedPhoneEntry>{
    /**
     * Unique constructor...
     *
     * @param activity     The activity which has created me :)
     * @param rowLayoutId  The layout id to use for creating item view
     * @param phoneEntries The initial phone entry list
     */
    public TimedPhoneEntryAdapter(Activity activity, int rowLayoutId, ArrayList<TimedPhoneEntry> phoneEntries) {
        super(activity, rowLayoutId, phoneEntries);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TimedPhoneEntry item = this.getItem(position);
        TextView timeField = ViewHolder.get(view, R.id.ccontTime);
        timeField.setText(new Date(item.getTime()).toString()); //TODO
        return view;
    }

}
