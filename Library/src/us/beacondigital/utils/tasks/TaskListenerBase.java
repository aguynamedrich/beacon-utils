package us.beacondigital.utils.tasks;

public abstract class TaskListenerBase<TProgress, TResult> implements TaskListener<TProgress, TResult> {
	public void onPreExecute() { /* no op */ }
	public void onProgressUpdate(TProgress... values) { /* no op */ }
	public void onCancelled(TResult result) { /* no op */ }
	public void onPostExecute(TResult result) { /* no op */ }
}