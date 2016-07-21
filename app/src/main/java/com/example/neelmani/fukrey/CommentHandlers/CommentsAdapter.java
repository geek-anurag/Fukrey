package com.example.neelmani.fukrey.CommentHandlers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.neelmani.fukrey.R;

import java.util.List;

public class CommentsAdapter  extends BaseAdapter {

    private final List<Comments> comments;
    private Activity context;

    public CommentsAdapter(Activity context, List<Comments> chatMessages) {
        this.context = context;
        this.comments = chatMessages;
    }

    @Override
    public int getCount() {
        if (comments != null) {
            return comments.size();
        } else {
            return 0;
        }
    }

    @Override
    public Comments getItem(int position) {
        if (comments != null) {
            return comments.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Comments comments = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_comment, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean isTitle = comments.getIsTitle() ;
        boolean isInfo = comments.getIsInfo();
        setCommentsAlignment(convertView,holder, isTitle, isInfo);
        holder.txtMessage.setText(comments.getMessage());
        if(isTitle || isInfo)
            holder.txtInfo.setText("");
        else
            holder.txtInfo.setText(comments.getDate());


        return convertView;
    }

    public void add(Comments message) {
        comments.add(message);
    }

    public void add(List<Comments> messages) {
        comments.addAll(messages);
    }

    private void setCommentsAlignment(View convertView, ViewHolder holder, boolean isTitle ,boolean isInfo) {

         if(isInfo) {
           holder.txtMessage.setTypeface(null, Typeface.ITALIC);
             holder.image.setVisibility(convertView.GONE);
             holder.userName.setVisibility(convertView.GONE);
          }
         else if (isTitle) {
             holder.txtMessage.setTypeface(null, Typeface.BOLD);
             holder.image.setVisibility(convertView.GONE);
             holder.userName.setVisibility(convertView.GONE);
         }
       else {
         }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.userName=(TextView) v.findViewById(R.id.txtUserName);
        holder.image=(ImageView) v.findViewById(R.id.imageView);
        return holder;
    }


    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public TextView userName;
        public ImageView image;
        }
}

