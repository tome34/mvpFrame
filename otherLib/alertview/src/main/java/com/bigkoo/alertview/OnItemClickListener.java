package com.bigkoo.alertview;

/**
 * Created by Sai on 15/8/9.
 */
@FunctionalInterface
public interface OnItemClickListener {
     void onItemClick(AlertView view,int position,String data);
}
