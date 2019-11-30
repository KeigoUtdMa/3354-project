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


        // 添加长按点击弹出选择菜单
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = new Intent(ContactGroupActivity.this,AddContactGroupActivity.class) ;
        startActivity(intent);


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        String id = String.valueOf(info.position);//得到选中的索引
        ContactGroup contact = this.mDateList.get(Integer.parseInt(id));//得到选中的Contacts对象
        //  Log.e("contact",contact.toString());

        switch (item.getItemId()) {
            case 1:
                // Toast.makeText(this, "修改",Toast.LENGTH_SHORT).show();
                //携带数据跳转到修改联系人界面
                Intent intent = new Intent(this, AddContactGroupActivity.class);
                intent.putExtra("id", contact.getId());
                startActivity(intent);

                break;
            case 2://删除联系人
                Toast.makeText(this, "delete success", Toast.LENGTH_SHORT).show();
                ContactGroupDao contactDao = App.getInstance().getDaoSession().getContactGroupDao();
                contactDao.deleteByKey(contact.getId());
                getData();
                break;
        }

        return super.onContextItemSelected(item);
    }

}
