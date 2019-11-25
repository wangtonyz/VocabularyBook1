package Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vocabularybook.R;
import com.example.vocabularybook.Words;

import java.util.ArrayList;

public class LandAdpter extends RecyclerView.Adapter<LandAdpter.myViewHodler> {

    private Context context;
    private ArrayList<Words> WordList;

    //创建构造函数
    public LandAdpter(Context context, ArrayList<Words> wordList) {
        //将传递过来的数据，赋值给本地变量
        this.context = context;//上下文
        this.WordList= wordList;//实体类数据ArrayList
    }

    public myViewHodler onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.lwords_view, null);
        return new myViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(myViewHodler holder, int position) {
        Words data = WordList.get(position);
        holder.WordName.setText(data.name);//获取实体类中的name字段并设置
    }

    @Override
    public int getItemCount() {
        return WordList.size();
    }

     class myViewHodler extends RecyclerView.ViewHolder {
        private TextView WordName;
        public myViewHodler(View itemView) {
            super(itemView);
            WordName = itemView.findViewById(R.id.Lwords);

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
                    if(onItemLongClickListener != null  ){
                        onItemLongClickListener.OnItemLongClick(v,WordList.get(getLayoutPosition()));
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

    public void refreshData(ArrayList<Words> data) {
        if (WordList != null) {
            WordList.clear();
            WordList.addAll(data);
        }
        this.notifyDataSetChanged();
    }
}
