//this is the class that allow user to add or remove contacts in the group
package cn.projects.team.demo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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

public class GroupContactActivity extends AppCompatActivity  {
        private RecyclerViewImplementsContextMenu mRecyclerView;
        private WaveSideBar mSideBar;
        private SortAdapter mAdapter;
        private ClearEditText mClearEditText;
        private LinearLayoutManager manager;
        private List<Contact> mDateList;
        private TitleItemDecoration mDecoration;
        private PinyinComparator mComparator;
        private long groupId;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            setContentView(R.layout.activity_main);

            groupId = getIntent().getLongExtra("groupId", 0);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(GroupContactActivity.this, AddContactsActivity.class);
                    startActivity(in);
                }
            });
            initViews();
        }

        //initialize
        private void initViews() {
            mComparator = new PinyinComparator();

            mSideBar = (WaveSideBar) findViewById(R.id.sideBar);

            //set the sidebar on the right
            mSideBar.setOnTouchLetterChangeListener(new WaveSideBar.OnTouchLetterChangeListener() {
                @Override
                public void onLetterChange(String letter) {
                    //get the position of the letter
                    int position = mAdapter.getPositionForSection(letter.charAt(0));
                    if (position != -1) {
                        manager.scrollToPositionWithOffset(position, 0);
                    }
                }
            });

            mRecyclerView = (RecyclerViewImplementsContextMenu) findViewById(R.id.rv);
            ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
            List<Contact> list = contactDao.queryBuilder().where(ContactDao.Properties.GroupId.eq(groupId)).list();
            List<Contact> contacts = filledData(list);
            mDateList = contacts;

            // sort the contact according to the aplhabetic order a-z
            Collections.sort(mDateList, mComparator);

            //set manager in RecyclerView
            manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);


            //click long-press to get menu
            mRecyclerView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(0, 1, 0, "Edit Contact");
                    menu.add(0, 2, 0, "Delete Contact");
                    menu.add(0, 3, 0, "Call");
                    menu.add(0, 4, 0, "Send SMS");
                    menu.add(0, 5, 0, "Add to blacklist");
                    menu.add(0, 7, 0, "Move out Group");

                }
            });

            mAdapter = new SortAdapter(this, mDateList);
            mRecyclerView.setAdapter(mAdapter);
            mDecoration = new TitleItemDecoration(this, mDateList);

            mRecyclerView.addItemDecoration(mDecoration);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(GroupContactActivity.this, DividerItemDecoration.VERTICAL));


            mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

            //searching based on the input
            mClearEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //when input is empty, update to contact list, otherwise show filter data
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
            // TODO Auto-generated method stub
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            ////info.id gets the id of chosen item in the listview
            String id = String.valueOf(info.position);//get the chosen position
            Contact contact = this.mDateList.get(Integer.parseInt(id));//get the chosen contact
            //  Log.e("contact",contact.toString());

            switch (item.getItemId()) {
                case 1:
                    // Toast.makeText(this, "edit",Toast.LENGTH_SHORT).show();
                    //get to the edit contact
                    Intent intent = new Intent(this,AddContactsActivity.class);
                    intent.putExtra("id" , contact.getId());

                    startActivity(intent);

                    break;
                case 2:
                    Toast.makeText(this, "delete success", Toast.LENGTH_SHORT).show();

                    ContactDao contactDao = App.getInstance().getDaoSession().getContactDao();
                    contactDao.deleteByKey(contact.getId());
                    filterData("",type);
                    break;
                case 3:

                    showPhones(contact);

                    break;
                case 4:
                    showSms(contact);

                    break;

                case 5:
                    ContactDao contactDao1 = App.getInstance().getDaoSession().getContactDao();
                    contact.setIsBlack(true);
                    contactDao1.update(contact);
                    filterData("",type);
                    break;
                case 6:
                    ContactDao contactDao2 = App.getInstance().getDaoSession().getContactDao();
                    contact.setIsBlack(false);
                    contactDao2.update(contact);
                    filterData("",type);
                    break;
                case 7://move in the contact group
                    ContactDao contactDao3 = App.getInstance().getDaoSession().getContactDao();
                    Contact contact1 = contactDao3.queryBuilder().where(ContactDao.Properties.Id.eq(contact.getId())).unique();
                    contact1.setGroupId(null);
                    contactDao3.update(contact1);
                    Toast.makeText(GroupContactActivity.this, "Success",Toast.LENGTH_SHORT).show();
                    filterData("",type);
                    break;
                default:
                    break;
            }
            return super.onContextItemSelected(item);
        }

        private void showSms(Contact contact) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle("Select a number");
            //    show the phone number items
            final String[] phones = {contact.getPhone1(), null==contact.getPhone2() ?"":contact.getPhone2(), null==contact.getPhone3() ?"":contact.getPhone3()};
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
                //change chinese to pinyin to set the order
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
                List<Contact> list = contactDao.queryBuilder().where(ContactDao.Properties.GroupId.eq(groupId)).list();
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

            Collections.sort(filterDateList, mComparator);
            mDateList.clear();
            mDateList.addAll(filterDateList);
            mAdapter.notifyDataSetChanged();
        }

        Boolean type = false ;
        @Override
        protected void onResume() {
            super.onResume();
            filterData("",type);
        }

        //mamke a phone call
        private void showPhones(Contact contact)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);
            builder.setTitle("Select a number");
            //    show the phone number items
            final String[] phones = {contact.getPhone1(), null==contact.getPhone2() ?"":contact.getPhone2(), null==contact.getPhone3() ?"":contact.getPhone3()};
            builder.setItems(phones, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //jump to call page with phone number
                    Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phones[which]));
                    startActivity(dialIntent);
                }
            });
            builder.show();
        }





}
