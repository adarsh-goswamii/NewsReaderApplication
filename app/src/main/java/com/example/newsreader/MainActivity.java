package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="News Reader";
    private RecyclerView recyclerView;
    private ArrayList<NewsItem> news;
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        news= new ArrayList<>();
        recyclerView= findViewById(R.id.recyclerView);
        adapter= new NewsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new GetNews().execute();
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
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setList(news);
        }

        private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
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

            parser.next();
            // parser.next() return an integer which tells what we have an opening tag, maybe end tag or a text.
            parser.require(XmlPullParser.START_TAG, null, "rss");
            // we are going to keep on parsing the xml file until we get an end tag for rss.
            while(parser.next()!= XmlPullParser.END_TAG)
            {
                if(parser.getEventType()!= XmlPullParser.START_TAG) // skip any metadata that we may get till we see an starting tag
                    continue;

                parser.require(XmlPullParser.START_TAG, null, "channel");
                while(parser.next()!= XmlPullParser.END_TAG) // till the tag channel ends check code beautify website and paste the link of rss.
                {
                    if(parser.getEventType()!= XmlPullParser.START_TAG)
                        continue;

                    String tag= parser.getName();
                    if(tag.equals("item"))
                    {
                        parser.require(XmlPullParser.START_TAG, null, "item");
                        String title="", link="", des="", date="";
                        while(parser.next()!= XmlPullParser.END_TAG) // we will parse through our item and get all the details which we want.
                        {
                            if(parser.getEventType()!= XmlPullParser.START_TAG)
                                continue;

                            String tagName= parser.getName();
                            //Log.e(TAG, "TAg name =:" +tagName);
                            if(tagName.equals("title"))
                            {
                                title= getContent(parser, "title");
                            }
                            else if(tagName.equals("link"))
                            {
                                link= getContent(parser, "link");
                            }
                            else if(tagName.equals("description"))
                            {
                               // Log.e(TAG, "TAG DESC FOUND");
                                des= getContent(parser, "description");
                            }
                            else if(tagName.equalsIgnoreCase("pubDate"))
                            {
                               // Log.e(TAG, "TAG FOUND");
                                date= getContent(parser, "pubDate");
                            }
                            else
                            {
                                skipTag(parser);
                            }
                        }
                        NewsItem obj= new NewsItem(title, des, link, date);
                        //Log.e(TAG, date);
                        news.add(obj);
                    }
                    else
                        skipTag(parser);
                }
            }
        }

        private String getContent(XmlPullParser parser, String title) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, title);

            String content="";
            if(parser.next()== XmlPullParser.TEXT)
            {
                content= parser.getText();
                parser.next();
            }

            return content;
        }

        private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
            if(parser.getEventType()!= XmlPullParser.START_TAG)
                throw new IllegalStateException();
            else
            {
                /**
                 * basically this part of the code is where we are skipping the irrelevant tags.
                 * we have initially given number value 1 as we know we have are under a starting tag
                 *
                 * (i). what we are doing is when we get a start tag we increase the value
                 * (ii). when we get an closing tag we decrease the value of number.
                 *
                 * Think of it like this we can have a nested tag so all the tags starting inside the tag which we wanna skip will eventually
                 * get neglected and the value only goes to zero when we receive the tags which we want.
                 *
                 * <tag> initially value is 1
                 *     <inner tag> number++ as opening tag =2
                 *         </inner> number-- =1
                 *      <inner tag> =2
                 *          </inner>  =1
                 * </end tag> =0
                 */


                int number= 1;
                while(number!=0)
                {
                    switch(parser.next())
                    {
                        case XmlPullParser.START_TAG: {
                            number++;
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            number--;
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
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