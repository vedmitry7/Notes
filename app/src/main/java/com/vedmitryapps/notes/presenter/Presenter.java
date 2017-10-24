package com.vedmitryapps.notes.presenter;


public interface Presenter {


    void onCreate();
    void onStop();
    void onPause();
    void onDestroy();

    void onTranslete(String section);
    void onDelete();
    void onSetColor();
    void onArchive();

    void onSetColor(int color);

    void onCreateSection();

}
