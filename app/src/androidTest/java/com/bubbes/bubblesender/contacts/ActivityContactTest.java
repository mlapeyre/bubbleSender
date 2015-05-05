package com.bubbes.bubblesender.contacts;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.bubbes.bubblesender.PhoneEntry;
import com.bubbes.bubblesender.R;


public class ActivityContactTest extends ActivityInstrumentationTestCase2<ContactActivity> {

    private ContactActivity contactActivity;

    public ActivityContactTest() {
        super(ContactActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.setActivityInitialTouchMode(true);
        this.contactActivity = this.getActivity();
    }

    @UiThreadTest
    public void testBubbleButtonIsDisabledAtStartup(){
        View viewById = this.getActivity().findViewById(R.id.bt_bubble_them);
        assertFalse(viewById.isEnabled());
    }

    @UiThreadTest
    public void testBubbleButtonIsEnabledAfterSelectingAnItem(){
        this.contactActivity.setSelectedContact(new PhoneEntry("martin", "0553670000", "Mobile", "uri1","uri2"));
        View viewById = this.contactActivity.findViewById(R.id.bt_bubble_them);
        assertTrue(viewById.isEnabled());
    }

    @UiThreadTest
    public void testTextFieldIsCorrectlyFiledAfterSelectingAnItem(){
        this.contactActivity.setSelectedContact(new PhoneEntry("martin", "0553670000", "Mobile", "uri1","uri2"));
        AutoCompleteTextView textView = (AutoCompleteTextView) this.contactActivity.findViewById(R.id.auto_complete_text_view);
        assertEquals("martin <0553670000>", textView.getText().toString());
    }

    @UiThreadTest
    public void testisAContactSelectedIsTrueAfterSelectedAnItem(){
        this.contactActivity.setSelectedContact(new PhoneEntry("martin", "0553670000", "Mobile", "uri1","uri2"));
        assertTrue(this.getActivity().isAContactSelected());
    }


    public void testRemoveACharacter() throws Throwable {
        final AutoCompleteTextView textView = (AutoCompleteTextView) contactActivity.findViewById(R.id.auto_complete_text_view);
        //jdk 8 would be nice...
        this.runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactActivity.setSelectedContact(new PhoneEntry("martin", "0553670000", "Mobile","uri1","uri2" ));
            }
        });
        assertTrue(textView.requestFocus());
        this.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MOVE_END);
        this.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
        assertFalse(this.contactActivity.isAContactSelected());
        assertEquals("", textView.getText().toString());
    }
}
