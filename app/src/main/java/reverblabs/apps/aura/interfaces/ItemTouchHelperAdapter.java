package reverblabs.apps.aura.interfaces;


public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromposition, int toPosition);
    void onItemDismiss(int position);
}
