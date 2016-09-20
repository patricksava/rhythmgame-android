package br.com.psava.rhythmgame.classes;

import android.content.Context;
import android.text.method.MovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import br.com.psava.rhythmgame.interfaces.MovementActionClickListener;

/**
 * Created by patricksava on 19/09/16.
 */

public abstract class MovementAction extends FrameLayout {

    protected int size;
    protected int sequenceNumber;
    protected int sequenceColor;
    protected long timeToShow;

    protected MovementActionClickListener listener;

    public MovementAction(Context c){
        super(c);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getSequenceColor() {
        return sequenceColor;
    }

    public void setSequenceColor(int sequenceColor) {
        this.sequenceColor = sequenceColor;
    }

    public long getTimeToShow() {
        return timeToShow;
    }

    public void setTimeToShow(long timeToShow) {
        this.timeToShow = timeToShow;
    }

    public MovementActionClickListener getListener() {
        return listener;
    }

    public void setListener(MovementActionClickListener listener) {
        this.listener = listener;
    }
}
