package DBTools;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private final static int version =1;
    private final static String SWORD="SWORD";
    private Context con ;

    public DbHelper(Context context, String name , SQLiteDatabase.CursorFactory factory,int version){
            super(context, name, factory, version);

    }

    public DbHelper(Context context, String name){
        this(context,name,version);
        con = context;
    }

    public DbHelper (Context context ,String name , int version){
        this(context,name,null,version);
    }

    public DbHelper(Context context){
        super(context,"word_db",null,version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD,"Create a DB");
        String Sql = "create table Words(name text,trans text,sentence text)";
        db.execSQL(Sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i(SWORD,"update a DB");
    }


}
