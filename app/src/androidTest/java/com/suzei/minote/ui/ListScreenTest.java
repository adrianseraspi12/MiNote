package com.suzei.minote.ui;

import com.suzei.minote.R;
import com.suzei.minote.ui.list.ListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListScreenTest {

    private static final String TITLE1 = "TITLE1";

    private static final String TITLE2 = "TITLE2";

    private static final String MESSAGE1 = "MESSAGE1";

    private static final String MESSAGE2 = "MESSAGE2";

    @Rule
    public ActivityTestRule<ListActivity> mListActivityTestRule =
            new ActivityTestRule<>(ListActivity.class);

    @Test
    public void clickAddNoteButton() {
        //  Click on the fab add note
        onView(withId(com.suzei.minote.R.id.list_add_note)).perform(click());

        //  Check if the editor screen show
        onView(withId(com.suzei.minote.R.id.editor_text_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void clickSettings() {
        //  Click settings icon
        onView(withId(R.id.menu_settings)).perform(click());

        //  Check if settings activity displayed
        onView(withText("Settings")).check(matches(isDisplayed()));
    }

    @Test
    public void addNewNoteNoPassword() {
        //  Add note
        createNoteNoPassword(TITLE1, MESSAGE1);

        //  Check if note is displayed
        onView(withText(TITLE1)).check(matches(isDisplayed()));
    }

    @Test
    public void addNewNoteWithPassword() {
        //  Add note
        createNoteWithPassword(TITLE1, MESSAGE1);

        //  Check if note is displayed
        onView(withText(TITLE1)).check(matches(isDisplayed())); // Check the title
        onView(withId(R.id.item_notes_password)).check(matches(isDisplayed())); //  Check the icon
    }

    @Test
    public void deleteNote() {
        //  Add note
        createNoteNoPassword(TITLE1, MESSAGE1);

        //  Click delete icon
        onView(withId(R.id.item_notes_delete)).perform(click());

        //  Check if the note is not displayed
        onView(withText(TITLE1)).check(doesNotExist());
    }

    @Test
    public void undoDeletion() {
        //  Add note
        createNoteNoPassword(TITLE1, MESSAGE1);

        //  Click delete icon
        onView(withId(R.id.item_notes_delete)).perform(click());

        // Click undo button
        onView(withId(com.google.android.material.R.id.snackbar_action)).perform(click());

        //  Check if it restore the note
        onView(withText(TITLE1)).check(matches(isDisplayed()));
    }

    @Test
    public void editNoteNoPassword() {
        //  Add note to list
        createNoteNoPassword(TITLE1, MESSAGE1);

        //  Click on the list
        onView(withText(TITLE1)).perform(click());

        String editNoteTitle = TITLE2;
        String editNoteMessage = MESSAGE2;

        //  Edit note title and message
        onView(withId(R.id.editor_title)).perform(replaceText(editNoteTitle),
                closeSoftKeyboard()); //  replace the title

        onView(withId(R.id.editor_text)).perform(replaceText(editNoteMessage),
                closeSoftKeyboard()); //  replace the message

        saveNoteAndGoBack();

        //  Check if note is displayed on list
        onView(allOf(withId(R.id.item_notes_title), withText(editNoteTitle)))
                .check(matches(isDisplayed()));

        //  Check the previous note title if displayed
        onView(allOf(withId(R.id.item_notes_title), withText(TITLE1)))
                .check(doesNotExist());
    }

    @Test
    public void editNoteWithPassword() {
        createNoteWithPassword(TITLE1, MESSAGE1);

        //  Click note and show password dialog
        onView(withText(TITLE1)).perform(click());

        //  Unlock the note (1234)
        onView(withId(R.id.buttonOne)).perform(click()); // Click 1st digit (1)
        onView(withId(R.id.buttonTwo)).perform(click()); // Click 2nd digit (2)
        onView(withId(R.id.buttonThree)).perform(click()); // Click 3rd digit (3)
        onView(withId(R.id.buttonFour)).perform(click()); // Click 4th digit (4)

        String editNoteTitle = TITLE2;
        String editNoteMessage = MESSAGE2;
        //  Edit Password 4321

        //  Change note title and message
        onView(withId(R.id.editor_title)).perform(replaceText(editNoteTitle),
                closeSoftKeyboard()); // type note title

        onView(withId(R.id.editor_text)).perform(replaceText(editNoteMessage),
                closeSoftKeyboard()); // type note message

        //  Click the menu and choose edit password
        onView(withId(R.id.editor_menu)).perform(click());
        onView(withId(R.id.bsd_edit_password)).perform(click());

        //  Enter new password (4321)
        onView(withId(R.id.buttonFour)).perform(click()); // Click 1st digit (4)
        onView(withId(R.id.buttonThree)).perform(click()); // Click 2nd digit (3)
        onView(withId(R.id.buttonTwo)).perform(click()); // Click 3rd digit (2)
        onView(withId(R.id.buttonOne)).perform(click()); // Click 4th digit (1)

        saveNoteAndGoBack();

        //  Check if note is displayed
        onView(allOf(withId(R.id.item_notes_title), withText(editNoteTitle)))
                .check(matches(isDisplayed()));

        //  Check if the previous title is displayed
        onView(allOf(withId(R.id.item_notes_title), withText(TITLE1))).check(doesNotExist());

        //  Check if the password is change, try to go to editor activity by entering the password
        onView(withText(TITLE2)).perform(click()); // Click the note and show a dialog password

        //  Enter the password (4321)
        onView(withId(R.id.buttonFour)).perform(click()); // Click 1st digit (4)
        onView(withId(R.id.buttonThree)).perform(click()); // Click 2nd digit (3)
        onView(withId(R.id.buttonTwo)).perform(click()); // Click 3rd digit (2)
        onView(withId(R.id.buttonOne)).perform(click()); // Click 4th digit (1)

        //  Check if editor activity is displayed
        onView(withId(R.id.editor_text_layout)).check(matches(isDisplayed()));
    }

    private void createNoteNoPassword(String title, String message) {
        //  click fab button to redirect to editor activity
        onView(withId(R.id.list_add_note)).perform(click());

        //  add note details
        onView(withId(R.id.editor_title)).perform(typeText(title),
                closeSoftKeyboard()); // type new title

        onView(withId(R.id.editor_text)).perform(typeText(message),
                closeSoftKeyboard()); // type new message

        saveNoteAndGoBack();
    }

    private void createNoteWithPassword(String title, String message) {
        //  Click fab button to redirect to editor activity
        onView(withId(R.id.list_add_note)).perform(click());

        //  Add note details
        onView(withId(R.id.editor_title)).perform(typeText(title),
                closeSoftKeyboard()); // type new title

        onView(withId(R.id.editor_text)).perform(typeText(message),
                closeSoftKeyboard()); // type new message

        //  Click menu and show bottomsheet dialog
        onView(withId(R.id.editor_menu)).perform(click());

        //  Click Edit Password and show a fullscreen dialog
        onView(withId(R.id.bsd_edit_password)).perform(click());

        //  Add 4 digit password (1234)
        onView(withId(R.id.buttonOne)).perform(click()); // 1st digit (1)
        onView(withId(R.id.buttonTwo)).perform(click()); // 2nd digit (2)
        onView(withId(R.id.buttonThree)).perform(click()); // 3rd digit (3)
        onView(withId(R.id.buttonFour)).perform(click()); // 4th digit (4)

        saveNoteAndGoBack();
    }

    private void saveNoteAndGoBack() {
        //  Save note
        onView(withId(R.id.editor_save)).perform(click());

        //  Click back arrow
        onView(withId(R.id.editor_back_arrow)).perform(click());
    }

}
