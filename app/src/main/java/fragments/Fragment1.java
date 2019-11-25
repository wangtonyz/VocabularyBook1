package fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocabularybook.AddWord;
import com.example.vocabularybook.R;
import com.example.vocabularybook.Words;

import java.util.ArrayList;
import java.util.Locale;

import Adapters.SearchAdapter;

import DBTools.DbHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {
    private EditText mEt;
    private TextView mTv;
    private RecyclerView mRv;
    private Cursor mCur;
    private View mView;
    private TextToSpeech tts;

    private ArrayList<Words> mData;//模糊查找的时候返回的数据
    private SearchAdapter mSearchAdapter;//ListView的适配

    public Fragment1() {
        // Required empty public constructor

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment1, container, false);
        initView();
        return mView;
    }

    public void initView() {
        mEt = mView.findViewById(R.id.sechET);
        mTv = mView.findViewById(R.id.sech);
        mRv = mView.findViewById(R.id.sRv1);
        mEt.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }//文本改变之前执行

            //文本改变的时候执行
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
            }

            public void afterTextChanged(Editable s) {
        mTv.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //如果输入框内容为空，提示请输入搜索内容
                if (TextUtils.isEmpty(mEt.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
                } else {
                    //判断cursor是否为空
                    if (mCur != null) {
                        int columnCount = mCur.getCount();
                        if (columnCount == 0) {
                            Toast.makeText(getActivity(), "not find", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
            }
        });


    }

    //搜索栏文本变化时开始进行模糊查找
    private void search() {
        String temp = mEt.getText().toString().trim();
        mData = new ArrayList<>();
        if (!temp.equals("")) {
            DbHelper helper = new DbHelper(getActivity(),"word_db");
            SQLiteDatabase db_search = helper.getReadableDatabase();
            Cursor csearch = db_search.rawQuery("SELECT * FROM word WHERE word LIKE '" +"%" +temp + "%'", null);
            while (csearch.moveToNext()) {
                String name = csearch.getString(csearch.getColumnIndex("word"));
                String trans = csearch.getString(csearch.getColumnIndex("trans"));
                Words sWord  = new Words();
                sWord.setName(name);
                sWord.setTrans(trans);
                mData.add(sWord);
            }
            db_search.close();
            csearch.close();

        }
        if (mSearchAdapter == null) {
            mSearchAdapter = new SearchAdapter(getActivity(), mData);
            mRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRv.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            mSearchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, final Words data) {
                    //Toast.makeText(getContext(),data.name,Toast.LENGTH_SHORT).show();
                    tts=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            tts.setLanguage(Locale.ENGLISH);
                            tts.speak(data.name,TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                    });
                }
            });
            mSearchAdapter.setOnItemLongClickListener(new SearchAdapter.OnItemLongClickListener() {
                @Override
                public void OnItemLongClick(View view, final Words data) {
                    //Toast.makeText(getContext(),data.name,Toast.LENGTH_SHORT).show();
                    PopupMenu popupMenu = new PopupMenu(getContext(),view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_items,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AddWord addWord = new AddWord();
                            ContentValues values = new ContentValues();
                            values.put("name",data.name);
                            values.put("trans",data.trans);
                            values.put("sentence","");
                            DbHelper helper = new DbHelper(getContext(),"word_db");
                            SQLiteDatabase dbIn = helper.getWritableDatabase();
                            long status = dbIn.insert("Words",null,values);
                            if(status == -1){
                                Toast.makeText(getContext(),"添加失败",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
                            }
                            dbIn.close();

                            return true;
                        }
                    });
                    popupMenu.show();

                }
            });
            mRv.setAdapter(mSearchAdapter);

        } else {
            mSearchAdapter.refreshData(mData);
        }
    }

}




