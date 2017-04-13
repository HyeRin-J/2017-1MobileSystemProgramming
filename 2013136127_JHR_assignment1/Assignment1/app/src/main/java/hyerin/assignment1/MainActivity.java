package hyerin.assignment1;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //공유 프레퍼런스
    SharedPreferences mSetting;
    public static final String SharedAlert = "StoredAlert";

    //등록할 경보를 위한 배열 및 변수들
    String[] Name = new String[3];
    Double[] Latitude = new Double[3];
    Double[] Longitude = new Double[3];
    Float[] Radius = new Float[3];
    double nowLatitude, nowLongitude;

    //GPS
    LocationManager locManager;
    AlertReceiver receiver;

    //텍스트 뷰
    TextView[] alertView = new TextView[3];
    PendingIntent proximityIntent;

    //퍼미션
    boolean isPermitted = false;
    boolean isLocRequested = false;
    boolean isAlertRegistered = false;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    //Receiver 내부 클래스로 구현
    public class AlertReceiver extends BroadcastReceiver {
        String name = "";

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);

            if(isEntering)
                Toast.makeText(context, intent.getStringExtra("AlertName") + "에 접근중입니다..", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, intent.getStringExtra("AlertName") + "에서 벗어납니다..", Toast.LENGTH_LONG).show();
        }
    }
    //내부 클래스로 구현
    LocationListener locationListener = new LocationListener(){

        //현재 위도와 경도 값을 지정
        @Override
        public void onLocationChanged(Location location) {
            nowLatitude = location.getLatitude();
            nowLongitude = location.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertView[0] = (TextView)findViewById(R.id.Alert1);
        alertView[1] = (TextView)findViewById(R.id.Alert2);
        alertView[2] = (TextView)findViewById(R.id.Alert3);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestRuntimePermission();

        //공유 프레퍼런스에 값이 있는지를 불러오고 텍스트 뷰에 표시
        for(int i = 0; i < 3; i++) {
            sData(i);
            alertView[i].setText(Name[i]);
        }
    }

    //공유 프레퍼런스에서 값을 불러오는 메소드
    public void sData(int i){
        mSetting = getSharedPreferences(SharedAlert,0);

        //key값에 대응하여 저장된 값을 불러옴
        String sName = mSetting.getString("SName" + i,"");
        double sLatitude = Double.parseDouble(mSetting.getString("SLatitude" + i, "0.0"));
        double sLongitude = Double.parseDouble(mSetting.getString("SLongitude" + i, "0.0"));
        float sRadius = mSetting.getFloat("SRadius" + i, 1);

        setData(sName, sLatitude, sLongitude, sRadius, i);
    }

    //퍼미션 부분
    private void requestRuntimePermission() {
        //*******************************************************************
        // Runtime permission check
        //*******************************************************************
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // ACCESS_FINE_LOCATION 권한이 있는 것
            isPermitted = true;
        }
        //*********************************************************************
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // read_external_storage-related task you need to do.

                    // ACCESS_FINE_LOCATION 권한을 얻음
                    isPermitted = true;
                    //앱을 처음 실행했을 때에 GPS 권한을 얻기 전 GPS 불러오는 부분이 있어 아에 requestPermission 부분에 넣어둠.
                    requestGPS();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // 권한을 얻지 못 하였으므로 location 요청 작업을 수행할 수 없다
                    // 적절히 대처한다
                    isPermitted = false;

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //버튼 동작을 위한 onClick 메소드
    public void onClickMain(View view){
        //등록 버튼
        if (view.getId() == R.id.RegisterAlert) {
            //실내에선 GPS 신호를 받을 때까지 오래걸려서 원하는 값이 나오지 않을 수 있기 때문에 설정
            if(nowLatitude == 0.0 && nowLongitude == 0.0){
                Toast.makeText(this, "GPS 신호를 받아올 때까지 조금 기다려주세요!", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                //현재 위도와 경도 값을 넘겨줌
                intent.putExtra("Latitude", nowLatitude);
                intent.putExtra("Longitude", nowLongitude);
                //AddActivity에서 입력한 값을 받아와야 하므로 startActivityForResult 사용
                startActivityForResult(intent, 1);
            }}
        //해제 버튼
        else if (view.getId() == R.id.DeleteAlert) {
            //Name[0]이 == ""이면 등록된 경보가 없음.
            if(Name[0].equals("")){
                Toast.makeText(getApplicationContext(), "해제할 경보가 없습니다.", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(MainActivity.this, RemoveActivity.class);
                //RemoveActivity에서 입력한 값을 받아와야 하므로 startActivityForResult 사용
                startActivityForResult(intent, 1);
            }}
    }
    //startActivityForResult를 통해 받아온 data 처리를 위한 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        int i;
        if(requestCode == 1){
            if(resultCode == 1){    //AddActivity
                for(i = 0; i < 3; i++) {
                    //빈칸에 값을 채움
                    if (Name[i] == ""){
                        //key값을 통해 값을 불러옴
                        Name[i] = data.getStringExtra("Name");
                        Latitude[i] = data.getDoubleExtra("Latitude", 0);
                        Longitude[i] = data.getDoubleExtra("Longitude", 0);
                        Radius[i] = data.getFloatExtra("Radius", 0);

                        break;  //끝까지 for문을 수행할 필요가 없음
                    }
                }
                //등록된 경보가 3개인데 더 등록하려고 할 경우
                if(i == 3)  Toast.makeText(getApplicationContext(), "경보 등록은 3개까지 가능합니다.", Toast.LENGTH_LONG).show();
            }
            else if(resultCode == 2){   //RemoveActivity
                int cnt = 0;
                for(i = 0; i < 3; i++){ //for문을 돌면서 key값으로 받아온 이름과 비교하여 초기화 수행
                    if(Name[i].equals(data.getStringExtra("Name"))){
                        Initialize(i);
                    }else {
                        cnt++;  //이름이 다른 경우 카운트 용
                    }}
                //for문을 끝까지 수행했는데도 맞는 이름이 없을 때 출력
                if(cnt == 3)    Toast.makeText(this, data.getStringExtra("Name") + "으로 등록된 경보가 없습니다.", Toast.LENGTH_LONG).show();
                //등록된 경보가 해제되면서 생기는 빈칸을 당겨옴
                for(i = 0; i < 2; i++){
                    if(Name[i].equals("")){
                        setData(Name[i+1], Latitude[i+1], Longitude[i+1], Radius[i+1], i);
                        Initialize(i+1);
                    }}
            }
            //텍스트 뷰 재설정
            for(i = 0; i < 3; i++) alertView[i].setText(Name[i]);
        }
    }
    //변수 초기화
    public void Initialize(int i){
        Name[i] = "";
        Latitude[i] = Longitude[i] = 0.0;
        Radius[i] = (float)0.0;
    }
    //변수 값 설정
    public void setData(String n, double la, double lo, float rad, int i){
        Name[i] = n;
        Latitude[i] = la;
        Longitude[i] = lo;
        Radius[i] = rad;
    }
    //근접 경보 등록
    public void register(int i, double latitude, double longitude, float radius){
        receiver = new AlertReceiver();
        IntentFilter filter = new IntentFilter(Name[i]);
        registerReceiver(receiver, filter);

        // ProximityAlert 등록을 위한 PendingIntent 객체 얻기
        Intent intent = new Intent(Name[i]);
        intent.putExtra("AlertName", Name[i]);
        proximityIntent = PendingIntent.getBroadcast(this, i, intent, 0);
        try {
            // 근접 경보 등록 메소드
            // void addProximityAlert(double latitude, double longitude, float radius, long expiration, PendingIntent intent)
            locManager.addProximityAlert(latitude, longitude, radius, -1, proximityIntent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        isAlertRegistered = true;
    }

    public void requestGPS(){
        try {
            if(isPermitted) {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                isLocRequested = true;
            }
            else
                Toast.makeText(this, "Permission이 없습니다.", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();

        //MainActivity 재생성시 GPS 다시 호출
        requestGPS();

        for(int i = 0; i < 3; i++) {
            if(!Name[i].equals(""))
                register(i, Latitude[i], Longitude[i], Radius[i]);
        }
    }
    @Override
    protected void onPause(){
        super.onPause();

        // 자원 사용 해제
        try {
            if(isLocRequested) {
                locManager.removeUpdates(locationListener);
                isLocRequested = false;
            }
            if(isAlertRegistered) {
                locManager.removeProximityAlert(proximityIntent);
                unregisterReceiver(receiver);
                isAlertRegistered = false;
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStop(){
        super.onStop();

        //공유 프레퍼런스 사용하여 앱 종료 후에도 데이터가 남아있게 함
        SharedPreferences.Editor editor = mSetting.edit();

        //공유 프레퍼런스에 key값으로 값을 저장
        for(int i = 0; i < 3; i++) {
            editor.putString("SName" + i, Name[i]);
            editor.putString("SLatitude" + i, Latitude[i] + "");
            editor.putString("SLongitude" + i, Longitude[i] + "");
            editor.putFloat("SRadius" + i, Radius[i]);
        }
        editor.commit();    //editor.apply();
    }
}
