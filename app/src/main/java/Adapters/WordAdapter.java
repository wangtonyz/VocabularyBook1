package Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vocabularybook.R;
import com.example.vocabularybook.Words;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.myViewHodler> {
    private Context context;
    private ArrayList<Words> WordList;

    //创建构造函数
    public WordAdapter(Context context, ArrayList<Words> wordList) {
        //将传递过来的数据，赋值给本地变量
        this.context = context;//上下文
        this.WordList= wordList;//实体类数据ArrayList
    }


    @Override
    public myViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建自定义布局
        View itemView = View.inflate(context, R.layout.words_item, null);
        return new myViewHodler(itemView);
    }

    public void updateList(ArrayList<Words> newWordList){
        WordList = newWordList;
    }
    /**
     * 绑定数据，数据与view绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(myViewHodler holder, int position) {
        //根据点击位置绑定数据
        Words data = WordList.get(position);
//        holder.mItemGoodsImg;
        holder.WordName.setText(data.name);//获取实体类中的name字段并设置
        holder.WordTrans.setText(data.trans);//获取实体类中的price字段并设置

    }

    /**
     * 得到总条数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return WordList.size();
    }


    class myViewHodler extends RecyclerView.ViewHolder {

        private TextView WordName;
        private TextView WordTrans;

        public myViewHodler(View itemView) {
            super(itemView);
            WordName = itemView.findViewById(R.id.wName);
            WordTrans =  itemView.findViewById(R.id.wTrans);
            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此处回传点击监听事件
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, WordList.get(getLayoutPosition()));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.OnItemLongClick(v, WordList.get(getLayoutPosition()));
                    }
                    return true;
                }
            });

        }

    }
    public interface OnItemClickListener {
        public void OnItemClick(View view, Words data);
    }

    public interface OnItemLongClickListener {
        public void OnItemLongClick(View view, Words data);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public  void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void replaceAll() {
        notifyDataSetChanged();
    }


}
