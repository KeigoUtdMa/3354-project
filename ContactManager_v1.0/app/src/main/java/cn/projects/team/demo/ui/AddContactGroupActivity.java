//This is the class for the adding contact group

package cn.projects.team.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;

import cn.droidlover.xdroidmvp.demo.R;
import cn.projects.team.demo.App;
import cn.projects.team.demo.greendao.ContactDao;
import cn.projects.team.demo.greendao.ContactGroupDao;
import cn.projects.team.demo.model.Contact;
import cn.projects.team.demo.model.ContactGroup;

public class AddContactGroupActivity extends AppCompatActivity {
    private TextView toolbar_title;
    private EditText name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        name = findViewById(R.id.name);
        long id = getIntent().getLongExtra("id", 0);
        //A group will has a name and id
        SuperButton sb_add =  findViewById(R.id.sb_add);
        toolbar_title = findViewById(R.id.toolbar_title);
        sb_add.setOnClickListener(new View.OnClickListener() {//a listener will listen the click then ask user for input
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString();
                if(TextUtils.isEmpty(nameStr)){
                    Toast.makeText(AddContactGroupActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                    return ;
                }
                ContactGroup group = new ContactGroup();
                //this group is a new ContactGroup object
                group.setGroupName(nameStr);
                //nameStr becomes GroupName
                ContactGroupDao contactDao = App.getInstance().getDaoSession().getContactGroupDao();
                //Creat new group object
                if(0!=id){
                    group.setId(id);
                    contactDao.update(group);
                    //If group exist, the system will update the group information.
                }else{
                    //If group does not exist, the system will insert a new group in systemn
                    contactDao.insert(group);
                }
            finish();
            }
        });
        //editing group page
        if(0!=id){
            toolbar_title.setText("Edit Group");
            ContactGroupDao contactDao = App.getInstance().getDaoSession().getContactGroupDao();
            ContactGroup contact = contactDao.queryBuilder().where(ContactGroupDao.Properties.Id.eq(id)).unique();
            name.setText(contact.getGroupName());
        }

    }
}
