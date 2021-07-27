package com.yuqirong.searchbarview;

import com.yuqirong.searchbarview.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

/**
 * MainAbility class.
 */
public class MainAbility extends Ability {

    /**
     * onStart.
     */
    @Override
    public void onStart(final Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }
}
