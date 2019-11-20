package com.example.contact;

import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public enum FragmentEditMode { EDIT, ADD }
    private FragmentEditMode mMode;

    private EditText mFirstNameTextView;
    private EditText mLastNameTextView;
    private EditText mPhoneNumberTextView;
    private Button mSaveButton;

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mFirstNameTextView = (EditText) view.findViewById(R.id.addedit_firstname);
        mLastNameTextView = (EditText) view.findViewById(R.id.addedit_lastname);
        mPhoneNumberTextView = (EditText) view.findViewById(R.id.addedit_phonenumber);
        mSaveButton = (Button) view.findViewById(R.id.addedit_save);

        Bundle arguments = getActivity().getIntent().getExtras();  // The line might be changed later

        final Contact contact;
        if(arguments != null) {
            Log.d(TAG, "onCreateView: retrieving contact details.");

            contact = (Contact) arguments.getSerializable(Contact.class.getSimpleName());
            if(contact != null) {
                Log.d(TAG, "onCreateView: contact details found, editing...");
                mFirstNameTextView.setText(contact.getmFirstName());
                mLastNameTextView.setText(contact.getmLastName());
                mPhoneNumberTextView.setText(Integer.toString(contact.getmPhoheNumber()));
                mMode = FragmentEditMode.EDIT;
            } else {
                // No contact, so we must be adding a new contact, and not editing an  existing one
                mMode = FragmentEditMode.ADD;
            }
        } else {
            contact = null;
            Log.d(TAG, "onCreateView: No arguments, adding new record");
            mMode = FragmentEditMode.ADD;
        }
/**
 * define save button to save the edit or add items into the database
 */
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the database if at least one field has changed.
                // - There's no need to hit the database unless this has happened.
                int so;     // to save repeated conversions to int.
                if(mPhoneNumberTextView.length()>0) {
                    so = Integer.parseInt(mPhoneNumberTextView.getText().toString());
                } else {
                    so = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode) {
                    case EDIT:
                        if(!mFirstNameTextView.getText().toString().equals(contact.getmFirstName())) {
                            values.put(ContactsContract.Columns.CONTACTS_FIRSTNAME, mFirstNameTextView.getText().toString());
                        }
                        if(!mLastNameTextView.getText().toString().equals(contact.getmLastName())) {
                            values.put(ContactsContract.Columns.CONTACTS_LASTNAME, mLastNameTextView.getText().toString());
                        }
                        if(so != contact.getmPhoheNumber()) {
                            values.put(ContactsContract.Columns.CONTACTS_PHONENUMBER, so);
                        }
                        if(values.size() != 0) {
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(ContactsContract.buildContactsUri(contact.getM_ID()), values, null, null);
                        }
                        break;
                    case ADD:
                        if(mFirstNameTextView.length()>0) {
                            Log.d(TAG, "onClick: adding new task");
                            values.put(ContactsContract.Columns.CONTACTS_FIRSTNAME, mFirstNameTextView.getText().toString());
                            values.put(ContactsContract.Columns.CONTACTS_LASTNAME, mLastNameTextView.getText().toString());
                            values.put(ContactsContract.Columns.CONTACTS_PHONENUMBER, so);
                            contentResolver.insert(ContactsContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: Done editing");
            }
        });
        Log.d(TAG, "onCreateView: Exiting...");


        return view;
    }
}