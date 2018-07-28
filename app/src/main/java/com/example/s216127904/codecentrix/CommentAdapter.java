package com.example.s216127904.codecentrix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    private ArrayList<CommentsModel> comments = new ArrayList<>();
    private LayoutInflater mInflater;
    int ticketID;
    private Context c;
    public CommentAdapter(Context context, ArrayList<CommentsModel> comments, int ticketID){
        c = context;
        this.ticketID = ticketID;

        for (int i =0; i<comments.size();i++) {
            if(comments.get(i).TicketID == ticketID) {
                this.comments.add(comments.get(i)) ;
            }
        }

        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position).CommentDescription;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mInflater.inflate(R.layout.comments_layout,null);

        TextView tvComment = v.findViewById(R.id.txtComment);
        TextView tvCommentID = v.findViewById(R.id.txtCommentID);
        tvComment.setText(comments.get(position).CommentDescription);
        tvComment.setText(comments.get(position).CommentID);
        tvComment.setVisibility(View.INVISIBLE);
        return v;
    }
}
