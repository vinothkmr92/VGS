package com.pos.app.assist;

import java.io.InputStream;

import com.sewoo.port.PortMediator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class StatusChecker
{
	private static final String TAG = "StatusChecker";
	private static final String key = "status";
	private StatusReader task;
	private Handler handler;
	private InputStream is;
	
	public StatusChecker()
	{
		is = PortMediator.getInstance().getIs();
	}
	
	public void start()
	{
		task = new StatusReader();
		task.setStopFlag(false);
		task.execute();
	}
	
	public void stop()
	{
		task.setStopFlag(true);
		task.cancel(true);
	}
	
	/**
	 * Set the Handler instance to use in this class.
	 * @param handler 
	 */
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	/**
	 * Get the Handler instance.
	 * @return handler
	 */
	public Handler getHandler()
	{
		return handler;
	}
	
	class StatusReader extends AsyncTask<Void, Void, Void>
	{
		private boolean stopFlag;
		
		public void setStopFlag(boolean stop)
		{
			this.stopFlag = stop;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
		}

/**		
	case 0x00: // ���� ����(Error recovery).
	case 0x01: // �μ� ��(Now Printing).
	case 0x02: // ���� ����(Paper Empty).
	case 0x04: // Ŀ�� ����(Cover Open).
	case 0x06: // Ŀ�� ����(Cover Open).
	case 0x08: // Battery Low.
	case 0x10: // �� ��ũ �˻� ����(Black Mark sensor error).
	case 0x20: // �μ� �Ϸ�(Complete Printing).
*/
		
		@Override
		protected Void doInBackground(Void... params)
		{
			byte[] buffer = new byte[4];
			int rin = 0;

			byte[] Totalbuffer = new byte[16];
			int Totalrin = 0;

			Message msg;
			Bundle bundle = new Bundle();
			try
			{
				while(true)
				{	
					if(stopFlag)
					{
						break;
					}
					rin = is.read(buffer);
					if(rin < 4)
					{
						System.arraycopy(buffer, 0, Totalbuffer, 0, rin);
						Thread.sleep(500);
						Totalrin = is.read(buffer);
						System.arraycopy(buffer, 0, Totalbuffer, rin, Totalrin);
						rin = rin + Totalrin;
					} else {
						System.arraycopy(buffer, 0, Totalbuffer, 0, rin);
					}
					if(handler != null)
					{
						// TODO
						msg = Message.obtain();
						msg.what = 0;
						bundle.putByteArray(key, Totalbuffer);
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
					Log.d(TAG,"Read "+Totalbuffer[0]+" "+Totalbuffer[1]+" "+Totalbuffer[2]+" "+Totalbuffer[3]+" "+rin);
					Thread.sleep(1000);
				}
			}
			catch(Exception e)
			{
				handler.sendEmptyMessage(-1);
				Log.e(TAG + e.toString(), e.getMessage());
			}
			return null;
		}
	}
}
