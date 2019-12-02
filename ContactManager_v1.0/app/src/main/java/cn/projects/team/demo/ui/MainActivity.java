//this is the main class 
package cn.projects.team.demo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.xp.wavesidebar.WaveSideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.droidlover.xdroidmvp.demo.R;
import cn.projects.team.demo.App;
import cn.projects.team.demo.adapter.SortAdapter;
import cn.projects.team.demo.greendao.ContactDao;
import cn.projects.team.demo.greendao.ContactGroupDao;
import cn.projects.team.demo.model.Contact;
import cn.projects.team.demo.model.ContactGroup;
import cn.projects.team.demo.widget.ClearEditText;
import cn.projects.team.demo.widget.PinyinComparator;
import cn.projects.team.demo.widget.PinyinUtils;
import cn.projects.team.demo.widget.RecyclerViewImplementsContextMenu;
import cn.projects.team.demo.widget.TitleItemDecoration;

public class MainActivity extends AppCompatActivity {
    private RecyclerViewImplementsContextMenu mRecyclerView;
    private WaveSideBar mSideBar;
    private SortAdapter mAdapter;
    private ClearEditText mClearEditText;
    private LinearLayoutManager manager;

    private List<Contact> mDateList;
    private TitleItemDecoration mDecoration;
    View view;

