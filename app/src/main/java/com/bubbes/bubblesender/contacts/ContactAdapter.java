package com.bubbes.bubblesender.contacts;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bubbes.bubblesender.R;
import com.bubbes.bubblesender.utils.Assertion;
import com.bubbes.bubblesender.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter implements Filterable {

    //==============================================================================================
    // Constructor
    //==============================================================================================
    /**
     * Activity which has called me, used for inflating the views
     */
    private Activity activity;
    /**
     * The elements which are currently displayed
     */
    private List<PhoneEntry> displayedElements;
    /**
     * The layout id used for the items of the view
     */
    private int rowLayout;
    /**
     * The complete list of the phone entries
     */
    private ArrayList<PhoneEntry> completePhoneList;
    /**
     * The filter which may be used on this adapter
     */
    private Filter contactFilter;

    //==============================================================================================
    // Constructor
    //==============================================================================================

    /**
     * Unique constructor...
     *
     * @param activity     The activity which has created me :)
     * @param rowLayoutId     The layout id to use for creating item view
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
    public View getView(final int position, View view, ViewGroup parent) {
        Assertion.assertIsMainThread();
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

        String uriString = phoneEntry.get(PhoneEntry.CONTACT_IMAGE_URI);
        if (uriString != null) {
            image.setImageURI(Uri.parse(uriString));
        } else {
            image.setImageResource(R.drawable.ic_contact_picture);
        }
        type.setText(phoneEntry.get(PhoneEntry.CONTACT_PHONE_TYPE));
        phoneNumber.setText(phoneEntry.get(PhoneEntry.CONTACT_PHONE));
        contactName.setText(phoneEntry.get(PhoneEntry.CONTACT_NAME));
        return view;
    }

    //==============================================================================================
    // Filterable implementations
    //==============================================================================================
    public Filter getFilter() {
        Assertion.assertIsMainThread();
        if (this.contactFilter == null) {
            this.contactFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (constraint == null || constraint.length() == 0) {
                        FilterResults filterResults = new FilterResults();
                        filterResults.count = completePhoneList.size();
                        filterResults.values = new ArrayList<>(completePhoneList);
                        return filterResults;
                    } else {
                        String toLowerCase = constraint.toString().toLowerCase();
                        ArrayList<PhoneEntry> phoneEntries = new ArrayList<>();
                        for (PhoneEntry allPhoneEntries : completePhoneList) {
                            if (allPhoneEntries.get(PhoneEntry.CONTACT_NAME).toLowerCase().contains(toLowerCase)) {
                                phoneEntries.add(allPhoneEntries);
                            }
                        }
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = phoneEntries;
                        filterResults.count = phoneEntries.size();
                        return filterResults;
                    }
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count == 0)
                        notifyDataSetInvalidated();
                    else {
                        displayedElements = (List<PhoneEntry>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }
        return this.contactFilter;
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Add the given list to the current phone list
     * @param entries The phone entries to add to the adapter
     */
    public void addAll(List<PhoneEntry> entries) {
        Assertion.assertIsMainThread();
        this.completePhoneList.addAll(entries);
        this.notifyDataSetChanged();
    }


}
