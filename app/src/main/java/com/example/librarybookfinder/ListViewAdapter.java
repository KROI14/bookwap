package com.example.librarybookfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Books>{

    private Context con;
    private ArrayList<Books> arrBooks;
    private int resource;

    public ListViewAdapter(Context con, int resource, ArrayList<Books> arrBooks){
        super(con, resource, arrBooks);
        this.con = con;
        this.resource = resource;
        this.arrBooks = arrBooks;
    }

    public void update(ArrayList<Books> arrBooks){
        this.arrBooks = new ArrayList<Books>();
        this.arrBooks.addAll(arrBooks);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.arrBooks.size();
    }

    @Override
    public int getPosition(@Nullable Books item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ContextThemeWrapper theme;
        SharedTheme sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            theme = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            theme = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        LayoutInflater layoutInflater = LayoutInflater.from(con).cloneInContext(theme);
        convertView = layoutInflater.inflate(this.resource, parent, false);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtAuthor = convertView.findViewById(R.id.txtAuthor);
        TextView txtYrPub = convertView.findViewById(R.id.txtYrPub);
        ImageView imgBook = convertView.findViewById(R.id.imgBook);

        txtTitle.setText(this.arrBooks.get(position).getTitle());
        txtAuthor.setText(this.arrBooks.get(position).getAuthor());
        txtYrPub.setText(this.arrBooks.get(position).getYearPub());

        Bitmap image = new OptimizeImage().optimizeBitmap(this.arrBooks.get(position).getImg(), 0, 100, 100);
        imgBook.setImageBitmap(image);

        txtTitle.setTextSize(sharedTheme.textSize());
        txtAuthor.setTextSize(sharedTheme.textSize());
        txtYrPub.setTextSize(sharedTheme.textSize());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < arrBooks.size(); i++) {
                    if (arrBooks.get(position).getID() == arrBooks.get(i).getID()){
                        ListViewTask listViewTask = new ListViewTask(arrBooks.get(i).getID(), arrBooks.get(i).getClassify(), getContext());
                        listViewTask.execute();
                    }
                }
            }
        });

        return convertView;
    }
}
