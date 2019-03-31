package com.suzei.minote.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.suzei.minote.Injection;
import com.suzei.minote.R;
import com.suzei.minote.data.DataSourceImpl;
import com.suzei.minote.data.entity.Notes;
import com.suzei.minote.ui.editor.note.EditorNoteActivity;
import com.suzei.minote.utils.ScreenOrientationUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Checks;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class EditorScreenTest {

    private static String NOTE_TITLE = "Important note";

    private static String NOTE_MESSAGE = "This is just a message";

    private static String NOTE_TEXT_COLOR = "#FFFFFF";

    private static String NOTE_COLOR = "#002071";

    private static Notes NOTE = new Notes(
            1,
            NOTE_TITLE,
            null,
            NOTE_MESSAGE,
            NOTE_TEXT_COLOR,
            NOTE_COLOR);

    private DataSourceImpl dataSourceImpl;

    /**
     *  Lazily start the activity so we can control the Intent to start this activity(EditorNoteActivity)
     *  Since we have two ways to use the EditorNoteActivity
     *  1st is to create a new note
     *  2nd is to edit a note therefore we need to pass the note id
     */
    @Rule
    public ActivityTestRule<EditorNoteActivity> editorActivityTestRule = new ActivityTestRule<>(
            EditorNoteActivity.class,
            true,
            false);

    @Before
    public void setUpEditor() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dataSourceImpl = Injection.INSTANCE.provideDataSourceImpl(context);
    }

    public static Matcher<View> withTextColor(String textColor) {
        Checks.checkNotNull(textColor);
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }

            @Override
            protected boolean matchesSafely(EditText item) {
                int color = item.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                return textColor.equals(hexColor);
            }
        };
    }

    public static Matcher<View> withBackgroundColor(String backgroundColor) {
        Checks.checkNotNull(backgroundColor);
        return new BoundedMatcher<View, LinearLayout>(LinearLayout.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: ");
            }

            @Override
            protected boolean matchesSafely(LinearLayout item) {
                ColorDrawable colorDrawable = (ColorDrawable) item.getBackground();
                String hexColor = String.format("#%06X", (0xFFFFFF & colorDrawable.getColor()));
                return hexColor.equals(backgroundColor);
            }
        };
    }

    private void startActivityWithId() {
        //  Click the fab button
        dataSourceImpl.saveNote(NOTE);

        Intent intent = new Intent();
        intent.putExtra(EditorNoteActivity.Companion.getEXTRA_NOTE_ID(), NOTE.getId());
        editorActivityTestRule.launchActivity(intent);
    }

    private void startActivityWithoutId() {
        editorActivityTestRule.launchActivity(new Intent());
    }

    @Test
    public void noteDetailsDisplayedUi() {
        startActivityWithId();

        //  Check if note details is displayed
        onView(withId(R.id.editor_title)).check(matches(withText(NOTE_TITLE)));
        onView(withId(R.id.editor_text)).check(matches(withText(NOTE_MESSAGE)));

        //  Check if note color is displayed
        onView(withId(R.id.editor_text)).check(matches(withTextColor(NOTE_TEXT_COLOR)));
        onView(withId(R.id.editor_title)).check(matches(withTextColor(NOTE_TEXT_COLOR)));
    }

    @Test
    public void newNoteDisplayedUi() {
        startActivityWithoutId();

        //  Check if the editor activity is displayed
        onView(withId(R.id.editor_text_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void orientationChangeNotePersist() {
        startActivityWithId();

        //  Rotate the device
        ScreenOrientationUtils.rotateScreen(editorActivityTestRule.getActivity());

        //  Check if the details persist
        onView(withId(R.id.editor_title)).check(matches(withText(NOTE_TITLE)));
        onView(withId(R.id.editor_text)).check(matches(withText(NOTE_MESSAGE)));
    }

}
