package com.example.bluetooth.elecpiano;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityElecPiano extends Activity {
	
	// クラス定数宣言定義
	public static final int CONNECTDEVICE = 1;
	public static final int ENABLEBLUETOOTH = 2;
	
	// Bluetoothインスタンス定数
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothClient BTclient;
	
	// バッファ定義
	private byte[] ReceivePacket = new byte[64];	// 受信バッファ
	private byte[] TransmitPacket = new byte[64];	// 送信バッファ
	
	//一般変数定義
	private static TextView text0;
	Context context = this;
	
	//最初に実行されるメソッド
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        
        // タイトル表示
        setContentView(R.layout.main);
        setTitle("ＭＹ スマホ鍵盤");  
        setTitleColor(Color.BLACK);
        
        text0=(TextView) findViewById(R.id.text0);
        
        // スイッチイベント組み込み
		findViewById(R.id.select).setOnClickListener((OnClickListener) new SelectExe());
        
		// Bluetooth搭載スマホか確認
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		//Bluetooth搭載情報の取得
		if (mBluetoothAdapter == null) {								//nullならBluetoothが未搭載
			text0.setTextColor(Color.YELLOW);					//文字色　黄色設定
			text0.setText("使用不可");							//Bluetoothの無効表示
		}        

		// 鍵盤のボタン操作
		findViewById(R.id.Button1).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
	    		TransmitPacket[0] = 'A';
				BTclient.write(TransmitPacket);
            }
        });			

		findViewById(R.id.Button2).setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
	    		TransmitPacket[0] = 'B';
				BTclient.write(TransmitPacket);
            }
        });	
		
		findViewById(R.id.Button3).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'C';
				BTclient.write(TransmitPacket);
            }
        });		
		
		findViewById(R.id.Button4).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'D';
				BTclient.write(TransmitPacket);
            }
        });	
		
		findViewById(R.id.Button5).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'E';
				BTclient.write(TransmitPacket);
            }
        });			

		findViewById(R.id.Button6).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'F';
				BTclient.write(TransmitPacket);
            }
        });	
		
		findViewById(R.id.Button7).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'G';
				BTclient.write(TransmitPacket);
            }
        });	
	
		findViewById(R.id.Button8).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'H';
				BTclient.write(TransmitPacket);
            }
        });	
		
		findViewById(R.id.Button9).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'I';
				BTclient.write(TransmitPacket);
            }
        });	
		
		findViewById(R.id.Button10).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'J';
				BTclient.write(TransmitPacket);
            }
        });		
		
		findViewById(R.id.Button11).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'K';
				BTclient.write(TransmitPacket);
            }
        });
		
		findViewById(R.id.Button12).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'L';
				BTclient.write(TransmitPacket);
            }
        });		
		
		findViewById(R.id.Button13).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'M';
				BTclient.write(TransmitPacket);
            }
        });		
		
		findViewById(R.id.Button14).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'N';
				BTclient.write(TransmitPacket);
            }
        });		
		
		findViewById(R.id.Button15).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'O';
				BTclient.write(TransmitPacket);
            }
        });
		
		findViewById(R.id.Button16).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'P';
				BTclient.write(TransmitPacket);
            }
        });		
		
		findViewById(R.id.Button17).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
	    		TransmitPacket[0] = 'Q';
				BTclient.write(TransmitPacket);
            }
        });			
    } 
    
	// アクティビティ開始時（ストップからの復帰時）
	@Override
	public void onStart() {
		super.onStart();
		if (mBluetoothAdapter.isEnabled() == false) {								// Bluetoorh無効時
			Intent BTenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);	// ダイアログ画面にて有効化
			startActivityForResult(BTenable, ENABLEBLUETOOTH);						// ENABLEパラメータ発行
		}
		else {
			if (BTclient == null) {													// Bluetoorh有効時
				BTclient = new BluetoothClient(this, handler);						// クライアントクラスを生成し
			}																		// ハンドラを生成
		}
	}

	// アクティビティ再開時（ポーズからの復帰時）
	@Override
	public synchronized void onResume() {
		super.onResume();		// 特に処理なし
	}

	// アクティビティ破棄時
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (BTclient != null) {
			BTclient.stop();		//Bluetoothクライアント停止
		}
	}

	// 遷移ダイアログからの戻り処理
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		
			case CONNECTDEVICE:														// 端末選択ダイアログからの戻り処理
				if (resultCode == Activity.RESULT_OK) {								// 端末が選択された場合
					String address = data.getExtras().getString(DeviceListActivity.DEVICEADDRESS);
					BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);	// 端末に接続要求
					BTclient.connect(device);										// 端末へ接続
				}
				break;
			case ENABLEBLUETOOTH:										// 有効化ダイアログからの戻り処理
				if (resultCode == Activity.RESULT_OK) {					// Bluetooth有効化の場合
					BTclient = new BluetoothClient(this, handler);		// クライアントクラスを生成し
				}														// ハンドラを生成
				else {													// 無効化の場合メッセージ表示
					Toast.makeText(this, "Bluetooth使用不可", Toast.LENGTH_SHORT).show();
					finish();
				}
		}
	}
	
	//  接続ボタンイベントクラス
    class SelectExe implements OnClickListener{
    	public void onClick(View v){									// デバイス検索と選択ダイアログへ移行 
    		Intent Intent = new Intent(ActivityElecPiano.this, DeviceListActivity.class);
 			startActivityForResult(Intent, CONNECTDEVICE);   			// 端末選択したら接続要求
    	}
    }
    
    // Bluetooth端末の接続処理のハンドラ
	private final Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {				// ハンドルメッセージごとの処理
			switch (msg.what) {									// 戻り値メッセージごとに処理を実行
			case BluetoothClient.MESSAGE_STATECHANGE:
					switch (msg.arg1) {
						case BluetoothClient.STATE_CONNECTED:	//接続完了
							text0.setTextColor(Color.GREEN);	//文字色　緑色設定
							text0.setText("接続完了");			//接続完了のテキスト表示
							break;
						case BluetoothClient.STATE_CONNECTING:	//接続中
							text0.setTextColor(Color.WHITE);	//文字色　白色設定
							text0.setText("接続中…");			//接続中のテキスト表示
							break;
						case BluetoothClient.STATE_NONE:		//接続なし
							text0.setTextColor(Color.RED);		//文字色　赤色設定
							text0.setText("接続失敗");			//接続失敗のテキスト表示
							break;
					}
					break;
			case BluetoothClient.MESSAGE_READ:					// Bluetooth受信処理
					ReceivePacket = (byte[])msg.obj;			// データの受信
					break;										// 受信処理
			}
		}
	}; 
}
