package com.zeyomir.ocfun.view.component;

import com.zeyomir.ocfun.R;

public enum TransactionAnimation {
    SLIDE(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right),
    FADE(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

    private final int enter;
    private final int exit;

    public int getEnter() {
        return enter;
    }

    public int getExit() {
        return exit;
    }

    public int getPopEnter() {
        return popEnter;
    }

    public int getPopExit() {
        return popExit;
    }

    private final int popEnter;
    private final int popExit;

    TransactionAnimation(int enter, int exit, int popEnter, int popExit) {
        this.enter = enter;
        this.exit = exit;
        this.popEnter = popEnter;
        this.popExit = popExit;
    }
}
