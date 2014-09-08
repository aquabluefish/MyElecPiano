package com.example.bluetooth.elecpiano;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

// Bluetooth送受信のクラス
public class BluetoothClient {

	// クラス定数を宣言する
	public static final int MESSAGE_STATECHANGE = 1;
	public static final int MESSAGE_READ = 2;

	// SPPのUUIDをセットする
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final int STATE_NONE = 0;
	public static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;
	private BluetoothAdapter BTadapter;
	private Handler handler;
	private int state;
	private ConnectThread connecting;
	private ConnectedThread transfer;
	
	// コンストラクタによるクラスの初期化
	public BluetoothClient(Context context, Handler handler) {
		this.BTadapter = BluetoothAdapter.getDefaultAdapter();
		this.handler = handler;
		state = STATE_NONE;
	}
	
	// Bluetoothの接続実行同期化処理
	public synchronized void connect(BluetoothDevice device) {
		if (state == STATE_CONNECTING) {
			if (connecting != null) {				// 接続中のとき
				connecting.cancel();				// 一旦クローズする
				connecting = null;
			}
		}
		if (transfer != null) {						// 接続済みのとき
			transfer.cancel();						// 一旦クローズする
			transfer = null;
		}
		connecting = new ConnectThread(device);		// 接続スレッドを生成する
		connecting.start();							// スレッドを開始する
		setState(STATE_CONNECTING);					// 状態を返送する
	}

	// Bluetooth接続処理スレッド
	private class ConnectThread extends Thread {
		private BluetoothDevice BTdevice;
		private BluetoothSocket BTsocket;
		
		// UUIDでリモート端末との接続用ソケットの生成
		public ConnectThread(BluetoothDevice device) {
			try {
				this.BTdevice = device;
				BTsocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			}
			catch (IOException e) {
			}
		}
		
		// 接続実行処理
		public void run() {
			BTadapter.cancelDiscovery();			// 検索処理終了
			try {
				BTsocket.connect();					// 接続中同期化処理へ
			}
			catch (IOException e) {
				setState(STATE_NONE);				// 状態を返送する
				try {
					BTsocket.close();				// エラーならクローズする
				}
				catch (IOException e2) {
				}
				return;
			}
			synchronized (BluetoothClient.this) {	// 同期を取る
				connecting = null;					// ステート初期化
			}
			connected(BTsocket, BTdevice);			// 通信開始同期化処理へ
		}
		
		// Blutooth接続を切り離す処理
		public void cancel() {
			try {
				BTsocket.close();
			}
			catch (IOException e) {
			}
		}
	}
	
	// Bluetooth通信開始同期化処理
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		if (connecting != null) {					// 接続中のとき
			connecting.cancel();					// 一旦クローズする
			connecting = null;
		}
		if (transfer != null) {						// 接続済みのとき
			transfer.cancel();						// 一旦クローズする
			transfer = null;
		}
		transfer = new ConnectedThread(socket);		// 送受信スレッドを生成する
		transfer.start();							// スレッドを開始する
		setState(STATE_CONNECTED);					// 状態を返送する
	}		

	// Bluetooth送受信処理実行スレッド
 	private class ConnectedThread extends Thread {
		private BluetoothSocket BTsocket;
		
		// コンストラクタで初期化処理
		public ConnectedThread(BluetoothSocket bluetoothsocket) {
			this.BTsocket = bluetoothsocket;
		}
		
		//受信待ち，バッファからデータを取り出す処理（最長64バイト受信）
		public void run() {
			byte[] buf = new byte[64];				// 受信バッファを用意する
			byte[] Rcv = new byte[64];				// 取り出しバッファを用意する
			int bytes, i;							// 受信バイト数，各変数を定義
			
			// 受信を繰り返すループ
			while (true) {
				try {
					InputStream input = BTsocket.getInputStream();
					bytes = input.read(buf);		// 受信を実行する
					for(i=0; i<bytes; i++){			// 受信バイト数だけ繰り返す
						Rcv[i]=buf[i];				// バッファをコピーする
					}
					// 受信したデータを返す　Rcvバッファに格納する
					handler.obtainMessage(MESSAGE_READ, 64, -1, Rcv).sendToTarget();
				}
				catch (IOException e) {
					setState(STATE_NONE);			// 状態を返送する
					break;
				}
			}
		}
		
		// 送信処理
		public void write(byte[] buf) {
			try {
				OutputStream output = BTsocket.getOutputStream();
				output.write(buf);					// 送信実行
			}
			catch (IOException e) {
			}
		}

		// クローズ処理
		public void cancel() {
			try {
				BTsocket.close();
			}
			catch (IOException e) {
			}
		}
	}
 	
 	// 送信実行メソッド
	public void write(byte[] out) {
		ConnectedThread transfer;
		synchronized (this) {						// 同期化と初期化
			if (state != STATE_CONNECTED) {
				return;
			}
			transfer = this.transfer;
		}
		transfer.write(out);						// 送信実行
	}
	
	// 状態の通知メソッド
	private synchronized void setState(int state) {
		this.state = state;
		handler.obtainMessage(MESSAGE_STATECHANGE, state, -1).sendToTarget();
	}

	// 状態の取得メソッド
	public synchronized int getState() {
		return state;
	}

	// Bluetoothの切断メソッド
	public synchronized void stop() {
		if (null != connecting) {
			connecting.cancel();
			connecting = null;
		}
		if (null != transfer) {
			transfer.cancel();
			transfer = null;
		}
		setState(STATE_NONE);
	}
}
