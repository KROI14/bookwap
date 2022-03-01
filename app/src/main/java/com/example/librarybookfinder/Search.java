package com.example.librarybookfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Search extends Fragment {

    private View view;

    private EditText editTitle, editAuthor, editYrPub;
    private Button btnClearTitle, btnClearAuthor, btnClearYrPub, btnSearch;
    private ListView listView;

    private ArrayList<Books> arrBooks;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContextThemeWrapper theme;
        SharedTheme sharedTheme = new SharedTheme(getContext());
        if(sharedTheme.isNightMode() == true){
            theme = new ContextThemeWrapper(getContext(), R.style.DarkTheme);
        }
        else{
            theme = new ContextThemeWrapper(getContext(), R.style.AppTheme);
        }
        view = inflater.cloneInContext(theme).inflate(R.layout.fragment_search, container, false);
        initializeVariables();
        setVariableListener();

        return view;
    }

    public void setVariableListener(){
        arrBooks = MainActivity.getBooks();

        editTitle.addTextChangedListener(new EditTextFunction(editTitle));
        editAuthor.addTextChangedListener(new EditTextFunction(editAuthor));
        editYrPub.addTextChangedListener(new EditTextFunction(editYrPub));

        btnSearch.setOnClickListener(new ButtonFunction());
    }

    public void initializeVariables(){
        editTitle = view.findViewById(R.id.edit_bookTitle);
        editAuthor = view.findViewById(R.id.edit_author);
        editYrPub = view.findViewById(R.id.edit_yrPub);

        btnClearTitle = view.findViewById(R.id.btnClearTitle);
        btnClearAuthor = view.findViewById(R.id.btnClearAuthor);
        btnClearYrPub = view.findViewById(R.id.btnClearYrPub);

        btnSearch = view.findViewById(R.id.btnSearch);

        listView = view.findViewById(R.id.listView);
    }

    public void search(){
        ArrayList<Books> newArrBooks = new ArrayList();
        String upTitle;
        String upAuthor;
        String upYrPub;

        String title = editTitle.getText().toString();
        String author = editAuthor.getText().toString();
        String year = editAuthor.getText().toString();

        for(int i = 0; i < arrBooks.size(); i++){
            upTitle = arrBooks.get(i).getTitle().toUpperCase();
            upAuthor = arrBooks.get(i).getAuthor().toUpperCase();
            upYrPub = arrBooks.get(i).getYearPub().toUpperCase();

            if((editTitle.length() == 0)  && (editAuthor.length() == 0) && (editYrPub.length() == 0)){
                Toast.makeText(getActivity(), "Fill up at least one blank", Toast.LENGTH_SHORT).show();
                break;
            }
            else if(title.matches(" ")){
                Toast.makeText(getActivity(), "Remove the space on Title text box first", Toast.LENGTH_SHORT).show();
                break;
            }
            else if(author.matches(" ")){
                Toast.makeText(getActivity(), "Remove the space on Author text box first", Toast.LENGTH_SHORT).show();
                break;
            }
            else if(year.matches(" ")){
                Toast.makeText(getActivity(), "Remove the space on Year Publish text box first", Toast.LENGTH_SHORT).show();
                break;
            }
            else if(editTitle.length() == 0){
                if(editAuthor.length() == 0){
                    if(upYrPub.contains(editYrPub.getText().toString().toUpperCase())){
                        newArrBooks.add(arrBooks.get(i));
                    }
                }
                else if(editYrPub.length() == 0){
                    if(upAuthor.contains(editAuthor.getText().toString().toUpperCase())){
                        newArrBooks.add(arrBooks.get(i));
                    }
                }
                else if(upYrPub.contains(editYrPub.getText().toString().toUpperCase()) &&
                        upAuthor.contains(editAuthor.getText().toString().toUpperCase())){
                    newArrBooks.add(arrBooks.get(i));
                }
            }
            else if(editAuthor.length() == 0){
                if(editTitle.length() == 0){
                    if(upYrPub.contains(editYrPub.getText().toString().toUpperCase())){
                        newArrBooks.add(arrBooks.get(i));
                    }
                }
                else if(editYrPub.length() == 0){
                    if(upTitle.contains(editTitle.getText().toString().toUpperCase())){
                        newArrBooks.add(arrBooks.get(i));
                    }
                }
                else if(upYrPub.contains(editYrPub.getText().toString().toUpperCase()) &&
                        upTitle.contains(editTitle.getText().toString().toUpperCase())){
                    newArrBooks.add(arrBooks.get(i));
                }
            }
            else if(editYrPub.length() == 0){
                if(editTitle.length() == 0){
                    if(upAuthor.contains(editAuthor.getText().toString().toUpperCase())){
                        newArrBooks.add(arrBooks.get(i));
                    }
                }
                else if(editAuthor.length() == 0){
                    if(upTitle.contains(editTitle.getText().toString().toUpperCase())){
                        newArrBooks.add(arrBooks.get(i));
                    }
                }
                else if(upTitle.contains(editTitle.getText().toString().toUpperCase()) &&
                        upAuthor.contains(editAuthor.getText().toString().toUpperCase())){
                    newArrBooks.add(arrBooks.get(i));
                }
            }
            else if((upTitle.contains(editTitle.getText().toString().toUpperCase())) &&
                    (upAuthor.contains(editAuthor.getText().toString().toUpperCase())) &&
                    (upYrPub.contains(editYrPub.getText().toString().toUpperCase()))){
                newArrBooks.add(arrBooks.get(i));
            }
        }
        listView.setAdapter(new ListViewAdapter(getActivity(), R.layout.custom_list_view, newArrBooks));

        if(!editTitle.getText().toString().isEmpty()){
            new InputLogs("INSERT INTO `searchedtitle`(`UserID`, `Title`) VALUES (" + QRScanner.UserID + ",'" + editTitle.getText().toString() + "')").execute();
        }

        if(!editAuthor.getText().toString().isEmpty()){
            new InputLogs("INSERT INTO `searchedauthor`(`UserID`, `Author`) VALUES (" + QRScanner.UserID + ",'" + editAuthor.getText().toString() + "')").execute();
        }
    }

    class EditTextFunction implements TextWatcher{
        private View view;

        public EditTextFunction(View v){
            this.view = v;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()){
                case R.id.edit_bookTitle:{
                    String str = charSequence.toString().replaceAll("\\s", "`");
                    if(str.length() > 0){
                        btnClearTitle.setVisibility(View.VISIBLE);
                        btnClearTitle.setOnClickListener(new ButtonFunction());
                    }else{
                        btnClearTitle.setVisibility(View.INVISIBLE);
                    }
                }break;
                case R.id.edit_author:{
                    String str = charSequence.toString().replaceAll("\\s", "`");
                    if(str.length() > 0){
                        btnClearAuthor.setVisibility(View.VISIBLE);
                        btnClearAuthor.setOnClickListener(new ButtonFunction());
                    }else{
                        btnClearAuthor.setVisibility(View.INVISIBLE);
                    }
                }break;
                case R.id.edit_yrPub:{
                    String str = charSequence.toString().replaceAll("\\s", "`");
                    if(str.length() > 0){
                        btnClearYrPub.setVisibility(View.VISIBLE);
                        btnClearYrPub.setOnClickListener(new ButtonFunction());
                    }else{
                        btnClearYrPub.setVisibility(View.INVISIBLE);
                    }
                }break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    }

    class ButtonFunction implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(view == btnClearTitle){
                editTitle.setText("");
            }
            else if(view == btnClearAuthor){
                editAuthor.setText("");
            }
            else if(view == btnClearYrPub){
                editYrPub.setText("");
            }
            else if(view == btnSearch){
                search();
            }
        }
    }
}
