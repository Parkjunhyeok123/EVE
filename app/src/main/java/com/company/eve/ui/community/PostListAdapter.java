package com.company.eve.ui.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.company.eve.R;
import com.company.eve.model.Post;

import java.util.List;

import utils.TimeUtils;

public class PostListAdapter extends ArrayAdapter<Post> {

    private Context context;
    private int resource;
    private List<Post> postList;

    public PostListAdapter(@NonNull Context context, int resource, @NonNull List<Post> postList) {
        super(context, resource, postList);
        this.context = context;
        this.resource = resource;
        this.postList = postList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        // 현재 position에 해당하는 Post 객체 가져오기
        Post post = postList.get(position);

        // View의 각 요소에 데이터 설정하기
        TextView titleTextView = convertView.findViewById(R.id.textViewTitle);
        TextView contentTextView = convertView.findViewById(R.id.textViewContent);
        TextView authorTextView = convertView.findViewById(R.id.textViewAuthor);
        TextView timeTextView = convertView.findViewById(R.id.textViewTime);
        TextView viewCountTextView = convertView.findViewById(R.id.textViewViewCount);

        // 데이터를 각 View에 설정
        titleTextView.setText(post.getTitle());
        contentTextView.setText(post.getContent());
        authorTextView.setText(post.getAuthor());

        // 작성 시간 설정
        String timeText = TimeUtils.getTimeDifference(post.getTimestamp());
        timeTextView.setText(timeText);

        // 조회수 설정
        viewCountTextView.setText("조회수: " + post.getViewCount());

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
