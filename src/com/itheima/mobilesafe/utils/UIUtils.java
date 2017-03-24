package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.itheima.mobilesafe.base.MyApplication;

/**
 * ui�Ĺ����࣬�ܶ�����ί������������ɡ�
 * Created by Administrator on 2016/10/2.
 */
public class UIUtils {

    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }
    public static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }

    /**************������Դ�ļ� *****************/

    /**
     * ���ز����ļ�
     *
     */
    public static View inflate(int id){
        return View.inflate(getContext(),id,null);
    }
    /**
     * ��ȡ�ַ���
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * ��ȡ�ַ�������
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * ��ȡ��ɫ
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * ��ȡͼƬ��Դ
     */
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    /**
     * ����id��ȡ��ɫѡ����
     */
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * ��ȡ�ߴ�
     */
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);// ���ؾ�������ֵ
    }

    /*********************dip��pix��ת��*************/

    public static int dip2px(float dip){
        float desity = getContext().getResources().getDisplayMetrics().density;
        return (int) (desity*dip);
    }

    public static float px2dip(int px){
        float desity = getContext().getResources().getDisplayMetrics().density;
        return px/desity;
    }

    /***************�ж��Ƿ����������߳�************/
    public static boolean isRunOnUIThread(){
        int mTid = android.os.Process.myTid();
        if(mTid== getMainThreadId()){
            return true;
        }
        return false;
    }
    /**************�����߳���ִ��***********/
    public static void RunOnUIThread(Runnable r){
        if(isRunOnUIThread()){
            r.run();
        }else{
            getHandler().post(r);
        }
    }
    
    /*********����˾**********/
    public static void ShowToast(String text){
    	Toast.makeText(getContext(), text, 0).show();
    }
}
