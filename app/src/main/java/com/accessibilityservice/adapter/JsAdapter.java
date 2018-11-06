package com.accessibilityservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.accessibilityservice.model.JsCategoryModel;

import java.util.Iterator;
import java.util.List;

public class JsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<JsCategoryModel> list;

    public JsAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        int i = 0;
        if (this.list == null) {
            return 0;
        }
        Iterator it = this.list.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((JsCategoryModel) it.next()).getItemCount() + i2;
        }
    }

    public Object getItem(int i) {
        if (this.list != null && i >= 0 && i <= getCount()) {
            int i2 = 0;
            Iterator it = this.list.iterator();
            while (true) {
                int i3 = i2;
                if (!it.hasNext()) {
                    break;
                }
                JsCategoryModel jsCategoryModel = (JsCategoryModel) it.next();
                int itemCount = jsCategoryModel.getItemCount();
                int i4 = i - i3;
                if (i4 < itemCount) {
                    return jsCategoryModel.getItem(i4);
                }
                i2 = i3 + itemCount;
            }
        }
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        if (this.list != null && i >= 0 && i <= getCount()) {
            int i2 = 0;
            for (JsCategoryModel itemCount : this.list) {
                int itemCount2 = itemCount.getItemCount();
                if (i - i2 == 0) {
                    return 0;
                }
                i2 = itemCount2 + i2;
            }
        }
        return 1;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        switch (getItemViewType(i)) {
            case 0:
            case 1:
            default:
                return null;
        }
    }

    public int getViewTypeCount() {
        return 2;
    }
}
