package com.bubbes.bubblesender.contacts;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
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

//    @UiThreadTest
//    public void testRemoveACharacter() throws Throwable {
//        final AutoCompleteTextView textView = (AutoCompleteTextView) contactActivity.findViewById(R.id.auto_complete_text_view);
//        //jdk 8 would be nice...
//        // contactActivity.setSelectedContact(new PhoneEntry("martin", "0553670000", "Mobile", "uri1", "uri2"));
//        assertTrue(textView.requestFocus());
//        Runnable runnable = new Runnable() {
//            @Override
//
//            public void run() {
//                getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MOVE_END);
//                getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
//            }
//        };
//        this.runSynchronouslyInDifferentThread(runnable);
//        assertFalse(this.contactActivity.isAContactSelected());
//        assertEquals("", textView.getText().toString());
//    }

    //==============================================================================================
    // Private
    //==============================================================================================

//    private void runSynchronouslyInDifferentThread(final Runnable runnable) throws InterruptedException {
//        final Object MUTEX = new Object();
//        final AtomicBoolean done = new AtomicBoolean(false);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runnable.run();
//                synchronized (MUTEX) {
//                    MUTEX.notifyAll();
//                    done.set(true);
//                }
//            }
//        }).start();
//        synchronized (MUTEX) {
//            if (!done.get()) {
//                MUTEX.wait(1000);
//                if (!done.get()) {
//                    throw new RuntimeException("aie");
//                }
//            }
//        }
//    }
}
