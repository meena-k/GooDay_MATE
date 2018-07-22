package com.example.mate.gooday_mate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mate.gooday_mate.adapter.NoticeAdapter;
import com.example.mate.gooday_mate.service.Item_Notice;

public class NoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initViews();
    }

    private void initViews() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.mipmap.mate_logo);
        setSupportActionBar(toolbar);

        ListView listView = findViewById(R.id.noticelist);
        NoticeAdapter adapter = new NoticeAdapter();

        listView.setAdapter(adapter);

        adapter.addItem(1, getId("ic_patient"), "  [GooDay] 306호 환자 보호자분...", "  소아과 권미나       |       02:23       |       조회 1052", "마주칠때마다 항상 웃어주시고 격려해주세요ㅠㅠ 너무너무 감동");
        adapter.addItem(2, getId("ic_patient"), "  [GooDay] 오늘 식단 !", " 기계실 윤가은       |       02:18       |       조회 932", " 영양사분 짱!!! 매콤한 닭도리탕에 도톰한 계란말이까지... 진짜 너무너무 맛있어요 다들 두그릇씩 드세요:)");
        adapter.addItem(3, getId("ic_patient"), "  [BaDay] 특히 힘드네요 오늘..", "   소아과 최찬호       |       02:15       |       조회 341", "조그만한 실수가 이토록 깨질줄이야 ㅠ 진짜 심각하게 그만 두고싶네요..");
        adapter.addItem(4, getId("ic_patient"), "  [GooDay] 더워도 다들 힘내세요", "   수납 문혜수       |       02:11       |       조회 33", "");
        adapter.addItem(5, getId("ic_patient"), "  [BaDay] 휴...", "   소아과 김영우       |       02:10       |       조회 25", "");
        adapter.addItem(6, getId("ic_patient"), "  [GooDay] 날씨가 딱 한강이네요", "  의사 강은진       |       01:55       |       조회 523", "");
        adapter.addItem(7, getId("ic_patient"), "  [BaDay] 7층 계단 조심하세요", "  간호사 김동진       |       01:23       |       조회 12", "");
        adapter.addItem(8, getId("ic_patient"), "  [BaDay] 차트 정리하실 때 ", "  소아과 박진수       |       00:20       |       조회 665", "");
        adapter.addItem(9, getId("ic_patient"), "  [BaDay] 아침 버스 헬 ", "  의사 윤하린       |       11:59       |       조회 234", "");
        adapter.addItem(10, getId("ic_patient"), " [GooDay] 오늘도 굿데이! ", "    간호사 김한솔       |       11:54       |       조회 1234", "");
        adapter.addItem(11, getId("ic_patient"), " [BaDay] 똥밟았네요 ", "  임상병리 김대중       |       11:53       |       조회 333", "");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle noticeData = new Bundle();
                Item_Notice item = (Item_Notice) parent.getItemAtPosition(position);
                noticeData.putInt("int", item.getId());
                noticeData.putInt("img", item.getIcon());
                noticeData.putString("title", item.getTitle());
                noticeData.putString("metadata", item.getMetadata());
                noticeData.putString("content", item.getContent());
                Intent viewIntent = new Intent(NoticeActivity.this, ViewNoticeActivity.class);
                viewIntent.putExtra("noticeData", noticeData);
                startActivity(viewIntent);

            }
        });

    }

    private int getId(String name) {
        int tempId = getResources().getIdentifier(name, "mipmap", this.getPackageName());
        return tempId;
    }
}
