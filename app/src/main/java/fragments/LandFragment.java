package fragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocabularybook.AddWord;
import com.example.vocabularybook.MainActivity;
import com.example.vocabularybook.R;
import com.example.vocabularybook.Words;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.ListResourceBundle;

import Adapters.LandAdpter;
import Adapters.SearchAdapter;

import DBTools.DbHelper;
import Util.ParseUtil;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LandFragment extends Fragment {
    private EditText mEt;
    private TextView mTv;
    private TextView Ltrans;
    private TextView Lsentence;
    private RecyclerView mRv;
    private Cursor mCur;
    private View mView;

    private ArrayList<Words> mData;//模糊查找的时候返回的数据
    private LandAdpter mLandAdapter;//ListView的适配
    private static final String TAG = "FragmentActivity";
    private Handler handler;


    public LandFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_land, container, false);
        initView();
        handler=new Handler();
       // Log.d("#Sentence",getSentence("good"));
        return mView;
    }

    public void initView() {
        mEt = mView.findViewById(R.id.sechET1);
        mTv = mView.findViewById(R.id.sech1);
        mRv = mView.findViewById(R.id.LRv1);
        Ltrans = mView.findViewById(R.id.Ltrans);
        Lsentence = mView.findViewById(R.id.Lsentence);
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
            DbHelper helper = new DbHelper(getActivity(), "word_db");
            SQLiteDatabase db_search = helper.getReadableDatabase();
            Cursor csearch = db_search.rawQuery("SELECT * FROM word WHERE word LIKE '" + temp + "%'", null);
            while (csearch.moveToNext()) {
                String name = csearch.getString(csearch.getColumnIndex("word"));
                String trans = csearch.getString(csearch.getColumnIndex("trans"));
                Words sWord = new Words();
                sWord.setName(name);
                sWord.setTrans(trans);
                mData.add(sWord);
            }
            db_search.close();
            csearch.close();

        }
        if (mLandAdapter == null) {
            mLandAdapter = new LandAdpter(getActivity(), mData);
            mRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            mLandAdapter.setOnItemClickListener(new LandAdpter.OnItemClickListener() {

                public void OnItemClick(View view, Words data) {

                    //Toast.makeText(getContext(), data.trans, Toast.LENGTH_SHORT).show();
                    getXml(data.name);
                    Ltrans.setText(data.trans);
                }
            });
            mLandAdapter.setOnItemLongClickListener(new LandAdpter.OnItemLongClickListener() {
                public void OnItemLongClick(View view, final Words data) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_items, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AddWord addWord = new AddWord();
                            ContentValues values = new ContentValues();
                            values.put("name", data.name);
                            values.put("trans", data.trans);
                            values.put("sentence", "");
                            DbHelper helper = new DbHelper(getContext(), "word_db");
                            SQLiteDatabase dbIn = helper.getWritableDatabase();
                            long status = dbIn.insert("Words", null, values);
                            if (status == -1) {
                                Toast.makeText(getContext(), "添加失败", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                            }
                            dbIn.close();

                            return true;
                        }
                    });
                    popupMenu.show();

                }
            });
            mRv.setAdapter(mLandAdapter);

        } else {
            mLandAdapter.refreshData(mData);
        }
    }

    private void getXml(final String wordName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlxml = "https://dict-co.iciba.com/api/dictionary.php?w="+wordName+"&key=591FCB0899133682704ACB0D58E68810";
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(urlxml)
                        .build();
                try {
                    Response response=okHttpClient.newCall(request).execute();
                    String responseData=response.body().string();
                    praseWithPull(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
    private void praseWithPull(final String xmlData) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String example="";
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xmlPullParser = factory.newPullParser();
                    xmlPullParser.setInput(new StringReader(xmlData));
                    int eventType = xmlPullParser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        String nodeName = xmlPullParser.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG: {
                                switch (nodeName) {
                                    case "orig":
                                        example += xmlPullParser.nextText();
                                        example = example.substring(0, example.length() - 1);
                                        break;
                                    case "trans":
                                        example += xmlPullParser.nextText();
                                        break;
                                    default:
                                        break;
                                }
                            }
                            default:
                                break;
                        }
                        eventType = xmlPullParser.next();
                        Lsentence.setText(example);
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}




