package us.beacondigital.utils.tasks;

import android.os.AsyncTask;

public class Task<TParam, TProgress, TResult> extends AsyncTask<TParam, TProgress, TResult> {
	
	protected TaskListener<TProgress, TResult> listener = null;
	
	public Task() { }
	
	public Task(TaskListener<TProgress, TResult> listener) {
		super();
		this.listener = listener;
	}
	
	public void setListener(TaskListener<TProgress, TResult> listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		if(listener != null)
			listener.onPreExecute();
	}

	@Override
	protected TResult doInBackground(TParam... params) {
		return null;
	}
	
	@Override
	protected void onProgressUpdate(TProgress... values) {
		if(listener != null)
			listener.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(TResult result) {
		if(listener != null)
			listener.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled(TResult result) {
		if(listener != null)
			listener.onCancelled(result);
	}
}