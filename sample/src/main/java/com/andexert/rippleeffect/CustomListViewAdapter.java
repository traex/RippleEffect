/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Robin Chutaux
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.andexert.rippleeffect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Author :    Chutaux Robin
 * Date :      1/6/2015
 */
public class CustomListViewAdapter extends BaseAdapter
{
    private final ArrayList<String> textArrayList;
    private final LayoutInflater layoutInflater;

    public CustomListViewAdapter(final Context context)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.textArrayList = new ArrayList<>();
    }

    @Override
    public int getCount()
    {
        return textArrayList.size();
    }

    @Override
    public String getItem(int position)
    {
        return textArrayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.row_view, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(textArrayList.get(position));

        return convertView;
    }

    public void updateList(ArrayList<String> stringArrayList)
    {
        this.textArrayList.clear();
        this.textArrayList.addAll(stringArrayList);
        this.notifyDataSetChanged();
    }

    private class ViewHolder
    {
        TextView textView;

        public ViewHolder(View v)
        {
            textView = (TextView) v.findViewById(R.id.text);
        }
    }
}
