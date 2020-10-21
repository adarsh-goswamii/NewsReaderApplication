package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="News Reader";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView= findViewById(R.id.recyclerView);
    }

    // We want to load the news from a website and we will do that in a background task and as we dont want
    // to block the user interface that's why we will use AsyncTask for it.
    private class GetNews extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            InputStream inputStream= getInputStream();
            if(inputStream!= null)
            {
                try {
                    initXMLPullParser(inputStream);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException {
            XmlPullParser parser= Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            /**
             * XMLPullParser : as we do not want to show all the data from the xml rss feed that we are getting from the website
             * we parse the xml file.
             *
             * FEATURE_PROCESS_NAMESPACE : is a process of differentiating different tags with same name but here we dont want to make
             * the code complicated so we have passed false for the feature process namespace.
             */
        }

        private InputStream getInputStream()
        {
            try {
                URL url= new URL("https://www.autosport.com/rss/feed/f1");
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                // we can also add certain features for out connection few of them are shown below
                connection.setRequestMethod("GET");// as we want data from the server.
                connection.setDoInput(true);// we would have send something to website we would have used setDoOutput

                return connection.getInputStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    }
}