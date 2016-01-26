import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import michal.myapplication.ParkCarScreen;
import michal.myapplication.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class ParkCarScreenUITest {


    @Rule
    public ActivityTestRule<ParkCarScreen> mActivityRule =
            new ActivityTestRule<>(ParkCarScreen.class);

    @Test
    public void checkAddingNotes() {
        // Type text and then press the button.
        onView(withId(R.id.notesEdit))
                .perform(typeText("HELLO"), closeSoftKeyboard());
        // Check that the text was changed.
        onView(withId(R.id.notesEdit)).check(matches(withText("HELLO")));


    }


} 
