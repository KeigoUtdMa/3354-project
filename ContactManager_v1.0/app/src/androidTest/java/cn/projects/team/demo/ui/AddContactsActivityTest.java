package cn.projects.team.demo.ui;


import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import cn.droidlover.xdroidmvp.demo.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.droidlover.xdroidmvp.demo.R;
import cn.projects.team.demo.App;
import cn.projects.team.demo.greendao.ContactDao;
import cn.projects.team.demo.greendao.ContactGroupDao;
import cn.projects.team.demo.model.Contact;
import cn.projects.team.demo.model.ContactGroup;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;




public class AddContactsActivityTest {

    @Rule
    public ActivityTestRule<AddContactsActivity> mActivityTestRule = new ActivityTestRule<AddContactsActivity>(AddContactsActivity.class);
    private AddContactsActivity mActivity = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null, false);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testInput() throws Exception {
        assertNotNull(mActivity.findViewById(R.id.add));

        onView(withId(R.id.name)).perform(setTextInTextView("test123"));
        onView(withId(R.id.phone1)).perform(setTextInTextView("6666666"));

        EditText name = (EditText) mActivity.findViewById(R.id.name);
        EditText phoneNum1 = (EditText) mActivity.findViewById(R.id.phone1);

        String e1 = name.getText().toString().trim();
        String e2 = phoneNum1.getText().toString().trim();

        assertEquals("test123",e1);
        assertEquals("6666666",e2);
    }



    private ViewAction setTextInTextView(String value) {
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }

    @After
    public void tearDown() throws Exception {

    }




}