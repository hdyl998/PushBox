package com.hdyl.pushbox.db;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库处理助手，这里和业务逻辑无关是一个通用工具类
 * Created by liugd on 2016/12/22.
 */

public class DBManagerHelper {


    private String[] stringFields = null;// 静态字段 ，自动生成的，BasketBall的字段名字
    private Field[] flds = null;// 静态字段,自动生成的BasketBall的字段属性

    public DBManagerHelper(Class<?> clazz) {
        initAllDatas(clazz);
    }

    /***
     * 初始化所有的字段
     *
     * @param clazz 实体类
     */
    private void initAllDatas(Class<?> clazz) {
        Field[] tmp = clazz.getFields();// 得到有的公有字段，不需要创建表的字段请设置为实体类的私有字段
        //防止静态变量含有"$"出现创建时的异常
        ArrayList<Field> list = new ArrayList<Field>();
        for (int i = 0; i < tmp.length; i++) {
            if (!tmp[i].getName().contains("$")) {
                list.add(tmp[i]);
            }
        }
        flds = new Field[list.size()];
        stringFields = new String[flds.length];
        for (int i = 0; i < flds.length; i++) {
            Field field = list.get(i);
            flds[i] = field;
            stringFields[i] = field.getName();//得到字段的名字
        }
    }

    /***
     * 创建数据表
     *
     * @param db         数据库
     * @param TABLE_NAME 表单
     */
    public void onCreate(SQLiteDatabase db, String TABLE_NAME) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (Field field : flds) {
            Class<?> clazz = field.getType();//得到字段的类型Intger、String、Boolean、Long
            // 加入字段
            stringBuilder.append(field.getName());//得到字段的名字
            // 判断 类型
            if (clazz == String.class) {
                stringBuilder.append(" nvarchar default \"\",");
            } else if (clazz == int.class || clazz == boolean.class || clazz == long.class) {
                stringBuilder.append(" INTEGER default 0,");
            }
        }
        // 去掉最后一个逗号
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        // 创建三张表
        String sql = "CREATE TABLE " + TABLE_NAME + stringBuilder.substring(0);
        db.execSQL(sql);
    }

    /***
     * 设置对象的数据，通过映射设置数据
     *
     * @param ball   数据对象
     * @param cursor 游标
     */
    public void setObjectData(Object ball, Cursor cursor) {
        for (int i = 0; i < flds.length; i++) {
            try {
                Object object = flds[i].get(ball);
                if (object == null) {
                }
                if (object instanceof Integer){ //判断类型
                    flds[i].set(ball, cursor.getInt(i));
                } else if (object instanceof String) {
                    flds[i].set(ball, cursor.getString(i));
                } else if (object instanceof Boolean) {
                    flds[i].set(ball, cursor.getInt(i) == 1 ? true : false);
                } else if (object instanceof Long) {
                    flds[i].set(ball, cursor.getLong(i));
                }
            } catch (Exception e) {
//                LogUitls.PrintObject("数据库", "查询数据时取值映射出现问题" + e);
            }
        }
    }

    /**
     * 得到对象所有的数据存入键值对
     *
     * @param item 条目数据
     * @return 键值对 数据
     */
    public ContentValues object2ContentValues(Object item) {
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < flds.length; i++) {
                String nameString = stringFields[i];// 得到字段名字
                Object object = flds[i].get(item);
                if (object == null) object = "";
                if (object instanceof Integer) values.put(nameString, (Integer) object);
                else if (object instanceof String) {
                    values.put(nameString, (String) object);
                } else if (object instanceof Boolean) {
                    values.put(nameString, (Boolean) object ? 1 : 0);// 真为1，假为0
                } else if (object instanceof Long) {
                    values.put(nameString, (Long) object);
                }
            }
        } catch (Exception e) {
//            LogUitls.PrintObject("数据库", "取值映射出现问题" + e);
        }
        return values;// 返回键值对
    }

    //GET SET METHOD
    public Field[] getFlds() {
        return flds;
    }

    public void setFlds(Field[] flds) {
        this.flds = flds;
    }

    public String[] getStringFields() {
        return stringFields;
    }

    public void setStringFields(String[] stringFields) {
        this.stringFields = stringFields;
    }
}
