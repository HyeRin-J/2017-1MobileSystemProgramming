package hyerin.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    TextView locationText;
    Intent intent;
    double nowLatitude, nowLongitude;
    EditText AddName, AddLatitude, AddLongitude, AddRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //layout
        locationText = (TextView)findViewById(R.id.Location);
        AddName = (EditText)findViewById(R.id.AlertName);
        AddLatitude = (EditText)findViewById(R.id.AlertLatitude);
        AddLongitude = (EditText)findViewById(R.id.AlertLongitude);
        AddRadius = (EditText)findViewById(R.id.AlertRadius);

        //intent 통해서 MainActivity에서 넘어온 현재 위도, 경도 값 받아옴
        intent = getIntent();
        nowLatitude = intent.getDoubleExtra("Latitude", 0);
        nowLongitude = intent.getDoubleExtra("Longitude", 0);

        //현재 위도 경도값 설정
        locationText.setText("현재 위치\n위도 : " + nowLatitude + "\n경도 : " + nowLongitude);
        //EditText에 자동으로 추가(수정할 수도 있음)
        AddLatitude.setText(nowLatitude + "");
        AddLongitude.setText(nowLongitude + "");
    }
    //버튼 동작을 위한 onClick 메소드
    public void onClickAdd(View view){
        if(view.getId() == R.id.AddAlert){
            //빈칸이 있을 경우 출력되는 토스트 메시지
            if(AddName.getText().toString().equals("") || AddLatitude.getText().toString().equals("") | AddLongitude.getText().toString().equals("") || AddRadius.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "빈 칸을 채워주세요!", Toast.LENGTH_LONG).show();
            } else{
                Intent intent1 = new Intent();
                //EditText에 유저가 입력한 값을 받아와 intent로 저장
                intent1.putExtra("Name", AddName.getText().toString());
                intent1.putExtra("Latitude", Double.parseDouble(AddLatitude.getText().toString()));
                intent1.putExtra("Longitude", Double.parseDouble(AddLongitude.getText().toString()));
                intent1.putExtra("Radius", Float.parseFloat(AddRadius.getText().toString()));
                //resultcode = 1
                setResult(1, intent1);
                finish();
            }}
    }
}
