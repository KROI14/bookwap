package com.example.librarybookfinder;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BorrowListViewAdapter extends ArrayAdapter<BorrowedBook> {

    private Context con;
    private ArrayList<BorrowedBook> borrowedBooks;
    private int res;

    public BorrowListViewAdapter(Context con, int resource, ArrayList<BorrowedBook> borrowedBooks){
        super(con, resource, borrowedBooks);

        this.con = con;
        this.borrowedBooks = borrowedBooks;
        this.res = resource;
    }

    @Override
    public int getCount() {
        return this.borrowedBooks.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ContextThemeWrapper wrapper;
        SharedTheme sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            wrapper = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            wrapper = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        LayoutInflater inflater = LayoutInflater.from(con).cloneInContext(wrapper);
        convertView = inflater.inflate(res, parent, false);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtAuth = convertView.findViewById(R.id.txtAuthor);
        TextView txtYrPub = convertView.findViewById(R.id.txtYrPub);
        TextView txtStatus = convertView.findViewById(R.id.txtStatus);

        txtTitle.setText(this.borrowedBooks.get(position).getTitle());
        txtAuth.setText(this.borrowedBooks.get(position).getAuthor());
        txtYrPub.setText(this.borrowedBooks.get(position).getYearPub());
        txtStatus.setText("Status: " + this.borrowedBooks.get(position).getStatus() + "\n\nBorrow Type: " + borrowedBooks.get(position).getBorrowType());

        txtTitle.setTextSize(sharedTheme.textSize());
        txtAuth.setTextSize(sharedTheme.textSize());
        txtYrPub.setTextSize(sharedTheme.textSize());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < borrowedBooks.size(); i++){
                    if(borrowedBooks.get(position).getID() == borrowedBooks.get(i).getID()){
                        ListViewTask listViewTask = new ListViewTask(borrowedBooks.get(i).getID(), borrowedBooks.get(i).getClassification(), con);
                        listViewTask.execute();
                    }
                }
            }
        });

        return convertView;
    }
}
