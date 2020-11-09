package com.example.autores;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private EditText inputBook;
    private TextView bookTitle;
    private TextView bookAuthor;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBook = (EditText)findViewById(R.id.inputbook);
        bookTitle = (TextView)findViewById(R.id.booktitle);
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        imageView = findViewById(R.id.img);



    }

    public void searchBook(View view) {
        String searchString = inputBook.getText().toString();
        //TODO : requerir el uso del servicio externo
        new GetBook(bookTitle,bookAuthor).execute(searchString);
    }
    //Android 9 en adelante ya se vuelve absoleta
    //STRING : VOID : STRING: resultado
    public class GetBook extends AsyncTask<String,Void,String> {

        private WeakReference<TextView> mTextTitle;
        private WeakReference<TextView> mTextAuthor;


        //constructores
        public GetBook(TextView mTextTitle,TextView mTextAuthor) {
            this.mTextTitle = new WeakReference<>(mTextTitle);
            this.mTextAuthor = new WeakReference<>(mTextAuthor);

        }

        @Override
        protected String doInBackground(String... strings) {
            return NetUtilities.getBookInfo(strings[0]);
        }

        @Override
        protected void onPostExecute(String s){

            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int i = 0;

                String title = null;
                String author = null;
                String smallThumbnail = null;

                while( i < itemsArray.length() && (title == null && author == null)){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                    try {
                        title = volumeInfo.getString("title");
                        author = volumeInfo.getString("authors");

                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                        try {
                            smallThumbnail = imageLinks.getString("smallThumbnail");

                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    i++;
                    if(title != null && author != null && smallThumbnail != null){
                        mTextTitle.get().setText(title);
                        mTextAuthor.get().setText(author);


                        Picasso.get().load(smallThumbnail).into(imageView);

                    }else{
                        mTextTitle.get().setText("No existen resultados para la consulta");
                        mTextAuthor.get().setText(" ");

                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
