package hyerin.assignment1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RemoveActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
        //layout
        editText = (EditText)findViewById(R.id.RemoveName);
    }
    //버튼 동작을 위한 onClick 메소드
    public void onClickRemove(View view){
        if(view.getId() == R.id.RemoveAlert){
            Intent intent = new Intent();
            //EditText에 유저가 입력한 값을 받아와 intent로 저장
            intent.putExtra("Name", editText.getText().toString());
            //resultcode = 2
            setResult(2, intent);
            finish();
        }
    }
}
