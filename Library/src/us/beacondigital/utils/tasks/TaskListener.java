package us.beacondigital.utils.tasks;

public interface TaskListener<TProgress, TResult> {

	void onPreExecute();
	void onProgressUpdate(TProgress... values);
	void onCancelled(TResult result);
	void onPostExecute(TResult result);

}
