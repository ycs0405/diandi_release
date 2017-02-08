package com.widget.android.interfaces;

public interface OnScrollChangeListener {
    public void ScrollChange(int l, int t, int oldl, int oldt);

    public void topOrbottom(boolean top, boolean bottom);

    /***
     * @param upOrdown true 上拉  false 下拉
     * @param offset
     ***/
    public void offset(boolean upOrdown, float offset, int y);
}
