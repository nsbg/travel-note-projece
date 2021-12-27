package com.example.a2021_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.SplittableRandom;

public class MainActivity extends AppCompatActivity {
    private static final String TABLE_NAME = "TravelNoteTable";

    SQLiteDatabase db;

    ListView listView;
    TravelAdapter adapter;

    ArrayList<TravelItem> items = new ArrayList<TravelItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        // 리스트뷰 항목은 단일 선택만 가능하도록 설정
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        adapter = new TravelAdapter();

        EditText edittextplace = (EditText) findViewById(R.id.editTextPlace);   // 방문 장소 입력
        EditText edittextdate = (EditText) findViewById(R.id.editTextDate);     // 방문 날짜 입력
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);         // 여행 만족도 별점 주기

        // 데이터베이스 초기화(생성 또는 열기)
        initDB();

        // 데이터베이스에 정보 입력 & 입력한 정보 리스트뷰에서 바로 보여주기
        Button inputBtn = (Button) findViewById(R.id.button1);
        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // db 내용 중복 출력 방지를 위한 어댑터 초기화
                adapter.delItem(items);

                // 입력 값 받아오기
                String place = edittextplace.getText().toString();
                String date = edittextdate.getText().toString();
                float rate = ratingBar.getRating();

                try {
                    db = openOrCreateDatabase("TravelNote", Activity.MODE_PRIVATE, null);
                    db.execSQL("Insert into TravelNoteTable(place, date, rate) values ('"+place+"', '"+date+"',"+rate+")");
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 확인용 log 메시지
                // Log.v("db", "내용 추가");

                try {
                    db = openOrCreateDatabase("TravelNote", Activity.MODE_PRIVATE, null);

                    Cursor c = db.rawQuery("select place, date, rate from TravelNoteTable", null);
                    
                    // 확인용 log 메시지
                    // Log.v("db 저장된 갯수 : ", String.valueOf(c.getCount()));

                    while (c.moveToNext()) {
                        String visitedPlace = c.getString(0);
                        String visitedDate = c.getString(1);
                        float visitedRate = c.getFloat(2);

                        adapter.addItem(new TravelItem(visitedPlace, visitedDate, visitedRate, 0));
                    }
                    // 사용이 끝난 커서, 데이터베이스 닫기
                    c.close();
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        // 리스트뷰의 아이템을 길게 눌렀을 때 목록에서 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int cnt, checked;

                cnt = adapter.getCount();

                if (cnt > 0) {
                    // 삭제하고자 하는 리스트뷰 아이템을 데이터베이스에서도 삭제하기 위해 데이터베이스 열기
                    db = openOrCreateDatabase("TravelNote", Activity.MODE_PRIVATE, null);

                    // 중복 출력을 막기 위해 데이터베이스에 저장된 정보들 모두 삭제
                    db.execSQL("delete from TravelNoteTable");

                    // 확인용 log 메시지
                    // Log.v("db 내용", "삭제");

                    checked = listView.getCheckedItemPosition();

                    // 확인용 log 메시지
                    // Log.v("선택된 아이템", String.valueOf(checked));

                    if (checked >= - 1 && checked < cnt) {
                        // 리스트뷰에서 선택한 아이템 삭제
                        items.remove(position);
                        
                        // 리스트뷰 갱신
                        listView.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                }
                
                // 아이템을 삭제할 때 리스트뷰의 몇 번째 아이템을 삭제했는지 토스트 메시지 띄워주기
                Toast.makeText(getApplicationContext(), (position+1)+"번째 아이템이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        // 검색 기능을 적용하기 위해 리스트뷰의 text filter 기능 활성화
        listView.setTextFilterEnabled(true);

        EditText editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {   // EditText의 값이 바뀔 때를 담당하는 리스너
            @Override
            public void afterTextChanged(Editable s) {              // 검색했을 때(EditText 값이 바뀌었을 때)의 이벤트
                String searchKeyword = s.toString();

                ((TravelAdapter)listView.getAdapter()).getFilter().filter(searchKeyword) ;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }
    
    // 데이터베이스가 없을 경우 생성하고, 있을 경우 열기 위한 함수
    public void initDB() {
        try {
            db = openOrCreateDatabase("TravelNote", Activity.MODE_PRIVATE, null);
            db.execSQL("create table if not exists TravelNoteTable(id integer PRIMARY KEY autoincrement, place text, date text, rate float); ");
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 확인용 log 메시지
        // Log.v("db", "생성 또는 오픈");
    }

    class TravelAdapter extends BaseAdapter implements Filterable {
        ArrayList<TravelItem> filteredList = items;  // 검색 결과를 저장하기 위한 ArrayList(리스트뷰에 있는 모든 데이터를 가지고 있는 것이 기본 상태)

        Filter keywordFilter;
        
        // Adapter를 위한 기본 생성자
        public TravelAdapter() {

        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        public void addItem(TravelItem item) {
            items.add(item);
        }

        public void delItem(ArrayList list) { items.clear();}

        @Override
        public Object getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            TravelItem item = filteredList.get(position);

            final Context context = viewGroup.getContext();
            
            // 리스트뷰가 보여주는 값이 달라질 때마다 뷰를 불러오는 비효율적인 과정을 방지하기 위해 뷰를 재사용
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.travel_item, viewGroup, false);
            }

            TextView textviewplace = (TextView) convertView.findViewById(R.id.textViewplace);
            TextView textviewdate = (TextView) convertView.findViewById(R.id.textViewdate);
            TextView textviewrate = (TextView) convertView.findViewById(R.id.textViewrate);

            textviewplace.setText(item.getPlace());
            textviewdate.setText(item.getDate());
            textviewrate.setText(String.valueOf(item.getRate()));

            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (keywordFilter == null) {
                keywordFilter = new ListFilter() ;
            }

            return keywordFilter ;
        }

        private class ListFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults() ;

                // 검색하지 않은 상태일 경우 데이터베이스에 저장한 목록 보여주기
                if (constraint == null || constraint.length() == 0) {
                    results.values = items;
                    results.count = items.size() ;
                } else {
                    // 검색 결과를 저장하기 위한 새로운 리스트
                    ArrayList<TravelItem> itemList = new ArrayList<TravelItem>() ;

                    for (TravelItem item : items) {
                        if (item.getPlace().contains(constraint.toString()))
                        {
                            itemList.add(item) ;
                        }
                    }

                    results.values = itemList ;
                    results.count = itemList.size() ;
                }
                
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                // 검색된 데이터로 리스트뷰 업데이트
                filteredList = (ArrayList<TravelItem>) results.values ;

                if (results.count > 0) {
                    notifyDataSetChanged() ;
                } else {
                    notifyDataSetInvalidated() ;
                }
            }
        }
    }
}