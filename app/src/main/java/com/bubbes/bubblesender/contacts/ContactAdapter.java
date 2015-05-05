package com.bubbes.bubblesender.contacts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubbes.bubblesender.PhoneEntry;
import com.bubbes.bubblesender.R;
import com.bubbes.bubblesender.utils.Assertion;
import com.bubbes.bubblesender.utils.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter implements Filterable {

    //==============================================================================================
    // Constructor
    //==============================================================================================
    /**
     * Activity which has called me, used for inflating the views
     */
    private final Activity activity;
    /**
     * The elements which are currently displayed
     */
    private List<PhoneEntry> displayedElements;
    /**
     * The layout id used for the items of the view
     */
    private final int rowLayout;
    /**
     * The complete list of the phone entries
     */
    private final ArrayList<PhoneEntry> completePhoneList;
    /**
     * The filter which may be used on this adapter
     */
    private Filter contactFilter;
    /**
     * A mutex on the complete list of phone entry, mandatory because the
     * Filter#performFiltering is not called in the UIThread....
     */
    private final Object COMPLETE_PHONE_LIST_MUTEX = new Object();
    /**
     * The filter currently in use may be null or even empty; not locked to be used in UIThread
     */
    private CharSequence currentFilter;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    /**
     * Unique constructor...
     *
     * @param activity     The activity which has created me :)
     * @param rowLayoutId  The layout id to use for creating item view
     * @param phoneEntries The initial phone entry list
     */
    public ContactAdapter(Activity activity, int rowLayoutId, ArrayList<PhoneEntry> phoneEntries) {
        this.activity = activity;
        this.rowLayout = rowLayoutId;
        this.completePhoneList = phoneEntries;
        this.displayedElements = new ArrayList<>(phoneEntries);
    }

    //==============================================================================================
    // BaseAdapter implementation
    //==============================================================================================
    @Override
    public int getCount() {
        Assertion.assertIsMainThread();
        return this.displayedElements.size();
    }

    @Override
    public PhoneEntry getItem(int position) {
        Assertion.assertIsMainThread();
        return this.displayedElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Assertion.assertIsMainThread();
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(rowLayout, null);
        }

        PhoneEntry phoneEntry = this.getItem(position);

        TextView contactName = ViewHolder.get(view, R.id.ccontName);
        ImageView image = ViewHolder.get(view, R.id.ccontImage);
        TextView phoneNumber = ViewHolder.get(view, R.id.ccontNo);
        TextView type = ViewHolder.get(view, R.id.ccontType);

        type.setText(phoneEntry.getType());
        phoneNumber.setText(phoneEntry.getPhone());
        contactName.setText(phoneEntry.getName());
        String imageUri = phoneEntry.getImageThumbUri();
        Picasso.with(activity).load(imageUri).placeholder(R.drawable.ic_contact_picture).into(image);
        return view;
    }

    //==============================================================================================
    // Filterable implementations
    //==============================================================================================
    public Filter getFilter() {
        Assertion.assertIsMainThread();
        if (this.contactFilter == null) {
            this.contactFilter = new ContactFilter();
        }
        return this.contactFilter;
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Add the given list to the current phone list
     *
     * @param entries The phone entries to add to the adapter
     */
    public void addAll(List<PhoneEntry> entries) {
        Assertion.assertIsMainThread();
        synchronized (COMPLETE_PHONE_LIST_MUTEX) {
            this.completePhoneList.addAll(entries);
        }
        if (!isEmptyFilter(this.currentFilter)) {
            for (PhoneEntry entry : entries) {
                if (accept(entry, this.currentFilter)) {
                    this.displayedElements.add(entry);
                }
            }
        }
        this.notifyDataSetChanged();
    }
    //==============================================================================================
    // Inner classes
    //==============================================================================================

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (isEmptyFilter(constraint)) {
                ArrayList<PhoneEntry> phoneEntries;
                synchronized (COMPLETE_PHONE_LIST_MUTEX) {
                    phoneEntries = new ArrayList<>(completePhoneList);
                }
                return createFilterResult(phoneEntries);
            } else {
                String toLowerCase = constraint.toString().toLowerCase();
                ArrayList<PhoneEntry> phoneEntries = new ArrayList<>();
                synchronized (COMPLETE_PHONE_LIST_MUTEX) {
                    for (PhoneEntry phoneEntry : completePhoneList) {
                        if (accept(phoneEntry, toLowerCase)) {
                            phoneEntries.add(phoneEntry);
                        }
                    }
                }
                return createFilterResult(phoneEntries);
            }
        }

        private FilterResults createFilterResult(ArrayList<PhoneEntry> phoneEntries) {
            FilterResults filterResults = new FilterResults();
            filterResults.values = phoneEntries;
            filterResults.count = phoneEntries.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            currentFilter = (constraint==null)?null:constraint.toString().toLowerCase();
            displayedElements = (List<PhoneEntry>) results.values;
            notifyDataSetChanged();
        }
    }

    private boolean accept(PhoneEntry phoneEntry, CharSequence constraint) {
        return phoneEntry.getName().toLowerCase().contains(constraint);
    }

    private boolean isEmptyFilter(CharSequence constraint) {
        return constraint == null || constraint.length() == 0;
    }
}
