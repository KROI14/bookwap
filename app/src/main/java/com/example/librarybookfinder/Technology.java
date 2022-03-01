package com.example.librarybookfinder;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Technology extends Fragment {

    private View view;

    private ListView listView;
    private ArrayList<Books> arrBooks = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContextThemeWrapper theme;
        SharedTheme sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            theme = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            theme = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        view =inflater.cloneInContext(theme).inflate(R.layout.fragment_technology, container, false);
        setHasOptionsMenu(true);

        for(int i = 0; i < MainActivity.getBooks().size(); i++){
            if(MainActivity.getBooks().get(i).getClassify().equals("Technology")){
                arrBooks.add(MainActivity.getBooks().get(i));
            }
        }

        listView = view.findViewById(R.id.listView);
        listView.setAdapter(new ListViewAdapter(getContext(), R.layout.custom_list_view, arrBooks));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuSearch = menu.findItem(R.id.option_search);
        SearchView searchView = (SearchView) menuSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Books> newArr = new ArrayList();
                for (Books books : arrBooks) {
                    if(books.getTitle().toUpperCase().contains(newText.toUpperCase())) {
                        newArr.add(books);
                    }
                    ((ListViewAdapter)listView.getAdapter()).update(newArr);
                }
                return false;
            }
        });
    }
}