    private PinyinComparator mComparator;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //fab button click
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, AddContactsActivity.class);
                startActivity(in);
            }
        });
        initViews();
    }


    private void initViews() {
        mComparator = new PinyinComparator();

        mSideBar = (WaveSideBar) findViewById(R.id.sideBar);

        ////set the sidebar on the right
        mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                // //get the position of the letter
                int position = mAdapter.getPositionForSection(letter.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }
        });

        mRecyclerView = (RecyclerViewImplementsContextMenu) findViewById(R.id.rv);
        ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
        //query all of the contacts
        List<Contact> list = contactDao.queryBuilder().where(ContactDao.Properties.IsBlack.eq(false)).list();
        List<Contact> contacts = filledData(list);
        mDateList = contacts;

        // sort contacts in alphabetic order according to a-z
        Collections.sort(mDateList, mComparator);

        //set RecyclerView manager
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);


        // click long-press to get menu
        mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "Edit Contact");
                menu.add(0, 2, 0, "Delete Contact");
                menu.add(0, 3, 0, "Call");
                menu.add(0, 4, 0, "Send SMS");
                menu.add(0, 5, 0, "Add to Blacklist");
                menu.add(0, 7, 0, "Move to Group");
            }
        });

        mAdapter = new SortAdapter(this, mDateList);
        mRecyclerView.setAdapter(mAdapter);
        mDecoration = new TitleItemDecoration(this, mDateList);
        mRecyclerView.addItemDecoration(mDecoration);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        //search based on the input
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // when input is empty, update to contact list, otherwise show filter data
                filterData(s.toString(),type);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id gets the id of chosen item in the listview
        String id = String.valueOf(info.position);////get the chosen position
        //get the chosen contact object
        contact = this.mDateList.get(Integer.parseInt(id));
        //  Log.e("contact",contact.toString());

        switch (item.getItemId()) {//edit contact
            case 1:
               // Toast.makeText(this, "edit",Toast.LENGTH_SHORT).show();
                //jump to edit contact
                Intent intent = new Intent(this,AddContactsActivity.class);
                intent.putExtra("id" , contact.getId());

                startActivity(intent);

                break;
            case 2://delete contact
                Toast.makeText(this, "delete successfully", Toast.LENGTH_SHORT).show();

                ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
                contactDao.deleteByKey(contact.getId());
                filterData("",type);
                break;
            case 3://show phones

                showPhones(contact);

                break;
            case 4://show sms
                showSms(contact);

                break;

            case 5://add to blacklist
                ContactDao contactDao1 = App.getInstance().getDaoSession().getContactDao();
                contact.setIsBlack(true);
                contactDao1.update(contact);
                filterData("",type);
                break;
            case 6://remove contacts from blacklist
                ContactDao contactDao2 = App.getInstance().getDaoSession().getContactDao();
                contact.setIsBlack(false);
                contactDao2.update(contact);
                filterData("",type);
                break;
            case 7://move contact to a specific group
                ContactGroupDao contactGroupDao = App.getInstance().getDaoSession().getContactGroupDao();
                List<ContactGroup> list = contactGroupDao.queryBuilder().list();
                List<String> list1 =  new ArrayList<>();
                for ( ContactGroup group :list) {
                      list1.add(group.getGroupName());
                }
                new XPopup.Builder(MainActivity.this)
                        .hasShadowBg(false)
//                        .popupAnimation(PopupAnimation.NoAnimation) //NoAnimation
                       .isCenterHorizontal(true)
                        //  .offsetY(-130)
                        .popupPosition(PopupPosition.Bottom) //set the pop out position
                        .atView(info.targetView)  // decide show menu position based on the user activity
                        .asAttachList(list1.toArray(new String[list1.size()]),
                                new int[]{},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        ContactDao contactDao2 = App.getInstance().getDaoSession().getContactDao();
                                        Contact contact1 = contactDao2.queryBuilder().where(ContactDao.Properties.Id.eq(contact.getId())).unique();
                                        contact1.setGroupId(list.get(position).getId().intValue());
                                        contactDao2.update(contact1);
                                        Toast.makeText(MainActivity.this, "Success",Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .show();

                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    //send sms
    private void showSms(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Select a number");
        //    show the phone number items
        final String[] phones = {contact.getPhone1(), null==contact.getPhone2() ?"":contact.getPhone2(), null==contact.getPhone3() ?"":contact.getPhone3()};
        //    set a drop-down list
        builder.setItems(phones, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Uri smsToUri = Uri.parse("smsto:"+phones[which]);

                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

                intent.putExtra("sms_body", "Hello");

                startActivity(intent);

            }
        });
        builder.show();
    }


    /**
     * Get data for the RecyclerView
     * @param date contact list data
     * @return the sorted contact list
     */
    private List<Contact> filledData( List<Contact> date) {
        List<Contact> mSortList = new ArrayList<>();

        for (int i = 0; i < date.size(); i++) {
            Contact sortModel = new Contact();
            sortModel.setName(date.get(i).getName());
            sortModel.setPhone3(date.get(i).getPhone3());
            sortModel.setPhone1(date.get(i).getPhone1());
            sortModel.setPhone2(date.get(i).getPhone2());
            sortModel.setIcon(date.get(i).getIcon());
            sortModel.setId(date.get(i).getId());
            String pinyin = PinyinUtils.getPingYin(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // determine the first character is English or not
            if (sortString.matches("[A-Z]")) {
                sortModel.setLetters(sortString.toUpperCase());
            } else {
                sortModel.setLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * filter data based on the input and update the recycleview
     * @param filterStr the filter data
     */
    private void filterData(String filterStr,Boolean t) {
        List<Contact> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
            List<Contact> list = contactDao.queryBuilder().where(ContactDao.Properties.IsBlack.eq(t)).list();
            filterDateList = filledData(list);
        } else {
            filterDateList.clear();
            for (Contact sortModel : mDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                        ) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // sort a-z
        Collections.sort(filterDateList, mComparator);
        mDateList.clear();
        mDateList.addAll(filterDateList);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        filterData("",type);
    }


    //make a phone call
    private void showPhones(Contact contact)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Select a number");
        //    show the phone number items
        final String[] phones = {contact.getPhone1(), null==contact.getPhone2() ?"":contact.getPhone2(), null==contact.getPhone3() ?"":contact.getPhone3()};
        //    set a drop-down list
        builder.setItems(phones, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phones[which]));
                startActivity(dialIntent);
            }
        });
        builder.show();
    }

    Menu menu ;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    Boolean type = false ;
    //set long-press menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_droid:
                if(type){
                    type = false;
                    filterData("",type);
                    menu.getItem(0).setTitle("Blacklist");
                    mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            menu.add(0, 1, 0, "Edit Contact");
                            menu.add(0, 2, 0, "Delete Contact");
                            menu.add(0, 3, 0, "Call");
                            menu.add(0, 4, 0, "Send SMS");
                            menu.add(0, 5, 0, "Add to blacklist");
                            menu.add(0, 7, 0, "Move to Group");

                        }
                    });
                }else{
                    type = true;
                    menu.getItem(0).setTitle("Whitelist");
                    filterData("",type);
                    mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                            menu.add(0, 1, 0, "Edit Contact");
                            menu.add(0, 2, 0, "Delete Contact");
                            menu.add(0, 6, 0, "Move out blacklist");
                            menu.add(0, 7, 0, "Move to Group");

                        }
                    });
                }
                break;

//            case R.id.action_Import:
//               Intent intent = new Intent(MainActivity.this,ImportExportActivity.class) ;
//               startActivity(intent);
//
//                break;

            case R.id.action_group:
                Intent intent1 = new Intent(MainActivity.this, ContactGroupActivity.class) ;
                startActivity(intent1);

                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
