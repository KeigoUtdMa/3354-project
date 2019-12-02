//this is the class that allow user to create or delete a contact group
package cn.projects.team.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.droidlover.xdroidmvp.demo.R;
import cn.projects.team.demo.App;
import cn.projects.team.demo.adapter.GroupAdapter;
import cn.projects.team.demo.adapter.SortAdapter;
import cn.projects.team.demo.greendao.ContactDao;
import cn.projects.team.demo.greendao.ContactGroupDao;
import cn.projects.team.demo.model.Contact;
import cn.projects.team.demo.model.ContactGroup;
import cn.projects.team.demo.widget.RecyclerViewImplementsContextMenu;
import cn.projects.team.demo.widget.TitleItemDecoration;

public class ContactGroupActivity extends AppCompatActivity {
    private List<ContactGroup> mDateList= new ArrayList<>();
    private TitleItemDecoration mDecoration;
    private RecyclerViewImplementsContextMenu mRecyclerView;
    private LinearLayoutManager manager;
    private GroupAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mRecyclerView = findViewById(R.id.rv);

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        /**
         *long-pressing group name to edit and delete group
         */
        mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "Edit Group");
                menu.add(0, 2, 0, "Delete Group");


            }
        });

        mAdapter = new GroupAdapter(this, mDateList);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getData();
    }

    private void getData() {
        ContactGroupDao contactDao = App.getInstance().getDaoSession().getContactGroupDao();
        List<ContactGroup> list = contactDao.queryBuilder().list();
        mDateList.clear();
        mDateList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }



    Menu menu ;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        this.menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        int id = item.getItemId();
        //create new intent object
        Intent intent = new Intent(ContactGroupActivity.this,AddContactGroupActivity.class) ;
        //start intent object
        startActivity(intent);


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id gets the id of chosen item in the listview
        //get the chosen position
        String id = String.valueOf(info.position);
        //get the chosen contact
        ContactGroup contact = this.mDateList.get(Integer.parseInt(id));

        switch (item.getItemId()) {
                //if contatc exist
            case 1:
                // Edit an existing contact
                Intent intent = new Intent(this, AddContactGroupActivity.class);
                intent.putExtra("id", contact.getId());
                startActivity(intent);

                break;
            case 2:
                //delete a contact
                Toast.makeText(this, "delete successfully", Toast.LENGTH_SHORT).show();
                ContactGroupDao contactDao = App.getInstance().getDaoSession().getContactGroupDao();
                contactDao.deleteByKey(contact.getId());
                getData();
                break;
        }

        return super.onContextItemSelected(item);
    }

}
