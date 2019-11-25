package fragments;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocabularybook.R;

import Adapters.WordAdapter;
import com.example.vocabularybook.Words;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import DBTools.DbHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordFragment extends Fragment {

    private View view;
    public RecyclerView wordRV;
    private ArrayList<Words> WordList;
    private WordAdapter wAdaper ;
    private boolean isCreated;
    private TextView popsentence;
    private PopupWindow popupWindow;
    private Handler handler;
    public WordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2, container, false);
        initData();
        initRecyclerView();
        handler = new Handler();
        isCreated = true;
        return view;
    }
    private void initData() {
        WordList = WordsFromTable();
        for (int i=0;i<WordList.size();i++){
            Words word = new Words();
            word.setName(WordList.get(i).getName());
            word.setTrans(WordList.get(i).getTrans());
        }
    }

    /**
     * TODO 对recycleview进行配置
     */

    private void initRecyclerView() {

        //获取RecyclerView
        wordRV= view.findViewById(R.id.Rv1);
        //创建adapter
        wAdaper = new WordAdapter(getActivity(), WordList);
        //给RecyclerView设置adapter
        wordRV.setAdapter(wAdaper);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        wordRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        wordRV.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        wAdaper.setOnItemClickListener(new WordAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Words data) {
                //此处进行监听事件的业务处理
                //Toast.makeText(getContext(),"我是item:"+data.getSentence(),Toast.LENGTH_SHORT).show();
                getXml(data.name);

            }
        });
        wAdaper.setOnItemLongClickListener(new WordAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, final Words data) {
                PopupMenu popupMenu = new PopupMenu(getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        DbHelper helper = new DbHelper(getContext(),"word_db");
                        SQLiteDatabase dbIn = helper.getWritableDatabase();
                        String wName = data.name;
                        dbIn.delete("Words","name=?",new String[]{wName});
                        long satus = dbIn.delete("Words","name=?",new String[]{wName});
                        if(satus == -1){
                            Toast.makeText(getContext(),"删除失败",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT).show();
                        }
                        dbIn.close();
                        onResume();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });


    }
    public ArrayList<Words> WordsFromTable(){
        WordList =new ArrayList<Words>();
        DbHelper helper = new DbHelper(getContext(),"word_db");
        SQLiteDatabase dbIn = helper.getReadableDatabase();
        Cursor cursor = dbIn.rawQuery("select * from Words",null);
        if(cursor != null){
            while(cursor.moveToNext()){
                Words sWord = new Words();
                Log.d("#DB","name:"+cursor.getString(0));
                String wordName = cursor.getString(0);
                sWord.setName(wordName);
                Log.d("#DB","trans:"+cursor.getString(1));
                String wordTrans = cursor.getString(1);
                sWord.setTrans(wordTrans);
                WordList.add(sWord);
            }
        }
        dbIn.close();
        return WordList;
    }

    @Override
    public void onResume() {
        super.onResume();
        wAdaper.updateList(WordsFromTable());
        wordRV.setAdapter(wAdaper);
    }


public void initPopweindow(String pSen){
    View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popwindow, null);
    popupWindow = new PopupWindow(contentView,
            RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
    popupWindow.setContentView(contentView);

    View rootview = LayoutInflater.from(getContext()).inflate(R.layout.fragment2, null);
    popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

    TextView pSentence = (TextView)contentView.findViewById(R.id.popsentence);
    pSentence.setText(pSen);
}

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isCreated) {
            return; }

        if (isVisibleToUser) {
            wAdaper.updateList(WordsFromTable());
            wordRV.setAdapter(wAdaper);
            wAdaper.notifyDataSetChanged();
        } else {
            // 相当于Fragment的onPause
            System.out.println("ChatFragment ---setUserVisibleHint---isVisibleToUser - FALSE");
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


                    }
                    initPopweindow(example);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }
}





