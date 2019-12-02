//This is the class for adding contacts
package cn.projects.team.demo.ui;

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

public class AddContactsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final int PRC_PHOTO_PICKER = 1;

    private static final int RC_CHOOSE_PHOTO = 1;
    private static final int RC_PHOTO_PREVIEW = 2;
    private ImageView setAvatar;
    private ImageView icon;
    private EditText name;
    private EditText phone1;
    private EditText phone2;
    private EditText tel;
    private String iconUrl;
    private TextView toolbar_title;
    private long id;
    private Spinner spinner;
    private List<String>  group = new ArrayList<>();
    private Long postion = 0L;
    private List<ContactGroup> listGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
//define some variables
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setAvatar = findViewById(R.id.setAvatar);
        icon = findViewById(R.id.icon);
        toolbar_title = findViewById(R.id.toolbar_title);
        Button add = findViewById(R.id.add);
        name = findViewById(R.id.name);
        phone1 = findViewById(R.id.phone1);
        phone2 = findViewById(R.id.phone2);
        tel = findViewById(R.id.tel);
        spinner = findViewById(R.id.spinner);
        setSpinnerAdapter();


        // Click the photo button to take photo
        setAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choicePhotoWrapper();
            }
        });

        // click the add button to add a new contact
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString();
                String phone1Str = phone1.getText().toString();
                String phone2Str = phone2.getText().toString();
                String telStr = tel.getText().toString();
                if(0L == postion){
                    Toast.makeText(AddContactsActivity.this,"Please enter a group",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(TextUtils.isEmpty(nameStr)){
                    Toast.makeText(AddContactsActivity.this,"Please enter a name",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(TextUtils.isEmpty(phone1Str)){
                    Toast.makeText(AddContactsActivity.this,"Please enter a phone",Toast.LENGTH_SHORT).show();
                    return ;
                }


                Contact contact = new Contact();

                ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
                contact.setIcon(iconUrl);
                contact.setName(nameStr);
                contact.setPhone1(phone1Str);
                contact.setIsBlack(false);
                contact.setPhone2(phone2Str);
                contact.setPhone3(telStr);
                contact.setGroupId(postion.intValue());
                if(0!= id){
                    contact.setId(id);
                    contactDao.update(contact);// update contact
                }else{
                    contactDao.insert(contact);// insert contact
                }

                Toast.makeText(AddContactsActivity.this,"Success",Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        id = getIntent().getLongExtra("id", 0);
        if(0!= id){//call this when editing a contact
            toolbar_title.setText("Edit A Contact");
            ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
            Contact contact = contactDao.queryBuilder().where(ContactDao.Properties.Id.eq(id)).unique();
            Glide.with(AddContactsActivity.this)
                    .load(contact.getIcon())
                    .into(icon);
            if(null != contact.getGroupId()){
                spinner.setSelection(contact.getGroupId(),true);
            }

            name.setText(contact.getName());
            phone1.setText(contact.getPhone1());
            phone2.setText(contact.getPhone2());
            iconUrl = contact.getIcon();
            tel.setText(contact.getPhone3());
        }
    }


    //select the group for the new contact
    public void setSpinnerAdapter()
    {
        ContactGroupDao contactDao = App.getInstance().getDaoSession().getContactGroupDao();
        listGroup = contactDao.queryBuilder().list();
        group.add("Please select a group");
        for (ContactGroup contactGroup : listGroup) {
            group.add(contactGroup.getGroupName());
        }
        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,R.layout.simple_spinner_item, group);
        spinner.setAdapter(_Adapter);

        spinner.setSelection(0,true);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if(0==pos){
                    postion = 0L;
                }else{
                    postion = listGroup.get(pos-1).getId();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //choose the profile photo for the new contact
    @AfterPermissionGranted(PRC_PHOTO_PICKER)
    private void choicePhotoWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // the photo directory
            File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");

            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(takePhotoDir) // photo directory
                    .maxChooseCount(1) // the max photo number can be chosen
                    .selectedPhotos(null)
                    .pauseOnScroll(false) // pause loading photo when scrolling
                    .build();
            startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, "Choose photo permission:\n\n " +
                    "Have permission to visit the photo library \n\n Have permission to take photos",
                    PRC_PHOTO_PICKER, perms);
        }
    }
    // permission grant
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == PRC_PHOTO_PICKER) {
            Toast.makeText(this, "You have to agree choosing photo permission", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * define the activity after choosing the photo
     * @param requestCode that request to load photo if it's ok
     * @param resultCode the result that decide to choose photo or preview photo
     * @param data get selected photos
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            ArrayList<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            Glide.with(AddContactsActivity.this)
                    .load(selectedPhotos.get(0))
                    .into(icon);
             iconUrl = selectedPhotos.get(0);
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            ArrayList<String> selectedPhotos = BGAPhotoPickerActivity.getSelectedPhotos(data);
            Glide.with(AddContactsActivity.this)
                    .load(selectedPhotos.get(0))
                    .into(icon);
            iconUrl = selectedPhotos.get(0);

        }
    }
}
