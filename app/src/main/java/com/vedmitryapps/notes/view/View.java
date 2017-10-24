package com.vedmitryapps.notes.view;

import com.vedmitryapps.notes.logic.SuperNote;

public interface View {

    void showFab();
    void hideFab();
    void startActionMode();
    void finishActionMode();
    void updateList(SuperNote notes);


}
