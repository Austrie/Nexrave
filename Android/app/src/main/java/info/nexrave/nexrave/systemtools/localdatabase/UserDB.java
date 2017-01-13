package info.nexrave.nexrave.systemtools.localdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by yoyor on 12/21/2016.
 */

public class UserDB {
//
//    class Row extends Object {
//        public long _Id;
//        public String code;
//        public String name;
//        public String gender;
//    }
//
//    private static final String DATABASE_CREATE =
//            "create table USERINFO(_id integer primary key autoincrement, "
//                    + "code text not null,"
//                    + "name text not null"
//                    +");";
//
//    private static final String DATABASE_NAME = "USERDB";
//
//    private static final String DATABASE_TABLE = "USERINFO";
//
//    private static final int DATABASE_VERSION = 1;
//
//    private SQLiteDatabase db;
//
//    public UserDB(Context ctx) {
//        try {
//            db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
//            db.execSQL(DATABASE_CREATE);
//        } catch (Exception e) {
//            Log.d("Nexrave USERDB: ", e.toString());
//        }
//    }
//
//    public void close() {
//        db.close();
//    }
//
//    public void createRow(String code, String name) {
//        ContentValues initialValues = new ContentValues();
//        initialValues.put("code", code);
//        initialValues.put("name", name);
//        db.insert(DATABASE_TABLE, null, initialValues);
//    }
//
//    public void deleteRow(long rowId) {
//        db.delete(DATABASE_TABLE, "_id=" + rowId, null);
//    }
//
//    public List<Row> fetchAllRows() {
//        ArrayList<Row> ret = new ArrayList<Row>();
//        try {
//            Cursor c =
//                    db.query(DATABASE_TABLE, new String[] {
//                            "_id", "code", "name"}, null, null, null, null, null);
//            int numRows = c.count();
//            c.first();
//            for (int i = 0; i < numRows; ++i) {
//                Row row = new Row();
//                row._Id = c.getLong(0);
//                row.code = c.getString(1);
//                row.name = c.getString(2);
//                ret.add(row);
//                c.next();
//            }
//        } catch (SQLException e) {
//            Log.e("Exception on query", e.toString());
//        }
//        return ret;
//    }
//
//    public Row fetchRow(long rowId) {
//        Row row = new Row();
//        Cursor c =
//                db.query(true, DATABASE_TABLE, new String[] {
//                                "_id", "code", "name"}, "_id=" + rowId, null, null,
//                        null, null);
//        if (c.count() > 0) {
//            c.first();
//            row._Id = c.getLong(0);
//            row.code = c.getString(1);
//            row.name = c.getString(2);
//            return row;
//        } else {
//            row.rowId = -1;
//            row.code = row.name= null;
//        }
//        return row;
//    }
//
//    public void updateRow(long rowId, String code, String name) {
//        ContentValues args = new ContentValues();
//        args.put("code", code);
//        args.put("name", name);
//        db.update(DATABASE_TABLE, args, "_id=" + rowId, null);
//    }
//    public Cursor GetAllRows() {
//        try {
//            return db.query(DATABASE_TABLE, new String[] {
//                    "_id", "code", "name"}, null, null, null, null, null);
//        } catch (SQLException e) {
//            Log.e("Exception on query", e.toString());
//            return null;
//        }
//    }
}
