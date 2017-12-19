package br.ufpe.cin.if710.podcast;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.ui.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.not;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EspressoInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule(MainActivity.class);


    @Test
    public void testTextDetailInfo() {
        onData(anything()).
                inAdapterView(withId(R.id.items))
                .atPosition(0)
                .onChildView(withId(R.id.item_title))
                .perform(click());

        onView(withId(R.id.viewTitle)).check(matches(withText("Ciência e Pseudociência")));
        //onData(allOf(is(instanceOf(String.class)), is("Ciência e Pseudociência"))).perform(click());
      //  onView(withId(R.id.viewTitle)).check(matches(isDisplayed()));
        //onView(withText("Hello Steve!")).check(matches(isDisplayed()));
    }

    @Test
    public void testUrlPodcast()
    {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());
        onView(allOf(   withText("Link do Feed"),
                hasSibling(withText("http://leopoldomt.com/if710/fronteirasdaciencia.xml"))));
    }

    @Test
    public void testChangeUrlPodcast()
    {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());
        onView(allOf(withText("Link do Feed"),
                hasSibling(withText("http://leopoldomt.com/if710/fronteirasdaciencia.xml"))))
        .perform(click());
        onView(withText("http://leopoldomt.com/if710/fronteirasdaciencia.xml")).
                perform(replaceText("novo"));
        onView(withText("Modificar")).perform(click());
        onView(allOf(   withText("Link do Feed"),
                not(hasSibling(withText("http://leopoldomt.com/if710/fronteirasdaciencia.xml")))));
    }
}

